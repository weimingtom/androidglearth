package org.earth.texturing;

import java.net.URL;

import org.earth.texturing.Tile.State;

import android.util.Log;

public abstract class TileProvider {
	protected static final String TAG = "TileProvider";

	public int loadingTileCounter;
	public String name;

	public TileProvider(String name) {
		this.name = name;
		loadingTileCounter = 0;
	}

	public int getMinZoomLevel() {
		return 0;
	}

	public abstract int getMaxZoomLevel();

	public abstract int getTileSize();

	public abstract URL getTileURL(int zoom, int x, int y);

	public abstract void tileLoadedHandler(Tile tile);

	public boolean loadTile(Tile tile) {
		Tile t = new Tile(tile.x, tile.y, tile.failed) {

			@Override
			public void onload(TileProvider tileprovider) {
				this.state = Tile.State.LOADED;
				tileprovider.loadingTileCounter--;
				tileprovider.tileLoadedHandler(this);
			}

			@Override
			public void onerror(TileProvider tileprovider) {

				Log.e(TAG, "Error loading tile: " + this.getKey() + " ("
						+ tileprovider.name + ")");

				this.state = Tile.State.ERROR;
				tileprovider.loadingTileCounter--;
			}

		};

		t.state = State.LOADING;
		t.imagesrc = this.getTileURL(tile.zoom, tile.x, tile.y);
		t.load(this);

		Log.i(TAG, "Loading tile " + t.getKey());

		this.loadingTileCounter++;

		return true;
	}
}
