trueskill-ai-contest-test-tool
==============================
@Author: Grégory Ribéron aka Manwe

Ranking Artificial intelligence codes is not an easy task. Trueskill has been often used to achieve this task because it is efficient quickly, and can rank players to games that might be played by more than two players simultaneously. But in the contests I did, I noticed that the number of matchs even if it is important , does not give a stability on the rankings. For sure, the best players are far away from the noobies, but if you take two players with a skill difference very low, you will notice that the rankings will change between them continuously. Their rank will only depend on the final cut that will be done.
After analysing the trueskill mechanism, I noted that the past matchs will tend to get less and less weight in the final score of the AI. As AI skills don't change, I found it sad that the number of matchs wouldn't give more precisions on the skill of the AI. 
To enhance this behavior, the best results I have so far is to use the following steps: run matchs with trueskill until all the sigma (trueskill confidence in its score) of each player are under a fixed limit. Then consider the trueskill score of a player is the mean of all the trueskill scores where the sigma is under the previous fixed limit. When the rankings did not changed during several match played, consider we get the correct leaderboard and stop to run matches.

As determining the real skill of an AI is not straight forward, I prefered to test trueskill on simulated games where the real skills of the players are fixed and known in advance. I wrote this tool so you can test what trueSkill would output as rankings after a number of matches, and what the steps above would also produce.

In the top part of the Application, you will be able to select the kind of game you want to simulate. In the main part, three graphs are displayed:
"Scores" contains the trueskill scores of the players when they play matches. It is the unmodified score of what trueskill produce
"Means" contains the trueskill score means of the players when they play matches. The mean is done only on trueskill scores where all the sigma of each players is under a fixed limit
"Sigma" draws the mean and max values of the trueskill players. You will find also the limit here
On the right of the main part, you will find the current ranking of the "Means" tab. 

For now three games are available. If the tool might be used on real games, its objective is first to test some situations. That's why here the three available games are not real games, but we only fix some probabilities that a player wins a match, and then when a "match" is played, we randomly declare a winner taking into account the given probabilities.
"Exact simulated game" is a game where the player with the greater skill always wins.
"Gaussian simulated game" is a game where each player as a fixed skill corresponding to its number. Then the probability of winning is the same as what trueskill should produce as score difference (a difference of 0 is a 50% chance to win, a difference of 25/6 is a 20% chance of win for the weakest player).
"Probabilities from file game" is a game where all the probabilities to win are taken from the csv file PlayersMatchProbabilities.csv. This file must contains a square matrix, where the first line contains all the player names. Then on each line, the first column is the name of the AI, and the number correspond to the probability of winning when the AI is opposed to the player of the column. An example file is written if no file exists

How to use it? Select a game from the combobox, then click either on the button "play until rank stabilized" or "play 100 matchs for each player". In the first case match results will be given to trueskill until the mean leaderboard is stable for a fixed number of matchs. In the second case, you will just play 100 matchs. You can change some configuration parameters in the Edit configuration link. Sigma limit is the sigma minimum to take into account the scores of the players for the Mean graphs. The Number of matchs represent the number of match played with a stable leaderboard to consider the leaderboard as final. Both parameters will be useful only if you use the Play until rank stabilized button.

The trueskill implementation I chose is the one of trueskill.org in python. You will need to add a Jython jar (tested with standalone version 2.5.3)  in the path because the python code is executed directly in the java application. 

It requires At least Java 7 to run the application, its UI has been done with JavaFX

Enjoy!
Manwe

