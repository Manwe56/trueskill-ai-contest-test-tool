package games;

import trueskill.Rating;

public class NamedPlayer extends Rating{

	private final String playerName;
	
	public NamedPlayer(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public String toString() {
		return playerName;
	}
	
}
