package it.polimi.se2019.model.cards.weapons;

import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class Cyberblade extends OptionalEffect{



	public Cyberblade(String description, List<AmmoType> reloadPrice) {
		super(description, reloadPrice);
		this.standardDamagesAndMarks = new ArrayList<>();
		this.standardDamagesAndMarks.add(new DamageAndMarks(PRIMARY_DAMAGE, PRIMARY_MARKS));
//		this.standardDamagesAndMarks.add(new DamageAndMarks(OPTIONAL2_DAMAGE, OPTIONAL2_MARKS));
//		this.moveDistance = MOVE_DISTANCE;
	}

	public Cyberblade(String description){
		this(description, null);
	}



	@Override
	Pair handlePrimaryFire(int choice) {
		return null;
	}

	@Override
	public List<Player> getPrimaryTargets() {
		return null;
	}

	@Override
	public void optionalEffect1() {

	}

	@Override
	public void optionalEffect2() {

	}

}