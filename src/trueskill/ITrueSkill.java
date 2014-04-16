package trueskill;

import java.util.List;

public interface ITrueSkill {
	double quality(List<List<? extends Rating>> teams);

	void rate(List<List<? extends Rating>> teams, List<Integer> ranks);
}
