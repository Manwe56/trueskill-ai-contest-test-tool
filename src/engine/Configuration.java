package engine;

public class Configuration {
	private double sigmaStabilization = 1.5;
	private int rankNumberOfStableLeaderBoard = 100;
	
	public Configuration(){
		
	}
	
	public Configuration(Configuration other) {
		sigmaStabilization = other.sigmaStabilization;
		rankNumberOfStableLeaderBoard = other.rankNumberOfStableLeaderBoard;
	}
	public double getSigmaStabilization() {
		return sigmaStabilization;
	}
	public void setSigmaStabilization(double sigmaStabilization) {
		this.sigmaStabilization = sigmaStabilization;
	}
	public int getRankNumberOfStableLeaderBoard() {
		return rankNumberOfStableLeaderBoard;
	}
	public void setRankNumberOfStableLeaderBoard(int rankNumberOfStableLeaderBoard) {
		this.rankNumberOfStableLeaderBoard = rankNumberOfStableLeaderBoard;
	}
}
