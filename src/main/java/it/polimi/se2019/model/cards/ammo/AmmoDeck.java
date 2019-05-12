package it.polimi.se2019.model.cards.ammo;

import com.google.gson.*;
import it.polimi.se2019.model.cards.Card;
import it.polimi.se2019.model.cards.Deck;
import it.polimi.se2019.utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the ammo deck
 *
 * @author MarcerAndrea
 */
public class AmmoDeck extends Deck<Card> {

	/**
	 * Initialize the ammo deck according to the file "AmmoDeck.json"
	 */
	protected void initializeDeck() {

		try (Reader reader = new FileReader(new File(Thread.currentThread().getContextClassLoader().getResource("decks/AmmoDeck.json").getFile()))) {
			JsonParser parser = new JsonParser();
			JsonObject rootObject = parser.parse(reader).getAsJsonObject();

			JsonArray ammoCards = rootObject.getAsJsonArray("ammocards");
			for (JsonElement entry : ammoCards) {
				JsonObject cardToAdd = entry.getAsJsonObject();

				List<AmmoType> ammo = new ArrayList<>();

				for (JsonElement ammoToAdd : cardToAdd.getAsJsonArray("ammo")) {
					ammo.add(AmmoType.valueOf(ammoToAdd.getAsString()));
				}
				Utils.logInfo("AmmoDeck -> initializeDeck(): Adding ammo card: " + ammo + (cardToAdd.get("powerup").getAsBoolean() ? " Powerup" : ""));
				addCard(new AmmoCard(ammo, cardToAdd.get("powerup").getAsBoolean(), cardToAdd.get("name").getAsString()));
			}
		} catch (IOException | JsonParseException e) {
			Utils.logError("Cannot parse ammo cards", e);
		}
	}
}