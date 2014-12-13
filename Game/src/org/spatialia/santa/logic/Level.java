package org.spatialia.santa.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spatialia.santa.R;
import org.spatialia.santa.Sprite;
import org.spatialia.santa.Tile;
import org.spatialia.santa.GameInput.Movement;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Level interface. It provides the world, tiles, sprites, etc.
 */
public class Level {
	/**
	 * 
	 * @param mgr
	 * @param world
	 * @param blankId
	 *            the grass res id
	 * @param wallId
	 *            the wall res id
	 */
	public Level(int levelId, int[][] world, int blankId, int wallId,
			int monsterId, int giftId) {
		this.levelId = levelId;
		this.blankId = blankId;
		this.wallId = wallId;
		this.monsterId = monsterId;
		this.giftId = giftId;
		this.world = world;

		for (int y = 0; y < world.length; y++) {
			for (int x = 0; x < world[0].length; x++) {
				if (world[y][x] == 2) {
					this.start = new Point(x, y);
				} else if (world[y][x] == 3) {
					this.end = new Point(x, y);
				}
			}
		}
	}

	private int levelId;

	private void saveSprite(int pos, Sprite sprite, Settings settings) {
		String settingsPrefix = String
				.format("level%d.sprite%d.", levelId, pos);

		settings.setInt(settingsPrefix + "x", sprite.getX());
		settings.setInt(settingsPrefix + "y", sprite.getY());
		settings.setInt(settingsPrefix + "dx", sprite.getDx());
		settings.setInt(settingsPrefix + "dy", sprite.getDy());
		settings.setInt(settingsPrefix + "deltaX", sprite.getDeltaX());
		settings.setInt(settingsPrefix + "deltaY", sprite.getDeltaY());
		settings.setString(settingsPrefix + "direction", sprite.getDirection()
				.name());
		settings.setBoolean(settingsPrefix + "visible", sprite.isVisible());
	}

	private void restoreSprite(int pos, Sprite sprite, Settings settings) {
		String settingsPrefix = String
				.format("level%d.sprite%d.", levelId, pos);

		if (settings.getInt(settingsPrefix + "deltaX") != -1) {
			sprite.setX(settings.getInt(settingsPrefix + "x"));
			sprite.setY(settings.getInt(settingsPrefix + "y"));
			sprite.setDx(settings.getInt(settingsPrefix + "dx"));
			sprite.setDy(settings.getInt(settingsPrefix + "dy"));
			sprite.setDeltaX(settings.getInt(settingsPrefix + "deltaX"));
			sprite.setDeltaY(settings.getInt(settingsPrefix + "deltaY"));

			String dir = settings.getString(settingsPrefix + "direction");
			if (dir.length() > 0) {
				sprite.setDirection(Movement.valueOf(dir));
			}
			sprite.setVisible(settings.getBoolean(settingsPrefix + "visible"));
		}
	}

	public void saveState(Settings settings) {
		if (sprites != null && mainCharacter != null) {
			for (int i = 0; i < sprites.size(); i++) {
				saveSprite(i, sprites.get(i), settings);
			}
			saveSprite(-1, mainCharacter, settings);
		}
		System.err.println("saved game state.");
	}

	public void restoreState(Settings settings) {
		for (int i = 0; i < sprites.size(); i++) {
			restoreSprite(i, sprites.get(i), settings);
		}
		restoreSprite(-1, mainCharacter, settings);
		System.err.println("restored game state.");
	}

	private int blankId, wallId, monsterId, giftId;

	private int[][] world;
	private Point start, end;

	// the size of a block in pixels
	private List<Sprite> sprites;
	private Tile[][] tiles;

	public Sprite getSprite(Resources resources, int resId, int states, int x,
			int y, int w, int h, int dx, int dy) {
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);

		final String id = resId + "_" + w + "_" + h;

		if (!spriteCache.containsKey(resId)) {
			Bitmap[] bmStates = null;
			if (bitmap.getWidth() / bitmap.getHeight() > 3) {
				bmStates = new Bitmap[states];

				for (int i = 0; i < states; i++) {
					Bitmap src = Bitmap.createBitmap(bitmap,
							i * bitmap.getHeight(), 0, bitmap.getHeight(),
							bitmap.getHeight());
					bmStates[i] = Bitmap.createScaledBitmap(src, w, h, true);
				}
			} else {
				bmStates = new Bitmap[states * 4];
				for (int i = 0; i < states * 4; i++) {
					int sw = bitmap.getWidth() / states;
					int sh = bitmap.getHeight() / 4;

					int sl = i % states;
					int st = i / states;

					bmStates[i] = Bitmap.createScaledBitmap(Bitmap
							.createBitmap(bitmap, sl * sw, st * sh, sw, sh), w,
							h, true);
				}
			}

			spriteCache.put(id, bmStates);
		}

		return new Sprite(spriteCache.get(id), states, x, y, w, h, dx, dy);
	}

	public Sprite loadResources(Resources resources, int w, int h) {
		sprites = new ArrayList<Sprite>();

		tiles = new Tile[world.length][world[0].length];

		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				if (world[i][j] == 1) {
					tiles[i][j] = new Tile(getBitmap(resources, wallId, w, h),
							j * w, i * h);
				} else if (world[i][j] == 2) {
					tiles[i][j] = new Tile(getBitmap(resources,
							R.drawable.start, w, h), j * w, i * h);
				} else if (world[i][j] == 3) {
					tiles[i][j] = new Tile(getBitmap(resources, R.drawable.end,
							w, h), j * w, i * h);
				} else {
					tiles[i][j] = new Tile(getBitmap(resources, blankId, w, h),
							j * w, i * h);
				}
				if (world[i][j] == 5) {
					int dx = 0;
					int dy = 0;
					if (canGo(j, i + 1) || canGo(j, i - 1)) {
						dy = 1;
					} else if (canGo(j + 1, i) || canGo(j - 1, i)) {
						dx = 1;
					}
					Sprite sprite = getSprite(resources, monsterId, 4, j, i, w,
							h, dx, dy);
					sprite.setId(world[i][j]);
					sprites.add(sprite);
				} else if (world[i][j] == 4) {
					Sprite sprite = getSprite(resources, giftId, 5, j, i, w, h,
							0, 0);
					sprite.setId(world[i][j]);
					sprites.add(sprite);
				}
			}
		}

		boolean prev = mainCharacter != null;

		mainCharacter = getSprite(resources, R.drawable.main_sprite, 3,
				prev ? mainCharacter.getX() : getStart().x,
				prev ? mainCharacter.getY() : getStart().y, w, h, 0, 0);
		mainCharacter.setId(0);
		return mainCharacter;
	}

	private Sprite mainCharacter;

	private Map<String, Bitmap[]> spriteCache = new HashMap<String, Bitmap[]>();
	private Map<String, Bitmap> cache = new HashMap<String, Bitmap>();

	private Bitmap getBitmap(Resources resources, int resId, int w, int h) {
		final String id = resId + "_" + w + "_" + h;
		if (!cache.containsKey(id)) {
			Bitmap bmp = BitmapFactory.decodeResource(resources, resId);
			bmp = Bitmap.createScaledBitmap(bmp, w, h, true);
			cache.put(id, bmp);
		}
		return cache.get(id);
	}

	public int[][] getWorld() {
		return world;
	}

	public boolean canGo(int x, int y) {
		return world[y][x] != 1;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public List<Sprite> getSprites() {
		return sprites;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}
}
