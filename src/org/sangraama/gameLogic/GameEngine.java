package org.sangraama.gameLogic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import org.java_websocket.WebSocket;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.sangraama.asserts.SangraamaMap;
import org.sangraama.asserts.Player;
import org.sangraama.common.Constants;
import org.sangraama.controller.CommunicationHandler;
import org.sangraama.controller.clientprotocol.PlayerDelta;

public enum GameEngine implements Runnable {
	INSTANCE;
	// Debug
	private String TAG = "Game Engine :";

	private World world = null;
	private SangraamaMap sangraamaMap = null;
	private UpdateEngine updateEngine = null;
	private Map<WebSocket, Player> playerList = null;
	private List<Player> newPlayerQueue = null;
	private List<Player> removePlayerQueue = null;
	private CommunicationHandler communicationHandler;

	// this method only access via class
	GameEngine() {
		System.out.println(TAG + "Init GameEngine...");
		this.world = new World(new Vec2(0.0f, 0.0f), true);
		this.playerList = new HashMap<WebSocket, Player>();
		this.newPlayerQueue = new ArrayList<Player>();
		this.removePlayerQueue = new ArrayList<Player>();
		this.updateEngine = UpdateEngine.INSTANCE;
		this.sangraamaMap = SangraamaMap.INSTANCE;
		this.communicationHandler = new CommunicationHandler();
	}

	@Override
	public void run() {
		System.out.println(TAG + "GameEngine Start running.. fps:"
				+ Constants.fps + " timesteps:" + Constants.timeStep);
		init();
		Timer timer = new Timer(40, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
				world.step(Constants.timeStep, Constants.velocityIterations,
						Constants.positionIterations);
				pushUpdate();
			}
		});

		timer.start();

	}

	public void init() {
		// Load static map asserts into JBox2D
	}

	public void update() {

		for (Player rmPlayer : removePlayerQueue) {
			this.playerList.remove(rmPlayer);
			System.out.println(TAG + "Removing new players");
		}
		this.removePlayerQueue.clear();

		// Add new player to the world

		for (Player newPlayer : newPlayerQueue) {
			newPlayer.setBody(world.createBody(newPlayer.getBodyDef()));
			this.playerList.put(newPlayer.getWebSocket(), newPlayer);
			System.out.println(TAG + "Adding new players");
		}
		this.newPlayerQueue.clear();

		for (Player player : playerList.values()) {
			// System.out.println(TAG + player.getUserID()
			// +" Adding players Updates");
			player.applyUpdate();
		}
	}

	public void pushUpdate() {
		List<PlayerDelta> playerDeltas = new ArrayList<>();
		for (Player player : playerList.values()) {
			playerDeltas.add(player.getPlayerDelta());
		}
		communicationHandler.sendUpdate(playerDeltas, playerList.values());
		// this.updateEngine.setPlayerList(playerList.values());
	}

	public void stopGameWorld() {
		// this.execute = false;
	}

	public void addToPlayerQueue(Player player) {
		this.newPlayerQueue.add(player);

	}

	public void addToRemovePlayerQueue(Player player) {
		this.removePlayerQueue.add(player);
	}

	public Map<WebSocket, Player> getPlayerList() {
		return this.playerList;
	}
}