package org.spatialia.santa.logic;

import java.util.ArrayList;
import java.util.List;

import org.spatialia.santa.R;
import org.spatialia.santa.Sprite;

import android.content.Context;
import android.graphics.Point;

public class LevelManager {

	private Context context;
	private Settings settings;

	private int level = -1;
	private Level currLevel;

	private int giftsCollected;

	private List<int[][]> levels = new ArrayList<int[][]>();

	private List<Point> bkMixes = new ArrayList<Point>();
	private List<Point> spMixes = new ArrayList<Point>();

	public void init(Context context) {
		this.context = context;
		this.settings = new Settings(context);
		builtIn();
		restoreGameState();
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

	public int getGiftsCollected() {
		return giftsCollected;
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
			currLevel = new Level(level, levels.get(level), bkMixes.get(level
					% bkMixes.size()).x, bkMixes.get(level % bkMixes.size()).y,
					spMixes.get(level % spMixes.size()).x, spMixes.get(level
							% spMixes.size()).y);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return currLevel;
	}

	public Level getNextLevel() {
		currLevel = getLevel(++level);
		return currLevel;
	}

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
		currLevel.saveState(settings);
		saveGameState();
	}

	private void builtIn() {
		bkMixes.add(new Point(R.drawable.grass, R.drawable.wall));
		bkMixes.add(new Point(R.drawable.grass2, R.drawable.wall2));
		bkMixes.add(new Point(R.drawable.grass1, R.drawable.wall4));
		bkMixes.add(new Point(R.drawable.grass3, R.drawable.wall3));
		bkMixes.add(new Point(R.drawable.grass2, R.drawable.wall4));

		spMixes.add(new Point(R.drawable.monster_sprite,
				R.drawable.gift_sprite2));
		spMixes.add(new Point(R.drawable.troll_sprite, R.drawable.gift_sprite));
		spMixes.add(new Point(R.drawable.troll_sprite_two,
				R.drawable.gift_sprite));
		spMixes.add(new Point(R.drawable.troll_sprite, R.drawable.gift_sprite));

		// 12 x 12
		levels.add(new int[][] { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 4, 0, 0, 0, 4, 1, 5, 0, 0, 3, 1 },
				{ 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 1, 4, 1, 0, 0, 0, 4, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 2, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 4, 5, 1, 0, 0, 4, 1 },
				{ 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1 },
				{ 1, 4, 0, 0, 0, 0, 1, 0, 0, 0, 5, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } });

