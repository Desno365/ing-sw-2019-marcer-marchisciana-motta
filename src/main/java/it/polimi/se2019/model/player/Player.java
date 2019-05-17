package it.polimi.se2019.model.player;

import it.polimi.se2019.model.Representable;
import it.polimi.se2019.model.Representation;
import it.polimi.se2019.model.cards.weapons.WeaponCard;
import it.polimi.se2019.model.player.damagestatus.DamageStatus;
import it.polimi.se2019.model.player.damagestatus.LowDamage;
import it.polimi.se2019.utils.Color;
import it.polimi.se2019.utils.MacroAction;
import it.polimi.se2019.utils.Utils;

import java.util.List;
import java.util.Observable;

/**
 * Class that implements the player.
 *
 * @author Desno365
 * @author MarcerAndrea
 * @author Marchingegno
 */
public class Player extends Observable implements Representable {

	private TurnStatus turnStatus;
	private boolean actionRequested; //If the player is already executing an action.
	private String playerName;
	private int playerID;
	private Color.CharacterColorType playerColor;
	private PlayerBoard playerBoard;
	private DamageStatus damageStatus;
	private PlayerRep playerRep;
	private int firingWeapon; //The current weapon that the player is firing with.


	public Player(String playerName, int playerID) {
		this.playerName = playerName;
		this.playerID = playerID;
		this.playerColor = Color.CharacterColorType.values()[playerID + 1]; // + 1 to avoid BLACK color
		playerBoard = new PlayerBoard();
		this.damageStatus = new LowDamage();
		this.turnStatus = TurnStatus.PRE_SPAWN;
		this.firingWeapon = -1;
		setChanged();
	}

	public int getIndexOfFiringWeapon() {
		return firingWeapon;
	}

	public boolean isActionRequested() {
		return actionRequested;
	}

	/**
	 * Returns the player's board.
	 * @return the player's board.
	 */
	public PlayerBoard getPlayerBoard() {
		return playerBoard;
	}

	/**
	 * Returns the player name.
	 * @return the player name.
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Returns the player ID.
	 * @return the player ID.
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Returns the player color.
	 * @return the player color.
	 */
	public Color.CharacterColorType getPlayerColor() {
		return playerColor;
	}

	/**
	 * Returns the player damage status.
	 * @return the player damage status.
	 */
	public DamageStatus getDamageStatus() {
		return damageStatus;
	}

	/**
	 * Sets the player damage status.
	 * @param newDamageStatus damage status to set to the player.
	 */
	public void setDamageStatus(DamageStatus newDamageStatus) {
		damageStatus = newDamageStatus;
		Utils.logInfo("Player -> setdamageStatus(): " + playerName + "'s damage status set to " + newDamageStatus);
		setChanged();
	}

	/**
	 * Sets the action requested parameter.
	 * @param bool the parameter to set to the player.
	 */
	public void setActionRequested(boolean bool){
		this.actionRequested = bool;
	}



	/**
	 * Returns the turn status.
	 * @return the turn status.
	 */
	public TurnStatus getTurnStatus(){
		return this.turnStatus;
	}

	/**
	 * sets the player turn status.
	 * @param status turn status to set to the player.
	 */
	public void setTurnStatus(TurnStatus status){
		this.turnStatus = status;
		Utils.logInfo("Player -> setTurnStatus(): " + playerName + "'s turn status set to " + status);
		setChanged();
	}

	/**
	 * Returns the available actions of the player according to his damage status.
	 * @return the available actions of the player according to his damage status.
	 */
	public List<MacroAction> getAvailableActions() {
		return damageStatus.getAvailableMacroActions();
	}

	/**
	 * Resets the player after he is dead.
	 */
	public void resetAfterDeath() {
		playerBoard.resetBoardAfterDeath();
		setDamageStatus(new LowDamage());
		//turnStatus = TurnStatus.IDLE;
		setChanged();
	}

	/**
	 * Flips the player's board if he has no damage.
	 * @return true if the board gets flipped.
	 */
	public boolean flipIfNoDamage() {
		boolean temp = playerBoard.flipIfNoDamage();
		if (temp)
			setChanged();
		return temp;
	}

	public void shoot() {
		setChanged();
	}

	public void reload(int indexOfWeaponToReload) {
		playerBoard.getWeaponCards().get(indexOfWeaponToReload).load();
		setChanged();
	}

	public WeaponCard getFiringWeapon(){
		if(firingWeapon == -1) throw new RuntimeException("No weapon firing!");
		return this.getPlayerBoard().getWeaponCards().get(this.firingWeapon);
	}

	/**
	 * Updates the player's representation.
	 */
	public void updateRep() {
		if (playerBoard.hasChanged() || playerBoard.getAmmoContainer().hasChanged() || damageStatus.hasChanged())
			setChanged();

		if (hasChanged() || playerRep == null) {
			playerRep = new PlayerRep(this);
			playerBoard.setNotChanged();
			playerBoard.getAmmoContainer().setNotChanged();
			damageStatus.setNotChanged();
			Utils.logInfo("Player -> updateRep(): " + playerName + "'s representation has been updated");
		} else
			Utils.logInfo("Player -> updateRep(): " + playerName + "'s representation is already up to date");
	}

	/**
	 * Returns the player representation. if the player who is asking the representation is the same player
	 * the representation is complete, otherwise it is hidden.
	 * @param playerAsking name of the player who is asking the representation.
	 * @return the player's representation.
	 */
	public Representation getRep(String playerAsking) {
		return playerName.equals(playerAsking) ? playerRep : playerRep.getHiddenPlayerRep();
	}

	/**
	 * Returns the player's representation.
	 * @return the player's representation.
	 */
	@Override
	public Representation getRep() {
		return playerRep;
	}
}