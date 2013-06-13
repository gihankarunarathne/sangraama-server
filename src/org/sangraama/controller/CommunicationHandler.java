package org.sangraama.controller;

import java.util.Collection;
import java.util.List;

import org.sangraama.asserts.Player;
import org.sangraama.controller.clientprotocol.PlayerDelta;

import com.google.gson.Gson;

public class CommunicationHandler {
	public void sendUpdate(List<PlayerDelta> deltaList,
			Collection<Player> players) {
		Gson gson = new Gson();
		for (Player player : players) {

			player.getWebSocket().send(gson.toJson(deltaList));
		}

	}

}