		levels.add(new int[][] { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1 },
				{ 1, 0, 0, 4, 0, 0, 0, 0, 0, 1, 0, 1 },
				{ 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1 },
				{ 1, 0, 5, 0, 0, 0, 1, 4, 0, 1, 4, 1 },
				{ 1, 4, 1, 0, 0, 2, 1, 0, 0, 1, 0, 1 },
				{ 1, 1, 1, 0, 1, 1, 1, 4, 0, 1, 0, 1 },
				{ 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1 },
				{ 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 5, 0, 0, 0, 0, 0, 1, 1, 0, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } });

		// 20 x 20
		levels.add(new int[][] {
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 4, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 4, 1 },
				{ 1, 0, 1, 1, 0, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 4, 1, 0, 1 },
				{ 1, 4, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 4, 1, 0, 1 },
				{ 1, 1, 1, 0, 0, 1, 4, 0, 0, 0, 4, 0, 0, 0, 1, 0, 1, 1, 0, 1 },
				{ 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 4, 1, 0, 5, 0, 0, 1 },
				{ 1, 0, 1, 5, 1, 0, 0, 1, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 1, 1, 1, 0, 1, 1, 4, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1 },
				{ 1, 4, 1, 0, 4, 0, 1, 0, 0, 0, 0, 5, 1, 0, 1, 0, 1, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 4, 0, 1 },
				{ 1, 0, 1, 0, 1, 0, 1, 4, 0, 0, 0, 0, 0, 4, 1, 0, 1, 4, 0, 1 },
				{ 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1 },
				{ 1, 0, 1, 0, 4, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1 },
				{ 1, 4, 1, 5, 1, 0, 0, 4, 0, 0, 1, 5, 0, 0, 0, 1, 0, 4, 0, 1 },
				{ 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 4, 1, 3, 0, 0, 1, 0, 1 },
				{ 1, 4, 1, 1, 4, 1, 1, 0, 0, 4, 1, 4, 0, 1, 4, 0, 0, 5, 4, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } });

		// 25 x 25
		levels.add(new int[][] {
				{ 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 0, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 4, 0,
						0, 1, 1, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1,
						0, 0, 1, 0, 0 },
				{ 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0,
						1, 0, 1, 1, 1 },
				{ 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0,
						1, 0, 0, 4, 1 },
				{ 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 4,
						1, 1, 1, 0, 1 },
				{ 1, 0, 0, 1, 5, 1, 1, 1, 0, 1, 1, 0, 1, 5, 1, 0, 0, 3, 0, 1,
						1, 0, 1, 0, 1 },
				{ 1, 0, 0, 5, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1,
						0, 0, 1, 0, 1 },
				{ 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 2, 1, 0,
						4, 0, 1, 0, 1 },
				{ 1, 0, 0, 1, 0, 1, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
						1, 0, 1, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 0,
						1, 0, 1, 0, 1 },
				{ 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
						1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
						1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 4, 0, 0, 0, 0, 0, 0, 0,
						1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
						1, 1, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 1, 0,
						1, 0, 4, 0, 1 },
				{ 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0,
						1, 1, 0, 1, 1 },
				{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,
						0, 1, 0, 1, 0 },
				{ 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1,
						1, 1, 0, 1, 0 },
				{ 1, 0, 1, 0, 1, 0, 0, 1, 4, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0,
						0, 0, 4, 1, 0 },
				{ 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1,
						1, 1, 0, 1, 0 },
				{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0,
						0, 1, 0, 1, 0 },
				{ 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
						0, 1, 0, 1, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 1, 1, 1,
						0, 1, 0, 1, 0 },
				{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
						1, 1, 1, 1, 0 } });

		// 30 x 17
		levels.add(new int[][] {
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 1, 4, 4, 1, 5, 1, 4, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 1, 0, 4, 0, 4, 0, 0, 1, 1, 1, 0, 1, 4, 4, 1, 0, 1, 0, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 4, 4, 0, 0, 0, 0, 1, 0,
						0, 0, 4, 0, 0, 4, 0, 1, 0, 1 },
				{ 1, 0, 1, 4, 5, 1, 1, 1, 5, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1,
						1, 1, 0, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 0, 5, 1, 0, 1, 0, 0, 4, 0, 4, 0, 4, 0, 0, 0,
						1, 0, 0, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0,
						1, 0, 0, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 5, 1, 5, 1, 5, 1, 4, 0,
						1, 0, 0, 1, 0, 1, 0, 1, 4, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
						1, 1, 0, 1, 0, 4, 0, 1, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
						0, 4, 0, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1,
						0, 1, 0, 1, 0, 1, 0, 1, 5, 1 },
				{ 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
						0, 1, 0, 1, 0, 1, 0, 1, 1, 1 },
				{ 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 0, 1, 0, 0, 0, 1 },
				{ 1, 0, 0, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
						0, 5, 0, 0, 4, 1, 4, 1, 0, 1 },
				{ 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 4, 0, 0, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } });

		// 17 x 30
		levels.add(new int[][] {
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 4, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 4, 1 },
				{ 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 5, 1, 1, 0, 1 },
				{ 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
				{ 1, 1, 0, 1, 1, 1, 0, 1, 4, 0, 0, 4, 0, 0, 1, 0, 1 },
				{ 1, 1, 0, 1, 0, 1, 4, 1, 0, 0, 1, 1, 0, 1, 1, 4, 1 },
				{ 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0, 1 },
				{ 1, 1, 1, 0, 0, 1, 0, 4, 0, 0, 0, 4, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 4, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 4, 1 },
				{ 1, 0, 1, 0, 0, 1, 1, 1, 0, 4, 1, 1, 1, 0, 1, 0, 1 },
				{ 1, 0, 1, 1, 1, 1, 5, 1, 1, 0, 0, 0, 0, 4, 1, 0, 1 },
				{ 1, 4, 0, 0, 0, 0, 0, 4, 1, 0, 1, 1, 1, 1, 1, 0, 1 },
				{ 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 5, 1, 0, 0, 1, 0, 1 },
				{ 1, 0, 1, 0, 3, 1, 0, 2, 0, 0, 1, 1, 0, 1, 1, 0, 1 },
				{ 1, 0, 1, 0, 1, 5, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1 },
				{ 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 4, 1, 1, 4, 0, 1 },
				{ 1, 0, 1, 1, 0, 0, 1, 1, 5, 1, 1, 1, 1, 0, 0, 0, 1 },
				{ 1, 4, 0, 1, 1, 0, 0, 0, 1, 4, 0, 0, 0, 0, 1, 0, 1 },
				{ 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1 },
				{ 1, 0, 0, 1, 1, 5, 1, 0, 1, 0, 0, 4, 0, 1, 1, 4, 1 },
				{ 1, 1, 4, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1 },
				{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 0, 1, 5, 1, 0, 4, 0, 5, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 0, 1, 1, 5, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 4, 0, 0, 0, 1, 0, 0, 0, 5, 1, 0, 1, 4, 1, 0, 1 },
				{ 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1 },
				{ 1, 0, 0, 4, 0, 1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 4, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } });
	}
}
