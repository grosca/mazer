package org.spatialia.santa;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Can be attached to a GameView; Provides the direction for which the engine
 * shall move the player.
 */
public abstract class GameInput implements OnTouchListener {

	public enum Movement {
		Left, Top, Right, Bottom, None
	}

	private Movement movement = Movement.None;

	public GameInput() {
	}

	protected Movement getMovement() {
		return movement;
	}

	public void onMoveTop() {
		movement = Movement.Top;
	}

	public void onMoveBottom() {
		movement = Movement.Bottom;
	}

	public void onMoveRight() {
		movement = Movement.Right;
	}

	public void onMoveLeft() {
		movement = Movement.Left;
	}

	public void onMoveStopped() {
		movement = Movement.None;
	}

	private int oldX;
	private int oldY;

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			onMoveStopped();
			break;
		case MotionEvent.ACTION_DOWN:
			oldX = (int) event.getX();
			oldY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int newX = (int) event.getX();
			int newY = (int) event.getY();

			int deltaX = newX - oldX;
			int deltaY = newY - oldY;

			if (Math.abs(deltaX) < 20 && Math.abs(deltaY) < 20) {
				break;
			}

			if (Math.abs(deltaX) > Math.abs(deltaY)) {
				if (deltaX > 0) {
					onMoveRight();
				} else {
					onMoveLeft();
				}
			} else {
				if (deltaY > 0) {
					onMoveBottom();
				} else {
					onMoveTop();
				}
			}

			oldX = newX;
			oldY = newY;
			break;
		}
		return true;
	}
}