package org.spatialia.santa;

import org.spatialia.santa.logic.Level;
import org.spatialia.santa.logic.LevelManager;

import android.content.res.Resources;

public class GameEngine extends GameInput implements Runnable {

	private GameView view;
	private LevelManager model;

	private Thread thread = null;
	private volatile boolean running = false;

	public GameEngine(GameView view, LevelManager model) {
		this.view = view;
		this.model = model;
	}

	// TODO: remove this
	private Resources resources;
	private Level level;

	public void init(Resources resources, Level level) {
		this.level = level;
		this.resources = resources;

		view.setOnTouchListener(this);
	}

	public boolean isPaused() {
		return !running;
	}

	public void onResume() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void onPause() {
		boolean retry = true;
		running = false;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception ex) {
		}
	}

	private boolean canGo(int x, int y) {
		return level.canGo(x, y);
	}

	private Sprite mainCharacter;

	private void doMainCharacterLogic() {
		if (!moveTowards(mainCharacter)) {
			Movement movement = getMovement();

			if (mainCharacter != null) {
				mainCharacter.setDirection(movement);
			}
			if (movement != Movement.None) {
				switch (movement) {
				case Top:
					if (canGo(mainCharacter.getX(), mainCharacter.getY() - 1)) {
						mainCharacter.setDeltaY(-10);
					}
					break;
				case Bottom:
					if (canGo(mainCharacter.getX(), mainCharacter.getY() + 1)) {
						mainCharacter.setDeltaY(+10);
					}
					break;
				case Left:
					if (canGo(mainCharacter.getX() - 1, mainCharacter.getY())) {
						mainCharacter.setDeltaX(-10);
					}
					break;
				case Right:
					if (canGo(mainCharacter.getX() + 1, mainCharacter.getY())) {
						mainCharacter.setDeltaX(+10);
					}
					break;
				case None:
					break;
				}
			}
		}
	}

	private int step = 20;

	@Override
	public void run() {
		while (running) {
			sleep(25);

			synchronized (model) {
				initSizeAndResources();
			}

			if (mainCharacter == null) {
				continue;
			} else {
				doMainCharacterLogic();
			}

			paint();

			synchronized (level) {
				for (Sprite s : level.getSprites()) {
					doSpriteLogic(s);

					if (s.isGift() && collides(mainCharacter, s)) {
						if (s.isVisible()) {
							model.giftCollected();
							s.setVisible(false);
						}
					} else if (s.isMonster() && collides(mainCharacter, s)) {
						mainCharacter.setX(level.getStart().x);
						mainCharacter.setY(level.getStart().y);
						mainCharacter.setDeltaX(0);
						mainCharacter.setDeltaY(0);
					}
				}
			}

			if (mainCharacter.getX() == level.getEnd().x
					&& mainCharacter.getY() == level.getEnd().y) {
				// TODO: level finished;
				loaded = false;
				init(resources, model.getNextLevel());
				mainCharacter.setX(level.getStart().x);
				mainCharacter.setY(level.getStart().y);
				initSizeAndResources();
			} else if (mainCharacter.getX() == level.getStart().x
					&& mainCharacter.getY() == level.getStart().y) {
			}
			// TODO: load previous level also;
		}
	}

	private boolean collides(Sprite s1, Sprite s2) {
		if (s1.getX() == s2.getX() && s1.getY() == s2.getY()) {
			return true;
		}
		return false;
	}

	/**
	 * Tries to continue the movement of a sprite.
	 * 
	 * @param sprite
	 * @return
	 */
	private boolean moveTowards(Sprite sprite) {
		if (sprite.getDeltaX() != 0) {
			if (sprite.getDeltaX() > 0) {
				sprite.setDeltaX(sprite.getDeltaX() + step);

				if (sprite.getDeltaX() >= w) {
					sprite.setX(sprite.getX() + 1);
					sprite.setDeltaX(0);
				}
			} else {
				sprite.setDeltaX(sprite.getDeltaX() - step);

				if (sprite.getDeltaX() <= -w) {
					sprite.setX(sprite.getX() - 1);
					sprite.setDeltaX(0);
				}
			}
		} else if (sprite.getDeltaY() != 0) {
			if (sprite.getDeltaY() > 0) {
				sprite.setDeltaY(sprite.getDeltaY() + step);

				if (sprite.getDeltaY() >= h) {
					sprite.setY(sprite.getY() + 1);
					sprite.setDeltaY(0);
				}
			} else {
				sprite.setDeltaY(sprite.getDeltaY() - step);

				if (sprite.getDeltaY() <= -h) {
					sprite.setY(sprite.getY() - 1);
					sprite.setDeltaY(0);
				}
			}
		} else {
			return false;
		}

		return true;
	}

	// move sprites arround, animate, etc.
	private void doSpriteLogic(Sprite sprite) {
		if (!moveTowards(sprite)) {
			if (canGo(sprite.getX() + sprite.getDx(),
					sprite.getY() + sprite.getDy())) {
				sprite.setDeltaY(10 * sprite.getDy());
				sprite.setDeltaX(10 * sprite.getDx());
			} else {
				sprite.setDy(-sprite.getDy());
				sprite.setDx(-sprite.getDx());
				sprite.switchDirection();
			}
		}
	}

	private int blocksW = 12;
	private int blocksH = 7;

	private int w;
	private int h;

	private boolean loaded = false;

	private void initSizeAndResources() {
		if (!loaded && view.getWidth() > 0 && view.getHeight() > 0) {
			if (view.getWidth() < view.getHeight()) {
				blocksW = 7;
				blocksH = 12;
			} else {
				blocksW = 12;
				blocksH = 7;
			}

			w = view.getWidth() / blocksW;
			h = view.getHeight() / blocksH;

			loadResources(w, h);

			loaded = true;
		}
	}

	public void onRotate() {
		synchronized (view) {
			synchronized (model.getCurrLevel()) {
				model.saveGameState();
				loaded = false;
				initSizeAndResources();
				model.restoreGameState();
			}
		}
		paint();
	}

	private void paint() {
		view.paint(level.getTiles(), mainCharacter, level.getSprites(),
				blocksW, blocksH, w, h);
	}

	private void loadResources(int w, int h) {
		mainCharacter = model.loadResources(w, h);
	}
}
