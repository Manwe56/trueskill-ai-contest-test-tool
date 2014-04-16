package trueskill;

import java.util.HashMap;
import java.util.Map;

import engine.MatchType;

public class Rating implements Comparable<Rating>{
	private double mu;
	private double sigma;
	private final Map<MatchType, Rating> typedRatings = new HashMap<>();
	
	public Rating(double mu, double sigma) {
		super();
		this.mu = mu;
		this.sigma = sigma;
	}
	
	@Override
	public String toString() {
		return "Rating [mu=" + mu + ", sigma=" + sigma + "]";
	}

	public Rating(){
		resetSkill();
	}
	
	public void resetSkill(){
		mu=25;
		sigma=mu/3;
	}
	
	public double getSkill(){
		return mu-3*sigma;
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public double getMu() {
		return mu;
	}

	public void setMu(double mu) {
		this.mu = mu;
	}

	@Override
	public int compareTo(Rating o) {
		double skill = getSkill();
		double oSkill = o.getSkill();
		
		if (skill>oSkill)
			return -1;
		if (skill<oSkill)
			return 1;
		return 0;
	}
	
	public Rating typedRating(MatchType type){
		if (!typedRatings.containsKey(type)){
			typedRatings.put(type, new Rating());
		}
		return typedRatings.get(type);
	}
}
