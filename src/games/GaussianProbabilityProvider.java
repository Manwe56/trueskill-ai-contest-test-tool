package games;

import games.ProbabilisticGame.ProbabilityProvider;

public class GaussianProbabilityProvider implements ProbabilityProvider<SkilledPlayer> {
	private double erf(double z) {
	    double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

	    // use Horner's method
	    double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
	                                        t * ( 1.00002368 +
	                                        t * ( 0.37409196 + 
	                                        t * ( 0.09678418 + 
	                                        t * (-0.18628806 + 
	                                        t * ( 0.27886807 + 
	                                        t * (-1.13520398 + 
	                                        t * ( 1.48851587 + 
	                                        t * (-0.82215223 + 
	                                        t * ( 0.17087277))))))))));
	    if (z >= 0) return  ans;
	    else        return -ans;
	}

	private double probabilityToWinForSkill1(double skill1, double skill2){
		double sigma = Math.sqrt(25);
		return (1+erf((skill1-skill2)/(sigma*Math.sqrt(2))))/2;
	}

	@Override
	public double chancesToWinForPlayer1(SkilledPlayer player1, SkilledPlayer player2) {
		return probabilityToWinForSkill1(player1.getRealSkill(), player2.getRealSkill());
	}
}
