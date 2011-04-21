package org.earth.texturing;

import java.net.URL;
import java.nio.Buffer;

public abstract class Tile {
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
	}
	
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract void onload(TileProvider tileprovider);
	
	public abstract void onerror(TileProvider tileprovider);
	
	public enum State {
		LOADED, ERROR, LOADING
		
	}

	public void load() {
		// TODO Auto-generated method stub
		
	}
}
