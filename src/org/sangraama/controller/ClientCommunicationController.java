package org.sangraama.controller;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.sangraama.asserts.Player;
import org.sangraama.controller.clientprotocol.ClientEvent;
import org.sangraama.gameLogic.GameEngine;

import com.google.gson.Gson;

public class ClientCommunicationController extends WebSocketServer {
	// private Player player;
	private Gson gson;

	public ClientCommunicationController(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		gson = new Gson();
	}

	public ClientCommunicationController(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onClose(WebSocket webSocket, int arg1, String arg2, boolean arg3) {
		System.out.println("On close" + arg3);
		String id = webSocket.toString().substring(
				webSocket.toString().indexOf("@"));
		System.out.println("close id" + id);
		GameEngine.INSTANCE.getPlayerList().remove(webSocket);

	}

	@Override
	public void onError(WebSocket webSocket, Exception arg1) {
		System.out.println("On error");

	}

	@Override
	public void onMessage(WebSocket webSocket, String msg) {
		System.out.println("On message" + msg + webSocket.toString());
		System.out.println(webSocket);
		ClientEvent clientEvent = gson.fromJson(msg, ClientEvent.class);

		if (clientEvent.getType() == 1) {
			if (GameEngine.INSTANCE.getPlayerList().get(webSocket) == null) {
				Player player = new Player(clientEvent, webSocket);
				player.setV(clientEvent.getV_x(), clientEvent.getV_y());
				GameEngine.INSTANCE.getPlayerList().put(webSocket, player);

			} else {
				Player player = GameEngine.INSTANCE.getPlayerList().get(
						webSocket);
				player.setV(clientEvent.getV_x(), clientEvent.getV_y());
				player.setClientEvent(clientEvent);
			}
		}

	}

	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake arg1) {
		System.out.println("Connection opened" + webSocket.toString());

	}

}
