package org.spatialia.santa;

import org.spatialia.santa.GameInput.Movement;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Sprite extends Tile {

	public void appear() {
	}

	public void dissapear() {
	}

	private boolean visible = true;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	private Movement direction = Movement.Bottom;
	private boolean resting = true;

	public Movement getDirection() {
		return direction;
	}

	private int states;

	private int deltaX, deltaY;

	public int getDeltaX() {
		return deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}

	private int id;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public boolean isMonster() {
		return id == 5;
	}

	public boolean isGift() {
		return id == 4;
	}

	Bitmap[] bmStates;

	private int w, h;

	public Sprite(Bitmap[] bmStates, int states, int x, int y, int w, int h,
			int dx, int dy) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

		this.states = states;
		this.bmStates = bmStates;

		this.dx = dx;
		this.dy = dy;

		if (dx > 0) {
			direction = Movement.Right;
		} else {
			direction = Movement.Bottom;
		}
	}

	private int dx, dy;

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	/**
	 * sets the direction of movement
	 */
	public void setDirection(Movement direction) {
		if (direction != Movement.None) {
			this.direction = direction;
			resting = false;
		} else {
			resting = true;
		}
	}

	/**
	 * toggles the direction of movement 180 degrees
	 */
	public void switchDirection() {
		resting = false;
		switch (direction) {
		case Bottom:
			direction = Movement.Top;
			break;
		case Left:
			direction = Movement.Right;
			break;
		case Right:
			direction = Movement.Left;
			break;
		case Top:
			direction = Movement.Bottom;
			break;
		case None:
			resting = false;
			break;
		}
	}

	private int count = 0;
	private int c = 0;

	// switches the background image;
	private int switchState() {
		if (bmStates.length == states) {
			if (count < 2) {
				count++;
			} else {
				count = 0;
				c = (c + 1) % states;
			}
			return c;
		}

		c = resting ? 0 : (c + 1) % states;

		switch (direction) {
		case Bottom:
			break;
		case Left:
			c += states;
			break;
		case Right:
			c += 2 * states;
			break;
		case Top:
			c += 3 * states;
			break;
		case None:
			break;
		}
		return c;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (visible) {
			canvas.drawBitmap(bmStates[switchState()], x * w + deltaX, y * h
					+ deltaY, paint);
		}
	}

	public int getLeft() {
		return x * w;
	}

	public int getTop() {
		return y * h;
	}
}
