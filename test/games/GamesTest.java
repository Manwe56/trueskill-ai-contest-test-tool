package games;

import static org.junit.Assert.*;

import org.junit.Test;

public class GamesTest {

	@Test
	public void gaussianEvaluation() {
		GaussianProbabilityProvider probs = new GaussianProbabilityProvider();
		assertEquals(0.2, probs.chancesToWinForPlayer1(new SkilledPlayer(10), new SkilledPlayer(10+25.0/6)), 0.01);
		assertEquals(0.42, probs.chancesToWinForPlayer1(new SkilledPlayer(10), new SkilledPlayer(10+1)), 0.01);
		assertEquals(0.5, probs.chancesToWinForPlayer1(new SkilledPlayer(10), new SkilledPlayer(10)), 0.01);
		assertEquals(0.8, probs.chancesToWinForPlayer1(new SkilledPlayer(10+25.0/6), new SkilledPlayer(10)), 0.01);
	}

}
