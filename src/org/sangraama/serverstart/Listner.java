package org.sangraama.serverstart;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import org.sangraama.asserts.SangraamaMap;
import org.sangraama.controller.ClientCommunicationController;
import org.sangraama.gameLogic.GameEngine;

public class Listner implements javax.servlet.ServletContextListener {
	private Thread gameEngine;
	// private Thread updateEngine = null;
	// private Thread thriftServerThread = null;
	private Properties prop;
	private ClientCommunicationController clientCommunicationController;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		GameEngine.INSTANCE.stopGameWorld();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		int port1 = 8887;

		try {
			this.prop = new Properties();
			/*this.prop.load(getClass().getResourceAsStream(
					"/sangraamaserver.properties"));
			SangraamaMap.INSTANCE.setMap(
					Float.parseFloat(prop.getProperty("maporiginx")),
					Float.parseFloat(prop.getProperty("maporiginy")),
					Float.parseFloat(prop.getProperty("mapwidth")),
					Float.parseFloat(prop.getProperty("mapheight")));*/
			// this.updateEngine = new Thread(UpdateEngine.INSTANCE);
			// this.updateEngine.start();
			this.gameEngine = new Thread(GameEngine.INSTANCE);
			this.gameEngine.start();
			clientCommunicationController = new ClientCommunicationController(
					port1);
			/*
			 * thriftServer = new ThriftServer(Integer.parseInt(prop
			 * .getProperty("thriftserverport"))); thriftServerThread = new
			 * Thread(thriftServer); thriftServerThread.start();
			 */
			System.out.println("SANGRAAMA STARTED");
			clientCommunicationController.start();
			System.out.println("Server1 started on port 8887");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
