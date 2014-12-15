package org.spatialia.santa.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spatialia.santa.R;
import org.spatialia.santa.Sprite;
import org.spatialia.santa.Tile;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;

/**
 * Level interface. It provides the world, tiles, sprites, etc.
 */
public class Level {

	private JSONArray images;

	private int levelId;

	private int[][] world;
	private Point start, end;

	// the size of a block in pixels
	private List<Sprite> sprites;
	private Tile[][] tiles;

	public Level(int levelId, JSONObject json) throws Exception {
		this.levelId = levelId;

		JSONArray array = json.getJSONArray("array");
		world = new int[array.length()][array.getJSONArray(0).length()];

		JSONArray subArray = null;
		for (int y = 0; y < world.length; y++) {
			subArray = array.getJSONArray(y);
			for (int x = 0; x < world[0].length; x++) {
				world[y][x] = subArray.getInt(x);
				if (world[y][x] == 2) {
					this.start = new Point(x, y);
				} else if (world[y][x] == 3) {
					this.end = new Point(x, y);
				}
			}
		}

		images = json.getJSONArray("images");
	}

	// TODO: improve shared prefs writins of 280 keys
	private void saveSprite(int pos, Sprite sprite, Settings settings) {
		String settingsPrefix = String
				.format("level%d.sprite%d.", levelId, pos);

		settings.setSprite(settingsPrefix, sprite);
	}

	private void restoreSprite(int pos, Sprite sprite, Settings settings) {
		String settingsPrefix = String
				.format("level%d.sprite%d.", levelId, pos);

		settings.getSprite(settingsPrefix, sprite);
	}

	public void saveState(Settings settings) {
		if (sprites != null && mainCharacter != null) {
			for (int i = 0; i < sprites.size(); i++) {
				saveSprite(i, sprites.get(i), settings);
			}
			saveSprite(-1, mainCharacter, settings);
		}
	}

	public void restoreState(Settings settings) {
		for (int i = 0; i < sprites.size(); i++) {
			restoreSprite(i, sprites.get(i), settings);
		}
		restoreSprite(-1, mainCharacter, settings);
	}

	public Sprite getSprite(Resources resources, int resId, int states, int x,
			int y, int w, int h, int dx, int dy) {
		Bitmap bitmap = null;
		if (resources == null) {
			try {
				byte[] decodedString = Base64.decode(images.getString(resId),
						Base64.DEFAULT);
				bitmap = BitmapFactory.decodeByteArray(decodedString, 0,
						decodedString.length);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			bitmap = BitmapFactory.decodeResource(resources, resId);
		}

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
				int tileId = world[i][j] > 3 ? 0 : world[i][j];
				tiles[i][j] = new Tile(getBitmap(tileId, w, h), j * w, i * h);
				if (world[i][j] == 5) {
					int dx = 0;
					int dy = 0;
					if (canGo(j, i + 1) || canGo(j, i - 1)) {
						dy = 1;
					} else if (canGo(j + 1, i) || canGo(j - 1, i)) {
						dx = 1;
					}
					Sprite sprite = getSprite(null, world[i][j], 4, j, i, w, h,
							dx, dy);
					sprite.setId(world[i][j]);
					sprites.add(sprite);
				} else if (world[i][j] == 4) {
					Sprite sprite = getSprite(null, world[i][j], 5, j, i, w, h,
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

	private Bitmap getBitmap(int resId, int w, int h) {
		final String id = resId + "_" + w + "_" + h;
		if (!cache.containsKey(id)) {
			try {
				byte[] decodedString = Base64.decode(images.getString(resId),
						Base64.DEFAULT);
				Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0,
						decodedString.length);
				bmp = Bitmap.createScaledBitmap(bmp, w, h, true);
				cache.put(id, bmp);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
