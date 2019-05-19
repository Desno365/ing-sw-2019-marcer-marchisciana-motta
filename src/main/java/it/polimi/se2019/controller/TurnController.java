package it.polimi.se2019.controller;

import it.polimi.se2019.model.Model;
import it.polimi.se2019.model.gamemap.Coordinates;
import it.polimi.se2019.network.message.DefaultActionMessage;
import it.polimi.se2019.network.message.IntMessage;
import it.polimi.se2019.network.message.MoveActionMessage;
import it.polimi.se2019.utils.ActionType;
import it.polimi.se2019.utils.Pair;
import it.polimi.se2019.utils.Utils;
import it.polimi.se2019.view.server.Event;
import it.polimi.se2019.view.server.VirtualView;

import java.util.List;

/**
 * This class is in a lower level than GameController. It handles the logic relative
 * @author Marchingegno
 */
public class TurnController{

	private VirtualViewsContainer virtualViewsContainer;
	private WeaponController weaponController;
	private Model model;


	public TurnController(Model model, VirtualViewsContainer virtualViewsContainer) {
		this.virtualViewsContainer = virtualViewsContainer;
		this.model = model;
		this.weaponController = new WeaponController();
	}


	void processEvent(Event event) {
		//TODO: Control veridicity of the message.

		VirtualView virtualView = event.getVirtualView();
		String playerName = virtualView.getNickname();

		Utils.logInfo("TurnController: processing this event " + event.toString());
		switch(event.getMessage().getMessageType()){
			case ACTION:
				model.setNextMacroAction(playerName, ((DefaultActionMessage)event.getMessage()).getContent());
				handleNextAction(virtualView);
				break;
			case ON_TURN_POWERUP:
				int powerupIndex = ((IntMessage)event.getMessage()).getContent();
				if(model.canOnTurnPowerupBeActivated(playerName, powerupIndex)) {
					// TODO activate powerup
					Utils.logWarning("TODO: ACTIVATE POWERUP, LAUNCHING ACTION/END PLACEHOLDER.");
					handleEnd(virtualView);
				}
				break;
			case GRAB_AMMO:
				model.grabAmmoCard(playerName, ((DefaultActionMessage)event.getMessage()).getContent());
				handleNextAction(virtualView);
				virtualViewsContainer.sendUpdatedReps();
				break;
			case GRAB_WEAPON:
				model.grabWeaponCard(playerName, ((DefaultActionMessage)event.getMessage()).getContent());
				handleNextAction(virtualView);
				virtualViewsContainer.sendUpdatedReps();
				break;
			case MOVE:
				Coordinates playerChoice = ((MoveActionMessage) event.getMessage()).getCoordinates().get(0);
				if (model.getReachableCoordinatesOfTheCurrentPlayer().contains(playerChoice)) {
					model.movePlayerTo(playerName, playerChoice);
					handleNextAction(virtualView);
					virtualViewsContainer.sendUpdatedReps();
				} else {
					virtualView.askMove(model.getReachableCoordinatesOfTheCurrentPlayer());
				}
				break;
			case RELOAD:
				model.reloadWeapon(playerName, ((DefaultActionMessage)event.getMessage()).getContent());
				handleNextAction(virtualView);
				virtualViewsContainer.sendUpdatedReps();
				break;
			case WEAPON:
				//TODO fix this warning
				Pair<String, List<String>> stringListString = model.playerWeaponHandleFire(playerName, ((DefaultActionMessage)event.getMessage()).getContent());
				if(model.isTheplayerDoneFiring(playerName)){
					model.resetPlayerCurrentWeapon(playerName);
					handleNextAction(virtualView);
				}
				else {
					virtualView.askChoice(stringListString.getFirst(), stringListString.getSecond());
				}
				break;
			default: Utils.logError("Received wrong type of message: " + event.toString(), new IllegalStateException());
		}

	}

	private void handleNextAction(VirtualView playerVirtualView) {
		ActionType actionType = model.getNextActionToExecute(playerVirtualView.getNickname());
		switch (actionType){
			case MOVE:
				if (model.getCurrentPlayer().getDamageStatus().getCurrentMacroAction().isGrab())
					playerVirtualView.askMove(model.getEmptyReachableCoordinatesOfTheCurrentPlayer());
				else
					playerVirtualView.askMove(model.getReachableCoordinatesOfTheCurrentPlayer());
				break;
			case GRAB:
				playerVirtualView.askGrab();
				break;
			case RELOAD:
				playerVirtualView.askReload();
				break;
			case SHOOT:
				playerVirtualView.askShoot();
				break;
			case END:
				//The MacroAction is already refilled.
				handleEnd(playerVirtualView);
				break;
			default:
				Utils.logError("This action type cannot be processed.", new IllegalStateException());
				break;
		}
	}

	private void handleEnd(VirtualView playerVirtualView) {
		String playerName = playerVirtualView.getNickname();
		if(model.doesThePlayerHaveActionsLeft(playerName)){
			playerVirtualView.askAction(model.getActivableOnTurnPowerups(playerName));
		} else {
			playerVirtualView.askEnd(model.getActivableOnTurnPowerups(playerName));
		}
	}
}