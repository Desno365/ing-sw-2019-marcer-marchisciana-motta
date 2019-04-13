package it.polimi.se2019.model.player.damagestatus;

import it.polimi.se2019.utils.MacroAction;
import it.polimi.se2019.utils.MacroActionBuilder;

import java.util.ArrayList;

import static it.polimi.se2019.utils.GameConstants.NUMBER_OF_ACTIONS_PER_TURN;

public class FrenzyBefore implements DamageStatus {
	public final int numberOfActions = NUMBER_OF_ACTIONS_PER_TURN;
	private ArrayList<MacroAction> availableActions;

	public FrenzyBefore(){
		MacroActionBuilder shootPeopleBuilder = new MacroActionBuilder();
		MacroActionBuilder runAroundBuilder = new MacroActionBuilder();
		MacroActionBuilder grabStuffBuilder = new MacroActionBuilder();
		availableActions = new ArrayList<>();

		shootPeopleBuilder.setMovementDistance(1);
		shootPeopleBuilder.setReloadAction(true);
		shootPeopleBuilder.setShootAction(true);
		availableActions.add(shootPeopleBuilder.build());

		runAroundBuilder.setMovementDistance(4);
		availableActions.add(runAroundBuilder.build());

		grabStuffBuilder.setMovementDistance(2);
		grabStuffBuilder.setGrabAction(true);
		availableActions.add(grabStuffBuilder.build());
	}

	@Override
	public ArrayList<MacroAction> getAvailableActions() {
		return (ArrayList<MacroAction>) availableActions.clone();
	}

	@Override
	public void doAction() {
	}

}