package trueskill;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class RatingTest {

	@Test
	public void skill() {
		Rating r1 = new Rating();
		assertEquals(0, r1.getSkill(), 0.001);
		
		Rating r2 = new Rating(21, 6);
		assertEquals(3, r2.getSkill(), 0.001);
	}
	
	@Test
	public void sorting(){
		Rating r1 = new Rating();
		Rating r2 = new Rating(21, 6);
		Rating r3 = new Rating(25, 3);
		Rating r4 = new Rating(16, 4);
		
		List<Rating> unsorted = new ArrayList<>(Arrays.asList(r1, r2, r3, r4));
		Collections.shuffle(unsorted);
		Collections.sort(unsorted);
		
		assertEquals(Arrays.asList(r3, r4, r2, r1), unsorted);
	}

}
