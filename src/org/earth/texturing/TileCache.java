package org.earth.texturing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.earth.Utils;

import android.util.Log;

public class TileCache {

	public static final String TAG = "TileCache";
	private HashMap<String, Tile> tileMap_;
	private ArrayList<Tile> loadRequests_;
	private int targetSize;
	private Timer cleanTimer_;
	private long tileProviderResetTime_;
	private TileProvider tileProvider_;

	public TileCache(TileProvider tileprovider) {

		this.tileMap_ = new HashMap<String, Tile>();
		this.targetSize = 512;
		this.cleanTimer_ = new Timer();

		cleanTimer_.schedule(new TimerTask() {

			@Override
			public void run() {
				cleanCache();
			}
		}, 20000, 20000);

		this.setTileProvider(tileprovider);
	}

	/**
	 * Change TileProvider on-the-fly
	 * 
	 * @param {!we.texturing.TileProvider} tileprovider TileProvider to be set.
	 */
	public void setTileProvider(TileProvider tileprovider) {
		this.tileProviderResetTime_ = new Date().getTime();
		this.tileProvider_ = tileprovider;

		// TODO
		// this.tileProvider_.tileLoadedHandler = goog.bind(this.tileLoaded_,
		// this);

		this.tileMap_.clear();
		this.loadRequests_ = new ArrayList<Tile>();
	}

	/**
	 * Removes LRU tiles from cache
	 */
	public void cleanCache() {
		List<Tile> cleanable = Utils.filter(this.tileMap_.values(),
				new Utils.CollectionFilter<Tile>() {
					@Override
					public boolean filter(Tile tile, int i,
							Collection<Tile> coll) {
						return tile.state == Tile.State.LOADED
								|| tile.state == Tile.State.PREPARING
								|| tile.state == Tile.State.ERROR;
					}
				});

		Collections.sort(cleanable, new Comparator<Tile>() {
			@Override
			public int compare(Tile tile1, Tile tile2) {
				return (int) (tile1.requestTime - tile2.requestTime);
			}
		});

		while (this.tileMap_.size() > this.targetSize && cleanable.size() > 0) {
			Tile tile = Utils.shift(cleanable);
			if (tile.state == Tile.State.PREPARING) {
				this.loadRequests_.remove(tile);
			}
			this.tileMap_.remove(tile.getKey());
			tile.dispose();
		}
	}

	/**
	 * Returns the tile from cache if available.
	 * 
	 * @param {string} key Key.
	 * @return {we.texturing.Tile} Tile from cache.
	 */
	public Tile getTileFromCache(String key) {
		return this.tileMap_.get(key);
	};

	public class CachedTile extends Tile {

		public CachedTile(int zoom, int x, int y, long requestTime) {
			super(zoom, x, y, 0);
		}

		/**
		 * Callback for loaded tiles.
		 */
		@Override
		public void onload(TileProvider tileprovider) {
			// To prevent caching late-arriving tiles.
			if (this.requestTime < tileProviderResetTime_) {

				Log.d(TAG, "Ignoring late tile..");

				this.state = Tile.State.ERROR;
				tileMap_.remove(this.getKey());
				this.dispose();
				return;
			}

			// TODO
			// tileCachedHandler(this);
		}

		@Override
		public void onerror(TileProvider tileprovider) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Returns tile from cache or starts loading it if not available
	 * 
	 * @param {number} zoom Zoom.
	 * @param {number} x X.
	 * @param {number} y Y.
	 * @param {number} requestTime Time of the request, used as priority.
	 * @return {!we.texturing.Tile} Requested tile.
	 */
	public Tile retrieveTile(int zoom, int x, int y, long requestTime) {
		String key = Tile.createKey(zoom, x, y);
		Tile tile = this.getTileFromCache(key);
		if (tile == null) {
			tile = new CachedTile(zoom, x, y, requestTime);
			this.tileMap_.put(key, tile);
			this.loadRequests_.add(tile);
		} else if (tile.state == Tile.State.ERROR) {
			if (requestTime - tile.requestTime > 7000) {
				// tile failed some time ago -> retry

				tile.dispose();

				tile = new CachedTile(zoom, x, y, requestTime);
				this.tileMap_.put(key, tile);
				this.loadRequests_.add(tile);
			}
		} else {
			tile.requestTime = requestTime;
		}
		return tile;
	}

	/**
	 * Tries to update tile's request time. If the tile is not present in cache,
	 * this function has no sideeffect.
	 * 
	 * @param {string} key Tile's key.
	 * @param {number} requestTime Request time to be set.
	 */
	public void updateRequestTime(String key, long requestTime) {
		Tile tile = this.getTileFromCache(key);
		if (tile != null) {
			tile.requestTime = requestTime;
		}
	}

	/**
	 * Removes old tiles from queue
	 * 
	 * @param {number} timeLimit Time limit in ms.
	 */
	public void purgeNotLoadedTiles(long timeLimit) {
		long time = new Date().getTime() - timeLimit;
		while (this.loadRequests_.size() > 0
				&& this.loadRequests_.get(0).requestTime < time) {
			Tile tile = Utils.shift(this.loadRequests_);
			this.tileMap_.remove(tile.getKey());
			tile.dispose();
		}
	}

	/**
	 * Ensures that the right amount of tiles is loading.
	 * 
	 * @param {number} tilesToBeLoading Number of tiles to be should be loading.
	 */
	public void processLoadRequests(int tilesToBeLoading) {
		Collections.sort(this.loadRequests_, new Comparator<Tile>() {
			@Override
			public int compare(Tile tile1, Tile tile2) {
				return (int) (tile1.requestTime - tile2.requestTime);
			}
		});

		int n = Math.min(this.loadRequests_.size(), tilesToBeLoading
				- this.tileProvider_.loadingTileCounter);
		for (int i = 0; i < n; i++) {
			Tile tile = Utils.pop(this.loadRequests_);
			if (!this.tileProvider_.loadTile(tile)) {
				this.loadRequests_.add(tile);
			}
		}
	};

}
