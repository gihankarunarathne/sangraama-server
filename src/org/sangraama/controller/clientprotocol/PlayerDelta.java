package org.sangraama.controller.clientprotocol;

public class PlayerDelta {
	private float x = 0, y = 0, angle = 0;
	private long userID = 0;
	private int type = 1;

	public PlayerDelta(float dx, float dy, float angle, long userID) {
		this.x = dx;
		this.y = dy;
		this.angle = angle;
		this.userID = userID;
	}

	public long getUserID() {
		return this.userID;
	}

	public float getDx() {
		return x;
	}

	public void setDx(int dx) {
		this.x = dx;
	}

	public float getDy() {
		return y;
	}

	public void setDy(int dy) {
		this.y = dy;
	}
}
