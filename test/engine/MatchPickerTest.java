package engine;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import engine.MatchPicker;
import engine.MatchType;

import trueskill.Rating;

public class MatchPickerTest {

	private final Rating r1 = new Rating();
	private final Rating r2 = new Rating(22, 1);
	private final Rating r3 = new Rating(24, 2);
	private final Rating r4 = new Rating(26, 3);
	private final Rating r5 = new Rating(28, 4);
	
	private final MatchPicker<Rating> picker = new MatchPicker<>();
	
	@Test
	public void testFFA234() {
		Map<Rating, Integer> playedMatchs = new HashMap<Rating, Integer>();
		playedMatchs.put(r1, 0);
		playedMatchs.put(r2, 3);
		playedMatchs.put(r3, 5);
		playedMatchs.put(r4, 2);
		playedMatchs.put(r5, 7);
		
		assertEquals(Arrays.asList(toList(r1), toList(r4)), picker.pickMatch(MatchType.FFA2, playedMatchs));
		assertEquals(Arrays.asList(toList(r1), toList(r4), toList(r2)), picker.pickMatch(MatchType.FFA3, playedMatchs));
		assertEquals(Arrays.asList(toList(r1), toList(r4), toList(r2), toList(r3)), picker.pickMatch(MatchType.FFA4, playedMatchs));
	}

	private List<Rating> toList(Rating r){
		return Arrays.asList(r);
	}
}
