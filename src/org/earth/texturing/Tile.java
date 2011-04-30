package org.earth.texturing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class Tile implements Comparable<Tile> {
	public int x;
	public int y;
	public Bitmap image;
	public int failed;
	public State state;
	public URL imagesrc;
	public int zoom;
	public long requestTime;

	public Tile(int x, int y, int failed) {
		this.x = x;
		this.y = y;
		this.failed = failed;

		this.state = State.PREPARING;
	}

	public static String createKey(int zoom, int x, int y) {
		return zoom + "_" + x + "_" + y;
	}

	public String getKey() {
		return createKey(this.zoom, this.x, this.y);
	}

	public void dispose() {
		// delete this.image;
		image.recycle();
	}

	public int compareTo(Tile t) {
		return this.zoom == t.zoom ? (this.x == t.x ? this.y - t.y : this.x
				- t.x) : this.zoom - t.zoom;
	}

	public abstract void onload(TileProvider tileprovider);

	public abstract void onerror(TileProvider tileprovider);

	public enum State {
		LOADED, ERROR, LOADING, PREPARING
	}

	public void load(final TileProvider tileprovider) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is;

					is = imagesrc.openStream();
					
					try {
						image = BitmapFactory.decodeStream(is);
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							// Ignore.
						}
					}
				} catch (Exception e) {
					onerror(tileprovider);
				}
				onload(tileprovider);
			}
		}, "Tile loader thread");
		thread.start();
	}
}
