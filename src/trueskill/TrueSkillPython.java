package trueskill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

class TrueSkillPython implements ITrueSkill {

	private final PythonInterpreter interpreter = new PythonInterpreter();
	
	public TrueSkillPython(){
		importModules();
	}
	
	private void importModules() {
		interpreter.exec("import sys");
		interpreter.exec("sys.path.append('trueskill')");
		interpreter.exec("from trueskill import *");
	}

	@Override
	public double quality(List<List<? extends Rating>> teams) {
		declareTeams(teams);
		PyObject quality = interpreter.eval("quality(teams)");
		return quality.asDouble();
	}

	private Map<Rating, PyObject> declareTeams(List<List<? extends Rating>> teams) {
		Map<Rating, PyObject> ratings=new HashMap<Rating, PyObject>();
		int index=0;
		interpreter.exec("teams = []");
		for (List<? extends Rating> team : teams){
			interpreter.exec("team = []");
			for (Rating rating : team){
				String ratingName = "rating"+index; 
				interpreter.exec(ratingName+" = Rating("+rating.getMu()+","+rating.getSigma()+")");
				index++;
				ratings.put(rating, interpreter.get(ratingName));
				interpreter.exec("team.append("+ratingName+")");
			}
			interpreter.exec("teams.append(team)");
		}
		return ratings;
	}

	@Override
	public void rate(List<List<? extends Rating>> teams, List<Integer> ranks) {
		declareTeams(teams);
		declareRanks(ranks);
		PyObject rate = interpreter.eval("rate(teams, ranks)");
		updateRatings(teams, rate);
	}

	private void updateRatings(List<List<? extends Rating>> teams, PyObject rate) {
		int teamIndex=0;
		for (List<? extends Rating> team : teams){
			PyObject teamRatings = rate.__getitem__(teamIndex);
			int ratingIndex=0;
			for (Rating rating : team){
				updateRating(rating, teamRatings.__getitem__(ratingIndex));	
				ratingIndex++;
			}
			teamIndex++;
		}
	}

	private void updateRating(Rating rating, PyObject pyObject) {
		rating.setMu(pyObject.__getattr__("mu").asDouble());
		rating.setSigma(pyObject.__getattr__("sigma").asDouble());
	}

	private void declareRanks(List<Integer> ranks) {
		interpreter.exec("ranks = []");
		for (int rank : ranks){
			interpreter.exec("ranks.append("+rank+")");
		}
	}

}
