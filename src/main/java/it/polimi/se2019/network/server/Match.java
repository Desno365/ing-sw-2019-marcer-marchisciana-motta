package it.polimi.se2019.network.server;

import it.polimi.se2019.controller.Controller;
import it.polimi.se2019.model.Model;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.network.message.GameConfigMessage;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.message.MessageSubtype;
import it.polimi.se2019.network.message.MessageType;
import it.polimi.se2019.utils.GameConstants;
import it.polimi.se2019.utils.Utils;
import it.polimi.se2019.view.server.VirtualView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Match {

	private final int numberOfParticipants;
	private final ArrayList<AbstractConnectionToClient> participants;
	private HashMap<AbstractConnectionToClient, VirtualView> virtualViews = new HashMap<>();
	private boolean matchStarted = false;

	// Game config attributes.
	private HashMap<AbstractConnectionToClient, Integer> skullsChosen = new HashMap<>();
	private HashMap<AbstractConnectionToClient, Integer> mapChosen = new HashMap<>();
	private int numberOfAnswers = 0;


	/**
	 * Create a new match with the specified clients.
	 * @param participants a map that contains all the clients for this match and their nicknames.
	 */
	public Match(List<AbstractConnectionToClient> participants) {
		numberOfParticipants = participants.size();
		if(numberOfParticipants < GameConstants.MIN_PLAYERS || numberOfParticipants > GameConstants.MAX_PLAYERS)
			throw new IllegalArgumentException("The number of participants for this match (" + numberOfParticipants + ") is not valid.");
		this.participants = new ArrayList<>(participants);
	}


	/**
	 * Send game config request messages to the clients, asking skulls and map type.
	 */
	public void requestMatchConfig() {
		for(AbstractConnectionToClient client : participants)
			client.sendMessage(new Message(MessageType.GAME_CONFIG, MessageSubtype.REQUEST));
	}

	// TODO start the match also after a timer and not wait every answer?
	public void addConfigVote(AbstractConnectionToClient client, int skulls, int mapIndex) {
		if(participants.contains(client)) { // Check if the client is in the Match.
			if(!skullsChosen.containsKey(client))
				skullsChosen.put(client, skulls);

			if(!mapChosen.containsKey(client))
				mapChosen.put(client, mapIndex);

			numberOfAnswers++;
			if(numberOfAnswers >= numberOfParticipants)
				startMatch();
		}
	}

	/**
	 * Returns a list with all the participants of this match.
	 * @return a list with all the participants of this match.
	 */
	public List<AbstractConnectionToClient> getParticipants() {
		return new ArrayList<>(participants);
	}

	/**
	 * Returns the VirtualView associated to the client, or null if this match doesn't have the VirtualView of the client.
	 * @param client the client.
	 * @return the VirtualView associated to the client.
	 */
	public VirtualView getVirtualViewOfClient(AbstractConnectionToClient client) {
		return virtualViews.get(client);
	}

	/**
	 * Returns true if the match started.
	 * @return true if the match started.
	 */
	public boolean isMatchStarted() {
		return matchStarted;
	}

	/**
	 * Start the match.
	 */
	private void startMatch() {
		// Find votes.
		int skulls = findVotedNumberOfSkulls();
		GameConstants.MapType mapType = findVotedMap();
		Utils.logInfo("Starting a new match with skulls: " + skulls + ", mapName: \"" + mapType.getMapName() + "\".");

		// Send messages with votes.
		for(AbstractConnectionToClient client : participants) {
			GameConfigMessage gameConfigMessage = new GameConfigMessage(MessageSubtype.OK);
			gameConfigMessage.setSkulls(skulls);
			gameConfigMessage.setMapIndex(mapType.ordinal());
			client.sendMessage(gameConfigMessage);
		}

		// Create list of player names.
		List<String> playerNames = participants.stream().map(AbstractConnectionToClient::getNickname).collect(Collectors.toList());

		// Create Model and Controller
		Model model = new Model(mapType.getMapName(), playerNames, skulls);
		Controller controller = new Controller(model);

		// Add VirtualView's observers to the model. (VirtualView -👀-> Model)
		for (AbstractConnectionToClient client : participants) {
			Utils.logInfo("Added Virtual View to " + client.getNickname());
			VirtualView virtualView =  new VirtualView(client, client.getNickname());
			virtualViews.put(client, virtualView);
			model.getGameBoard().addObserver(virtualView.getGameBoardObserver());
			Utils.logInfo(client.getNickname() + " now observes Game Board");
			model.getGameBoard().getGameMap().addObserver(virtualView.getGameMapObserver());
			Utils.logInfo(client.getNickname() + " now observes Game Map");
			for (Player player : model.getPlayers()) {
				player.addObserver(virtualView.getPlayerObserver());
				Utils.logInfo(client.getNickname() + " now observes " + player.getPlayerName());
			}
		}

		// Start the game.
		controller.startGame();
		matchStarted = true;
	}

	/**
	 * Calculates the average of voted skulls.
	 * @return the average of voted skulls.
	 */
	private int findVotedNumberOfSkulls() {
		float average = 0f;
		for(Integer votedSkulls : skullsChosen.values())
			average += votedSkulls;
		return  Math.round(average / skullsChosen.values().size());
	}

	/**
	 * Find the most voted map, if votes are tied it chooses the smaller map.
	 * @return the map name of the most voted map.
	 */
	private GameConstants.MapType findVotedMap() {
		// Create array of votes.
		int[] votes = new int[GameConstants.MapType.values().length];

		// Initialize array with all zeros.
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		// Add votes.
		for(Integer votedMap : mapChosen.values())
			votes[votedMap]++;

		// search for max.
		int indexOfMax = 0;
		for(int i = 1; i < votes.length; i++) {
			if (votes[i] > votes[indexOfMax])
				indexOfMax = i;
		}

		// Return corresponding map.
		return GameConstants.MapType.values()[indexOfMax];
	}

}
