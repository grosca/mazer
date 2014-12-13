package org.spatialia.santa;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Tile {
	protected int x;
	protected int y;
	protected Bitmap bitmap;

	public Tile(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
	}

	public Tile() {
	}

	public void draw(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bitmap, x, y, paint);
	}
}
