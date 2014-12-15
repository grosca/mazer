package org.spatialia.santa.logic;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.json.JSONObject;
import org.spatialia.santa.MainActivity;
import org.spatialia.santa.Sprite;

import android.content.Context;

public class LevelManager {

	private Context context;
	private Settings settings;

	private int level = -1;
	private Level currLevel;

	private int giftsCollected;

	public JSONObject getJSONLevel(int level) {
		JSONObject json = null;
		try {
			InputStream is = null;
			String filePath = "level." + level;
			if (Arrays.asList(context.getAssets().list("")).contains(filePath)) {
				is = context.getAssets().open("level." + level);
			} else {
				is = new FileInputStream(context.getFilesDir()
						.getAbsolutePath() + "/" + filePath);
			}

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			json = new JSONObject(new String(buffer, "UTF-8"));

		} catch (Exception ex) {
			return null;
		}
		return json;
	}

	public void init(Context context) {
		this.context = context;
		this.settings = new Settings(context);

		restoreGameState();
	}

	public int getGiftsCollected() {
		return giftsCollected;
	}

	public void onLevelComplete() {
		((MainActivity) context).loadNextLevel();
	}

	// saves everything in settings
	public void saveGameState() {
		settings.setInt(Settings.LEVEL, level);
		settings.setInt(Settings.GIFTS + level, giftsCollected);
	}

	// restores everything from settings
	public void restoreGameState() {
		level = settings.getInt(Settings.LEVEL);
		giftsCollected = settings.getInt(Settings.GIFTS + level);
	}

	public Level getLevel(int level) {
		this.level = level;
		try {
			currLevel = new Level(level, getJSONLevel(level));
		} catch (Exception ex) {
			currLevel = null;
		}
		return currLevel;
	}

	public Level getNextLevel() {
		currLevel = getLevel(++level);
		return currLevel;
	}

	public int getCurrentLevel() {
		return level + 1;
	}

	public Level getCurrLevel() {
		if (currLevel == null) {
			currLevel = getLevel(settings.getInt(Settings.LEVEL));
		}
		return currLevel;
	}

	// TODO: run on non ui thread
	public Sprite loadResources(int w, int h) {
		synchronized (currLevel) {
			currLevel.saveState(settings);
			Sprite mainCharacter = currLevel.loadResources(
					context.getResources(), w, h);
			currLevel.restoreState(settings);
			return mainCharacter;
		}
	}

	public void giftCollected() {
		giftsCollected++;
	}

	public void uninit() {
		if (currLevel != null) {
			currLevel.saveState(settings);
		}
		saveGameState();
	}
}
