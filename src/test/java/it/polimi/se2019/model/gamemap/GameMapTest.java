package it.polimi.se2019.model.gamemap;

import it.polimi.se2019.model.player.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameMapTest {

	private Player player1, player2, player3, player4, player5;
	private ArrayList<Player> players;

	@Before
	public void setUp(){
		players = new ArrayList<>();
		player1 = new Player("Test 1", 0, Color.GREEN);
		player2 = new Player("Test 2", 1, Color.YELLOW);
		player3 = new Player("Test 3", 2, Color.BLUE);
		player4 = new Player("Test 4", 3, Color.GRAY);
		player5 = new Player("Test 5", 4, Color.MAGENTA);
		players.add(player1);
		players.add(player2);
		players.add(player3);
		players.add(player4);
		players.add(player5);
	}

	@After
	public void tearDown(){
	}

	@Test
	public void playerCoordinates_playerNotYetPlaced_shouldBeNull() {
		GameMap map = new GameMap("MediumMap.txt", players);
		assertNull(map.playerCoordinates(player1));
		assertNull(map.playerCoordinates(player2));
		assertNull(map.playerCoordinates(player3));
		assertNull(map.playerCoordinates(player4));
		assertNull(map.playerCoordinates(player5));
	}

	@Test
	public void playerCoordinates_playerMoved_correctOutput() {
		GameMap map = new GameMap("MediumMap.txt", players);
		map.movePlayerTo(player1, new Coordinates(2, 3));
		assertEquals(new Coordinates(2, 3), map.playerCoordinates(player1));
	}

	@Test
	public void movePlayerTo_correctInput_correctOutput() {
		GameMap map = new GameMap("MediumMap.txt", players);
		map.movePlayerTo(player1, new Coordinates(1,1));
		map.movePlayerTo(player2, new Coordinates(0,0));
		map.movePlayerTo(player3, new Coordinates(2,2));
		map.movePlayerTo(player4, new Coordinates(2,3));
		map.movePlayerTo(player3, new Coordinates(1,1));
		map.movePlayerTo(player5, new Coordinates(2,1));
		assertEquals(new Coordinates(1,1), map.playerCoordinates(player1));
		assertEquals(new Coordinates(0,0), map.playerCoordinates(player2));
		assertEquals(new Coordinates(1,1), map.playerCoordinates(player3));
		assertEquals(new Coordinates(2,3), map.playerCoordinates(player4));
		assertEquals(new Coordinates(2,1), map.playerCoordinates(player5));
	}

	@Test (expected = OutOfBoundariesException.class)
	public void movePlayerTo_playerOutOfColumn_throwsOutOfBoundaryException() {
		GameMap map = new GameMap("MediumMap.txt", players);
		map.movePlayerTo(player1, new Coordinates(1,5));
	}

	@Test (expected = OutOfBoundariesException.class)
	public void movePlayerTo_playerOutOfRow_throwsOutOfBoundaryException() {
		GameMap map = new GameMap("MediumMap.txt", players);
		map.movePlayerTo(player1, new Coordinates(9,1));
	}

	@Test
	public void reachableSquares() {
	}

	@Test
	public void getRoomCoordinates_MediumMapCoordinates_correctOutput() {
		GameMap map = new GameMap("MediumMap.txt", players);

		//--------------------------------------------FIRST ROOM---------------------------------
		ArrayList<Coordinates> correctOutput = new ArrayList<>();
		correctOutput.add(new Coordinates(0,0));
		correctOutput.add(new Coordinates(0,2));
		correctOutput.add(new Coordinates(0,1));
		ArrayList<Coordinates> methodOutput = map.getRoomCoordinates(new Coordinates(0,0));

		assertEquals(methodOutput.size(), correctOutput.size());

		boolean valid = true;
		for (Coordinates coordinates : correctOutput) {
			if(!(methodOutput.contains(coordinates)))
				valid = false;
		}
		assertTrue(valid);

		//--------------------------------------------SECOND ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(0,3));
		methodOutput = map.getRoomCoordinates(new Coordinates(0,3));

		assertEquals(methodOutput, correctOutput);

		//--------------------------------------------THIRD ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(1,0));
		correctOutput.add(new Coordinates(1,1));
		methodOutput = map.getRoomCoordinates(new Coordinates(1,0));

		assertEquals(methodOutput.size(), correctOutput.size());

		valid = true;
		for (Coordinates coordinates : correctOutput) {
			if(!(methodOutput.contains(coordinates)))
				valid = false;
		}
		assertTrue(valid);

		//--------------------------------------------FOURTH ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(1,2));
		correctOutput.add(new Coordinates(1,3));
		correctOutput.add(new Coordinates(2,2));
		correctOutput.add(new Coordinates(2,3));
		methodOutput = map.getRoomCoordinates(new Coordinates(2,3));

		assertEquals(methodOutput.size(), correctOutput.size());

		valid = true;
		for (Coordinates coordinates : correctOutput) {
			if(!(methodOutput.contains(coordinates)))
				valid = false;
		}
		assertTrue(valid);

		//--------------------------------------------FIFTH ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(2,1));

		methodOutput = map.getRoomCoordinates(new Coordinates(2,1));

		assertEquals(methodOutput, correctOutput);
	}

	@Test
	public void getRoomCoordinates_BigMapSquare_correctOutput() {
		GameMap map = new GameMap("BigMap.txt", players);

		//--------------------------------------------FIRST ROOM---------------------------------
		ArrayList<Coordinates> correctOutput = new ArrayList<>();
		correctOutput.add(new Coordinates(1, 0));
		correctOutput.add(new Coordinates(0, 0));
		ArrayList<Coordinates> methodOutput = map.getRoomCoordinates(map.getSquare(new Coordinates(0, 0)));

		assertEquals(methodOutput.size(), correctOutput.size());

		boolean valid = true;
		for (Coordinates coordinates : correctOutput) {
			if (!(methodOutput.contains(coordinates)))
				valid = false;
		}
		assertTrue(valid);

		//--------------------------------------------SECOND ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(0, 2));
		correctOutput.add(new Coordinates(0, 1));
		methodOutput = map.getRoomCoordinates(map.getSquare(new Coordinates(0, 2)));

		assertEquals(methodOutput.size(), correctOutput.size());

		valid = true;
		for (Coordinates coordinates : correctOutput) {
			if (!(methodOutput.contains(coordinates)))
				valid = false;
		}
		assertTrue(valid);

		//--------------------------------------------THIRD ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(0, 3));
		methodOutput = map.getRoomCoordinates(map.getSquare(new Coordinates(0, 3)));

		assertEquals(methodOutput, correctOutput);

		//--------------------------------------------FOURTH ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(1, 1));
		methodOutput = map.getRoomCoordinates(map.getSquare(new Coordinates(1, 1)));

		assertEquals(methodOutput, correctOutput);

		//--------------------------------------------FIFTH ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(2, 2));
		correctOutput.add(new Coordinates(2, 3));
		correctOutput.add(new Coordinates(1, 2));
		correctOutput.add(new Coordinates(1, 3));
		methodOutput = map.getRoomCoordinates(map.getSquare(new Coordinates(1, 3)));

		assertEquals(methodOutput.size(), correctOutput.size());

		valid = true;
		for (Coordinates coordinates : correctOutput) {
			if (!(methodOutput.contains(coordinates)))
				valid = false;
		}
		assertTrue(valid);

		//--------------------------------------------SIXTH ROOM---------------------------------
		correctOutput.clear();
		correctOutput.add(new Coordinates(2, 0));
		correctOutput.add(new Coordinates(2, 1));
		methodOutput = map.getRoomCoordinates(map.getSquare(new Coordinates(2, 0)));

		assertEquals(methodOutput.size(), correctOutput.size());

		valid = true;
		for (Coordinates coordinates : correctOutput) {
			if (!(methodOutput.contains(coordinates)))
				valid = false;
		}
		assertTrue(valid);
	}

	@Test
	public void getRoomCoordinates1() {
	}

	@Test
	public void visible() {
	}

	@Test
	public void getSpawnSquare() {
	}

	@Test
	public void getCoordinates() {
	}

	@Test
	public void getDistance() {
	}

	@Test
	public void getSquare() {
	}

	@Test
	public void isSpawnSquare() {
	}
}