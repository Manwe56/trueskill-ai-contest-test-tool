package games;

import games.ProbabilisticGame.ProbabilityProvider;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticMatrixProbabilityProvider implements ProbabilityProvider<NamedPlayer>{
	private final Map<NamedPlayer, Map<NamedPlayer, Double>> probabilities = new HashMap<>();
		
	public StaticMatrixProbabilityProvider(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			List<NamedPlayer> playersOrderedAsInMatrix = new ArrayList<>();
			Map<String, NamedPlayer> playersByName = new HashMap<>();
			String line;
			while ((line = reader.readLine())!=null){
				if (probabilities.isEmpty()){
					//First line, we expect to get the names of the players here
					String[] playerNames = line.split(";");
					for (int i=1; i<playerNames.length; i++){
						NamedPlayer newPlayer = new NamedPlayer(playerNames[i].trim());
						playersByName.put(playerNames[i].trim(), newPlayer);
						playersOrderedAsInMatrix.add(newPlayer);
						probabilities.put(newPlayer, new HashMap<NamedPlayer, Double>());
					}
				}
				else{
					//Other line, we expect to get the names of the players first, then the scores
					String[] probabilitiesValues = line.split(";");
					NamedPlayer linePlayer = playersByName.get(probabilitiesValues[0]);
					
					for (int i=1; i<probabilitiesValues.length; i++){
						probabilities.get(linePlayer).put(playersOrderedAsInMatrix.get(i-1), Double.valueOf(probabilitiesValues[i].trim()));
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Probability file not found. Writing a sample file to "+fileName);
			try {
				FileOutputStream outStream = new FileOutputStream(fileName, false);
				String sample = "\t;P1;P2;P3\n"+
								"P1;0.0;1.0;0.5\n"+
								"P2;0.0;0.0;0.8\n"+
								"P3;0.5;0.2;0.0\n";
				outStream.write(sample.getBytes());
				outStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public List<NamedPlayer> getPlayers(){
		return new ArrayList<>(probabilities.keySet());
	}

	@Override
	public double chancesToWinForPlayer1(NamedPlayer player1, NamedPlayer player2) {
		return probabilities.get(player1).get(player2);
	}
}
