package org.earth.texturing;

import java.net.URL;
import java.nio.Buffer;

public abstract class Tile {
	public int x;
	public int y;
	public Buffer image;
	public int failed;
	public URL imagesrc;
	
	public Tile(int x, int y, int failed) {
		this.x = x;
		this.y = y;
		this.failed = failed;
	}

	public abstract void onload();
	
	public abstract void onerror();
}
