package it.polimi.se2019.model.cards.weapons;

import com.google.gson.JsonObject;
import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.utils.QuestionContainer;

import java.util.List;

public class GrenadeLauncher extends OptionalEffectsWeapon {

	public GrenadeLauncher(String description, List<AmmoType> reloadPrice) {
		super("Grenade Launcher", description, reloadPrice, 0, 0, 0);
	}

	public GrenadeLauncher(JsonObject parameters) {
		super(parameters);
	}


	public void primaryFire() {
	}

	@Override
	QuestionContainer handlePrimaryFire(int choice) {
		return null;
	}

	@Override
	public List<Player> getPrimaryTargets() {
		return null;
	}

	@Override
	public void optional1Fire() {

	}

	@Override
	public void optional2Fire() {

	}

}