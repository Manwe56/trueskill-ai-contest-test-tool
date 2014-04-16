package trueskill;

public class TrueSkillFactory {
	public static ITrueSkill instance(){
		return new TrueSkillPython();
	}
}
