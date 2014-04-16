package games;

import trueskill.Rating;

public class SkilledPlayer extends Rating {
	private double realSkill;
	public SkilledPlayer(double realSkill) {
		super();
		this.realSkill = realSkill;
	}

	public double getRealSkill() {
		return realSkill;
	}
	@Override
	public String toString() {
		return "P:"+realSkill;
	}

//	public void setRealSkill(double realSkill) {
//		this.realSkill = realSkill;
//	}
}
