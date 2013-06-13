package org.sangraama.asserts;

import org.java_websocket.WebSocket;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.sangraama.controller.clientprotocol.ClientEvent;
import org.sangraama.controller.clientprotocol.PlayerDelta;
import org.sangraama.gameLogic.GameEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player {
	// Debug
	// Local Debug or logs
	private static boolean LL = true;
	private static boolean LD = true;
	public static final Logger log = LoggerFactory.getLogger(Player.class);
	private static final String TAG = "player :";
	private long userID;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Body body;
	private GameEngine gameEngine;
	private SangraamaMap sangraamaMap;
	private ClientEvent clientEvent;
	private float angle;
	// WebSocket Connection
	// private WebSocketConnection con;
	private boolean isUpdate;

	// Player Dynamic Parameters

	private Vec2 v = new Vec2(0f, 0f);
	private PlayerDelta delta;

	// Area of Interest
	private float halfWidth = 10f;
	private float halfHieght = 1000f;
	private WebSocket webSocket;

	public WebSocket getWebSocket() {
		return webSocket;
	}

	public void setWebSocket(WebSocket webSocket) {
		this.webSocket = webSocket;
	}

	public boolean isUpdate() {
		return this.isUpdate;
	}

	public Player(ClientEvent clientEvent, WebSocket webSocket) {
		this.createPlayer(clientEvent, webSocket);
	}

	/*
	 * public Player(long userID, float x, float y, WebSocketConnection con) {
	 * // this.createPlayer(userID, x, y, con); }
	 */
	private void createPlayer(ClientEvent clientEvent, WebSocket webSocket) {
		this.userID = clientEvent.getUserID();
		this.clientEvent = clientEvent;
		this.webSocket = webSocket;
		this.bodyDef = this.createBodyDef();
		this.fixtureDef = createFixtureDef();
		this.gameEngine = GameEngine.INSTANCE;
		this.gameEngine.addToPlayerQueue(this);
		this.sangraamaMap = SangraamaMap.INSTANCE;

		// System.out.println(TAG + " init player x:" + x + " :" + y);
	}

	public PlayerDelta getPlayerDelta() {
		// if (!isUpdate) {
		/*
		 * System.out.println(TAG + "id: " + this.userID + " x:" +
		 * this.body.getPosition().x + " " + "y:" + this.body.getPosition().y);
		 */

		// this.delta = new PlayerDelta(this.body.getPosition().x - this.x,
		// this.body.getPosition().y - this.y, this.userID);
		this.delta = new PlayerDelta(this.body.getPosition().x,
				this.body.getPosition().y, this.body.getAngle(),
				this.clientEvent.getUserID());
		// this.x = this.body.getPosition().x;
		// this.y = this.body.getPosition().y;

		// Check whether player is inside the tile or not
		/*
		 * if (!this.isInsideMap(this.x, this.y)) {
		 * PlayerPassHandler.INSTANCE.setPassPlayer(this); }
		 */

		// isUpdate = true;
		// }
		return this.delta;
	}

	/*
	 * public void sendUpdate(List<PlayerDelta> deltaList) { if (this.con !=
	 * null) { con.sendUpdate(deltaList); } }
	 */

	public void applyUpdate() {
		this.body.setLinearVelocity(this.getV());
		this.body.setTransform(body.getPosition(), this.getAngle());
	}

	private boolean isInsideMap(float x, float y) {
		// System.out.println(TAG + "is inside "+x+":"+y);
		if (0 <= x && x <= sangraamaMap.getMapWidth() && 0 <= y
				&& y <= sangraamaMap.getMapHeight()) {
			return true;
		} else {
			System.out.println(TAG + sangraamaMap.getMapWidth() + ":"
					+ sangraamaMap.getMapHeight());
			return false;
		}
	}

	/*
	 * public void sendNewConnection(ClientTransferReq transferReq) {
	 * con.sendNewConnection(transferReq); }
	 */

	public BodyDef createBodyDef() {
		BodyDef bd = new BodyDef();
		/*
		 * System.out.println(TAG + "create body def player x:" + this.x + " :"
		 * + this.y);
		 */
		bd.position.set(this.clientEvent.getX(), this.clientEvent.getY());
		bd.angle = clientEvent.getAngle();
		bd.type = BodyType.DYNAMIC;
		return bd;
	}

	public BodyDef getBodyDef() {
		return this.bodyDef;
	}

	private FixtureDef createFixtureDef() {
		CircleShape circle = new CircleShape();
		circle.m_radius = 1f;

		FixtureDef fd = new FixtureDef();
		fd.density = 0.5f;
		fd.shape = circle;
		fd.friction = 0.2f;
		fd.restitution = 0.5f;
		return fd;
	}

	public FixtureDef getFixtureDef() {
		return this.fixtureDef;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return this.body;
	}

	// public void setX(float x) {
	// if (x > 0) {
	// this.x = x;
	// }
	// }

	// public void setY(float y) {
	// if (y > 0) {
	// this.y = y;
	// }
	// }

	public Vec2 getV() {
		return this.v;
	}

	public void setV(float x, float y) {
		this.v.set(x, y);
		// System.out.println(TAG + " set V :");
	}

	public void setAOI(float width, float height) {
		this.halfWidth = width / 2;
		this.halfHieght = height / 2;
	}

	public float getAOIWidth() {
		return this.halfWidth;
	}

	public float getAOIHeight() {
		return this.halfHieght;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userId) {
		this.userID = userId;
	}

	public ClientEvent getClientEvent() {
		return clientEvent;
	}

	public void setClientEvent(ClientEvent clientEvent) {
		this.clientEvent = clientEvent;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(ClientEvent clientEvent) {
		float angle = body.getAngle();
		float v_x = clientEvent.getV_x();
		float v_y = clientEvent.getV_y();
		float v_a = clientEvent.getV_a();
		if (v_x == 1) {
			angle = 0;
		} else if (v_x == -1) {
			angle = 180;
		}
		if (v_y == 1) {
			angle = 90;
		} else if (v_y == -1) {
			angle = 270;
		}
		angle += v_a;
		this.angle = angle;
	}

}
