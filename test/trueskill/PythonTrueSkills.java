package trueskill;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PythonTrueSkills {

	ITrueSkill trueSkill = TrueSkillFactory.instance();
	
	@Test
	public void testOneVsOne() {
		Rating r1 = new Rating();
		Rating r2 = new Rating();
		List<List<? extends Rating>> teams = Arrays.asList(toList(r1), toList(r2));
		assertEquals(0.447, trueSkill.quality(teams), 0.001);
		trueSkill.rate(teams, Arrays.asList(0,1));
		assertEquals(29.396, r1.getMu(), 0.001);
		assertEquals(7.171, r1.getSigma(), 0.001);
		assertEquals(20.604, r2.getMu(), 0.001);
		assertEquals(7.171, r2.getSigma(), 0.001);
	}
	
	@Test
	public void testTwoVsOne() {
		Rating r1 = new Rating();
		Rating r2 = new Rating();
		Rating r3 = new Rating();
		List<List<? extends Rating>> teams = Arrays.asList(toList(r1), toList(r2, r3));
		assertEquals(0.135, trueSkill.quality(teams), 0.001);
		trueSkill.rate(teams, Arrays.asList(0,1));
		assertEquals(33.731, r1.getMu(), 0.001);
		assertEquals(7.317, r1.getSigma(), 0.001);
		assertEquals(16.269, r2.getMu(), 0.001);
		assertEquals(7.317, r2.getSigma(), 0.001);
		assertEquals(r2, r3);
	}

	List<? extends Rating> toList(Rating... ratings){
		return Arrays.asList(ratings);
	}
}
