package org.spatialia.santa;

import java.util.List;

import org.spatialia.santa.util.Perf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
	}

	private Bitmap background;

	private void drawBoard(Tile[][] world, Sprite mainCharacter,
			List<Sprite> sprites, int blocksW, int blocksH, int w, int h,
			Canvas canvas) {
		Perf.start("drawBoard");

		if (world == null) {
			return;
		}

		if (background == null || background.getWidth() != w) {
			background = BitmapFactory.decodeResource(getResources(),
					R.drawable.background);
			background = Bitmap.createScaledBitmap(background, w, h, true);
		}

		int sy = mainCharacter.getY() - blocksH / 2;
		int sx = mainCharacter.getX() - blocksW / 2;

		int saveCount = canvas.save();
		canvas.translate(-(sx * w + mainCharacter.getDeltaX()),
				-(sy * h + mainCharacter.getDeltaY()));

		// TODO: minimize this code
		int ldx = sx - (mainCharacter.getDeltaX() < 0 ? 1 : 0);
		int ldy = sy - (mainCharacter.getDeltaY() < 0 ? 1 : 0);
		int rdx = sx + blocksW + (mainCharacter.getDeltaX() > 0 ? 1 : 0);
		int rdy = sy + blocksH + (mainCharacter.getDeltaY() > 0 ? 1 : 0);

		int visibleTiles = 0;
		for (int ty = ldy; ty < rdy; ty++) {
			for (int tx = ldx; tx < rdx; tx++) {
				Tile block = getTile(world, tx, ty);
				if (block != null) {
					visibleTiles++;
					block.draw(canvas, paint);
				} else {
					canvas.drawBitmap(background, tx * w, ty * h, paint);
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

		if (getWidth() > blocksW * w) {
			canvas.drawRect(blocksW * w, 0, getWidth(), getHeight(), paint);
		}

		canvas.drawRect(0, getHeight() - 40, getWidth(), getHeight(), paint);
		canvas.drawText(String.format("%d ms. %d sprites. %d tiles.",
				Perf.average("drawBoard", 100), visibleSprites, visibleTiles),
				10, getHeight() - 18, textPaint);

		Perf.end("drawBoard");
	}

	private Tile getTile(Tile[][] world, int i, int j) {
		if (i < 0 || j < 0 || j >= world.length || i >= world[0].length) {
			return null;
		}
		return world[j][i];
	}
}
