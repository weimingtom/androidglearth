package org.earth.texturing;

import java.net.URL;

import org.earth.texturing.Tile.State;

import android.util.Log;

public abstract class TileProvider {
	protected static final String TAG = "TileProvider";

	public volatile int loadingTileCounter;
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
	
	public abstract void appendCopyrightContent();

	public boolean loadTile(final Tile tile) {
		Tile t = new Tile(tile.zoom, tile.x, tile.y, tile.failed) {

			@Override
			public void onload(TileProvider tileprovider) {
				this.state = Tile.State.LOADED;
				tileprovider.loadingTileCounter--;
				tileprovider.tileLoadedHandler(this);
				tile.image = this.image;
				tile.state = Tile.State.LOADED;
				tile.onload(TileProvider.this);
			}

			@Override
			public void onerror(TileProvider tileprovider) {

				Log.e(TAG, "Error loading tile: " + this.getKey() + " ("
						+ tileprovider.name + ")");

				this.state = Tile.State.ERROR;
				tileprovider.loadingTileCounter--;
				tile.state = Tile.State.ERROR;
				tile.onerror(TileProvider.this);
			}

		};
		tile.state =  State.LOADING;
		t.imagesrc = this.getTileURL(tile.zoom, tile.x, tile.y);
		t.load(this);

		Log.i(TAG, "Loading tile " + t.getKey());

		this.loadingTileCounter++;

		return true;
	}
}
