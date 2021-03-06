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
	public volatile State state;
	public URL imagesrc;
	public int zoom;
	public long requestTime;

	public Tile(int zoom, int x, int y,  int failed) {
		this.x = x;
		this.y = y;
		this.zoom = zoom;
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
		if(image != null)
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
				state = State.LOADING;
				try {
					InputStream is;
					if(imagesrc==null) {
						onerror(tileprovider);
						return;
					}
					is = imagesrc.openStream();
					
					try {
						image = BitmapFactory.decodeStream(is);
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					onerror(tileprovider);
					return;
				}
				state = State.LOADED;
				onload(tileprovider);
			}
		}, "Tile loader thread");
		thread.start();
	}
}
