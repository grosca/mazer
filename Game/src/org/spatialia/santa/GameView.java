package org.spatialia.santa;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView {

	private SurfaceHolder surfaceHolder;
	private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	private Paint textPaint = new Paint() {
		{
			setColor(Color.RED);
			setTextSize(20);
			setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
		}
	};
	private BitmapDrawable background;

	public void paint(Tile[][] world, Sprite mainCharacter,
			List<Sprite> sprites, int blocksW, int blocksH, int w, int h) {
		if (surfaceHolder.getSurface().isValid()) {
			Canvas canvas = surfaceHolder.lockCanvas();
			synchronized (this) {
				drawBoard(world, mainCharacter, sprites, blocksW, blocksH, w,
						h, canvas);
			}
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		surfaceHolder = getHolder();
		background = (BitmapDrawable) getResources().getDrawable(
				R.drawable.background);
	}

	private long average = 0;

	private void drawBoard(Tile[][] world, Sprite mainCharacter,
			List<Sprite> sprites, int blocksW, int blocksH, int w, int h,
			Canvas canvas) {
		long start = System.currentTimeMillis();
		if (world == null) {
			return;
		}

		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

		background.setBounds(0, 0, w * blocksW, h * blocksH);
		background.draw(canvas);

		int sy = mainCharacter.getY() - blocksH / 2;
		int sx = mainCharacter.getX() - blocksW / 2;

		int saveCount = canvas.save();
		canvas.translate(-(sx * w + mainCharacter.getDeltaX()),
				-(sy * h + mainCharacter.getDeltaY()));

		// draw tiles; i = y j = x

		// TODO: minimize this code
		int ldx = sx, ldy = sy, rdx = sx + blocksW, rdy = sy + blocksH;

		if (mainCharacter.getDeltaX() > 0) {
			rdx += 1;
		} else if (mainCharacter.getDeltaX() < 0) {
			ldx -= 1;
		}
		if (mainCharacter.getDeltaY() > 0) {
			rdy += 1;
		} else if (mainCharacter.getDeltaY() < 0) {
			ldy -= 1;
		}
		// TODO:

		int visibleTiles = 0;
		for (int ty = ldy; ty < rdy; ty++) {
			for (int tx = ldx; tx < rdx; tx++) {
				Tile block = getTile(world, tx, ty);
				if (block != null) {
					visibleTiles++;
					block.draw(canvas, paint);
				}
			}
		}

		mainCharacter.draw(canvas, paint);

		int visibleSprites = 1;

		for (Sprite s : sprites) {
			boolean isVisible = s.getY() >= ldy && s.getY() <= rdy
					&& s.getX() >= ldx && s.getX() <= rdx;
			if (isVisible) {
				s.draw(canvas, paint);
				visibleSprites++;
			}
		}

		canvas.restoreToCount(saveCount);

		long end = System.currentTimeMillis();
		times.add(end - start);

		if (times.size() > 100) {
			long sum = 0;
			for (Long t : times) {
				sum += t;
			}
			average = sum / times.size();
			times.clear();
		}

		canvas.drawRect(0, getHeight() - 40, getWidth(), getHeight(), paint);
		canvas.drawText(String.format("%d ms. %d sprites. %d tiles.", average,
				visibleSprites, visibleTiles), 10, getHeight() - 18, textPaint);
	}

	private Tile getTile(Tile[][] world, int i, int j) {
		if (i < 0 || j < 0 || j >= world.length || i >= world[0].length) {
			return null;
		}
		return world[j][i];
	}

	private List<Long> times = new ArrayList<Long>();
}
