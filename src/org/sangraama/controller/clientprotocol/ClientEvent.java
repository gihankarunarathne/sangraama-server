package org.sangraama.controller.clientprotocol;

public class ClientEvent {
	private int type;
	private long userID;
	private float v_x;
	private float v_y;
	private float v_a;
	private float x;
	private float y;
	private float angle;

	public float getV_a() {
		return v_a;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getAngle() {
		return angle;
	}

	public int getType() {
		return type;
	}

	public long getUserID() {
		return userID;
	}

	public float getV_x() {
		return v_x;
	}

	public float getV_y() {
		return v_y;
	}

}
