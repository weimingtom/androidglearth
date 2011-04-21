package org.earth.texturing;

import java.net.URL;
import java.nio.Buffer;

public abstract class Tile implements Comparable<Tile> {
	public int x;
	public int y;
	public Buffer image;
	public int failed;
	public State state;
	public URL imagesrc;
	public int zoom;

	public Tile(int x, int y, int failed) {
		this.x = x;
		this.y = y;
		this.failed = failed;

		this.state = State.PREPARING;
	}

	public String createKey(int zoom, int x, int y) {
		return zoom + "_" + x + "_" + y;
	}

	public String getKey() {
		return createKey(this.zoom, this.x, this.y);
	}
	
	public void dispose() {
		// delete this.image;
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

	public void load() {
		// TODO Auto-generated method stub

	}
}
