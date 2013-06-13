package org.sangraama.gameLogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sangraama.asserts.Player;
import org.sangraama.asserts.SangraamaMap;
import org.sangraama.controller.CommunicationHandler;
import org.sangraama.controller.clientprotocol.PlayerDelta;

public enum UpdateEngine implements Runnable {
	INSTANCE;
	// Debug
	private String TAG = "Update Engine :";

	private SangraamaMap sangraamaMap;
	private List<Player> playerList; // don't modify;read only
	private Map<Long, PlayerDelta> playerDelta;
	private float[][] locations;// store x,y and ID
	private boolean execute = true;
	private boolean isUpdate;
	private CommunicationHandler communicationHandler;

	UpdateEngine() {
		System.out.println(TAG + "Init GameEngine...");
		communicationHandler = new CommunicationHandler();
		this.playerList = new ArrayList<Player>();
		// this.locations = new float[100][3];
		this.sangraamaMap = SangraamaMap.INSTANCE;
	}

	public boolean isUpdateVal() {
		return this.isUpdate;
	}

	private boolean setIsUpdate(boolean isUpdate) {
		return this.isUpdate = isUpdate;
	}

	public boolean setPlayerList(Collection<Player> playerList) {
		this.playerList = (List<Player>) playerList;
		return this.setIsUpdate(true);
	}

	@Override
	public void run() {
		while (this.execute) {
			if (this.isUpdateVal()) {
				this.pushUpdate();
			}
		}
	}

	public void pushUpdate() {
		playerDelta = new HashMap<Long, PlayerDelta>();
		// System.out.println(TAG + "delta list length :" + playerDelta.size());
		for (Player player : playerList) {
			// System.out.println(TAG + player.getUserID() +
			// " Sending player updates");
			playerDelta.put(player.getUserID(), player.getPlayerDelta());
		}
		// System.out.println(TAG + "delta list length :" + playerDelta.size());

		this.isUpdate = false;
	}

	/**
	 * This method can replace with region query in 4.14 box2D manual
	 * 
	 * @param player
	 * @return ArrayList<PlayerDelta>
	 */
	private List<PlayerDelta> getAreaOfInterest(Player p) {
		List<PlayerDelta> delta = new ArrayList<PlayerDelta>();
		// Add players own details

		// going through all players and check their locations
		// inefficient
		/*
		 * for (Player player : playerList) { if (p.getX() - p.getAOIWidth() <=
		 * player.getX() && player.getX() <= p.getX() + p.getAOIWidth() &&
		 * p.getY() - p.getAOIHeight() <= player.getY() && player.getY() <=
		 * p.getY() + p.getAOIHeight()) {
		 * delta.add(this.playerDelta.get(player.getUserID())); } }
		 */

		return delta;
	}

	public boolean stopUpdateEngine() {
		return this.execute = false;
	}
}
