package ui.threading;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

import javax.swing.SwingUtilities;

public class ThreadingUtilities {
	private static interface Queue {
		void add(Runnable runnable);
	}

	private static class QueueDispatcher {
		private final Queue queue;

		public QueueDispatcher(Queue queue) {
			this.queue = queue;
		}
		@SuppressWarnings("unchecked")
		public <T, S extends T> T wrap(Class<T> class1, S implementor) {
			return (T) Proxy.newProxyInstance(QueueDispatcher.class.getClassLoader(), new Class[] { class1 }, new DynamicProxyQueueDispatcher<S>(queue,
					implementor));
		}

		private static <T> T runWithEnsuredMethodAccessibility(final AccessibleObject accessibleObject, Callable<T> callable) throws Exception {
			boolean lastAccessibility = accessibleObject.isAccessible();
			if (!lastAccessibility) {
				setAccessible(accessibleObject, true);
				try {
					return callable.call();
				} finally {
					setAccessible(accessibleObject, false);
				}
			} else {
				return callable.call();
			}
		}

		private static void setAccessible(final AccessibleObject accessibleObject, final boolean accessibility) {
			java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
				@Override
				public Void run() {
					accessibleObject.setAccessible(accessibility);
					return null;
				}
			});
		}

		private static Method lookupMethod(String methodName, Class<? extends Object> class1) throws NoSuchFieldException, NoSuchMethodException {
			if (class1 == Object.class)
				throw new NoSuchMethodException("cannot find method" + methodName);

			Method[] methods = class1.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					return method;
				}
			}
			return lookupMethod(methodName, class1.getSuperclass());
		}

		private static class DynamicProxyQueueDispatcher<S> implements InvocationHandler {
			private final Queue queue;
			private final S implementor;

			public DynamicProxyQueueDispatcher(Queue queue, S implementor) {
				this.queue = queue;
				this.implementor = implementor;
			}

			@Override
			public Object invoke(Object target, final Method method, final Object[] arguments) throws Throwable {

				queue.add(new Runnable() {
					@Override
					public void run() {
						try {
							final Method targetMethod = lookupMethod(method.getName(), implementor.getClass());
							runWithEnsuredMethodAccessibility(targetMethod, new Callable<Object>() {

								@Override
								public Object call() throws Exception {
									targetMethod.invoke(implementor, arguments);
									return null;
								}
							});
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
				return null;
			}
		}
	}

	private static class EventQueue implements Queue {

		private static EventQueue singletonEventQueue = new EventQueue();

		private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		private Thread executionThread = null;

		protected EventQueue(String queueName) {
			executionThread = new Thread(new ExecutionTask());
			executionThread.setName(queueName);
			executionThread.start();
		}

		private EventQueue() {
			this("EventQueue");
		}

		public static EventQueue singleton() {
			return singletonEventQueue;
		}

		@Override
		public void add(Runnable newTask) {
			try {
				synchronized (this) {
					notifyAll();
				}
				queue.put(newTask);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private class ExecutionTask implements Runnable {

			@Override
			public void run() {
				while (true) {
					try {
						Runnable task = queue.poll(10, TimeUnit.MILLISECONDS);
						if (task != null) {
							try {
								task.run();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}

	}

	private static final Queue SWING = new Queue() {
		@Override
		public void add(Runnable runnable) {
			SwingUtilities.invokeLater(runnable);
		}
	};

	private static final Queue JAVAFX = new Queue() {
		@Override
		public void add(Runnable runnable) {
			Platform.runLater(runnable);
		}
	};

	private static final QueueDispatcher swingDispatcher = new QueueDispatcher(SWING);
	private static final QueueDispatcher fxDispatcher = new QueueDispatcher(JAVAFX);
	private static final QueueDispatcher eventQueueDispatcher = new QueueDispatcher(EventQueue.singleton());

	public static <T, S extends T> T runInSwing(Class<T> class1, S implementor) {
		return swingDispatcher.wrap(class1, implementor);
	}

	public static <T, S extends T> T runInEventQueue(Class<T> class1, S implementor) {
		return eventQueueDispatcher.wrap(class1, implementor);
	}

	public static <T, S extends T> T runInFX(Class<T> class1, S implementor) {
		return fxDispatcher.wrap(class1, implementor);
	}
}
