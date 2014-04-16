package trueskill;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TrueSkillTest {

	ITrueSkill trueSkill = TrueSkillFactory.instance();
	
	@Test
	public void testOneVsOne() {
		Rating r1 = new Rating();
		Rating r2 = new Rating();
		assertEquals(0.447, qualityFFA(r1, r2), 0.001);
		rateFFA(r1, r2);
		assertEquals(29.396, r1.getMu(), 0.001);
		assertEquals(7.171, r1.getSigma(), 0.001);
		assertEquals(20.604, r2.getMu(), 0.001);
		assertEquals(7.171, r2.getSigma(), 0.001);
	}
	
	@Test
	public void testThreeFFAWithDraw() {
		Rating r1 = new Rating();
		Rating r2 = new Rating();
		Rating r3 = new Rating();
		assertEquals(0.200, qualityFFA(r1, r2, r3), 0.001);
		rateFFA(Arrays.asList(0, 1, 1), r1, r2, r3);
		assertEquals(30.109, r1.getMu(), 0.001);
		assertEquals(6.735, r1.getSigma(), 0.001);
		assertEquals(22.44, r2.getMu(), 0.01);
		assertEquals(5.97, r2.getSigma(), 0.01);
		assertEquals(22.44, r3.getMu(), 0.01);
		assertEquals(5.97, r3.getSigma(), 0.01);
	}
	
	private double qualityFFA(Rating... ratings) {
		List<List<? extends Rating>> teams = new ArrayList<>();
		for (Rating rating : ratings){
			teams.add(Arrays.asList(rating));
		}
		return trueSkill.quality(teams);
	}

	//from winner to last
	private void rateFFA(Rating... ratings) {
		List<List<? extends Rating>> teams = new ArrayList<>();
		List<Integer> ranks = new ArrayList<>();
		int rank = 0;
		for (Rating rating : ratings){
			teams.add(Arrays.asList(rating));
			ranks.add(rank++);
		}
		trueSkill.rate(teams, ranks);
	}

	private void rateFFA(List<Integer> ranks, Rating... ratings) {
		List<List<? extends Rating>> teams = new ArrayList<>();
		for (Rating rating : ratings){
			teams.add(Arrays.asList(rating));
		}
		trueSkill.rate(teams, ranks);
	}
}
