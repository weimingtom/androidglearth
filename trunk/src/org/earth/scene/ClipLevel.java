package org.earth.scene;

import java.util.ArrayList;
import java.util.Date;

import org.earth.Utils;
import org.earth.gl.Context;
import org.earth.texturing.Tile;
import org.earth.texturing.TileCache;
import org.earth.texturing.TileProvider;

import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class ClipLevel {

	private static final String TAG = "ClipLevel";

	public ClipBuffer buffer;

	/**
	 * Array of rows, row is array of cols -> to get tile x,y you have to get
	 * row first (this.metaBuffer[y][x])
	 * 
	 * @type {Array.<Array.<number>>}
	 */
	public float[][] metaBuffer;

	/**
	 * Texture offset in tiles from [0,0] tile origin
	 * 
	 * @type {number}
	 */
	public int offX;

	/**
	 * Texture offset in tiles from [0,0] tile origin
	 * 
	 * @type {number}
	 */
	public int offY;

	/**
	 * Array of buffer requests - ordered by request time (most recent request
	 * are at the end)
	 * 
	 * @private
	 */
	private ArrayList<Tile> bufferRequests_;

	/**
	 * Buffer width in tiles.
	 * 
	 * @type {number}
	 * @private
	 */
	private int side_;

	private TileProvider tileProvider_;
	private TileCache tileCache_;
	private int zoom_;

	/**
	 * "Cached" 1 << this.zoom_
	 * 
	 * @type {number}
	 * @private
	 */
	private int tileCount_;

	/**
	 * ClipLevel is "degenerated" if the buffer is large enough to store the
	 * whole level.
	 * 
	 * @type {boolean}
	 * @private
	 */
	private boolean degenerated_;

	public ClipLevel(TileProvider tileprovider, Context context, int side,
			int zoom) {
		this.bufferRequests_ = new ArrayList<Tile>();
		this.side_ = side;
		this.tileProvider_ = tileprovider;
		this.tileCache_ = new TileCache(tileprovider);
		this.zoom_ = zoom;
		this.tileCount_ = 1 << this.zoom_;
		this.degenerated_ = this.side_ >= this.tileCount_;
		this.offX = 0;
		this.offY = 0;
		this.buffer = null;

		this.resetMeta_();
	}

	/**
	 * Marks all slots as "not loaded" by creating new, empty metadata.
	 * 
	 * @private
	 */
	private void resetMeta_() {
		this.metaBuffer = new float[side_][side_];
	}

	/**
	 * Disables this level.
	 */
	public void disable() {
		this.bufferRequests_.clear();
		this.resetMeta_();
		this.buffer = null;
	}

	/**
	 * @param {number} centerOffX X offset of the center in tiles.
	 * @param {number} centerOffY Y offset of the center in tiles.
	 */
	public void moveCenter(float centerOffX, float centerOffY) {
		if (!this.degenerated_) {
			int offX = (int) Utils.modulo(
					(int) Math.round(centerOffX - this.side_ / 2),
					this.tileCount_);
			int offY = Math.round(centerOffY - this.side_ / 2);

			int diffX = offX - this.offX;
			int diffY = offY - this.offY;

			if (Math.abs(diffX) > this.tileCount_ / 2) { // It's shorter the
															// other way
				diffX = (int) ((diffX - Math.signum(diffX) * this.tileCount_) % this.tileCount_);
			}

			this.offX = offX;
			this.offY = offY;

			if (Math.abs(diffX) >= this.side_ || Math.abs(diffY) >= this.side_) {
				this.resetMeta_(); // too different - reset everything
			} else {

				if (diffX > 0) {
					for (int i = 0; i < this.side_; ++i) {
						this.metaBuffer[i] = Utils.splice(this.metaBuffer[i],
								0, diffX);
						this.metaBuffer[i] = Utils.push(this.metaBuffer[i],
								diffX);
					}
				}
				// TODO
				if (diffX < 0) {
					for (int i = 0; i < this.side_; ++i) {
						Utils.splice(this.metaBuffer[i], this.side_ + diffX,
								-diffX);
						this.metaBuffer[i] = Utils.unshift(this.metaBuffer[i],
								-diffX);
					}
				}
				if (diffY > 0) {
					for (int i = 0; i < diffY; ++i) {
						this.metaBuffer = Utils.shift(float[].class,
								this.metaBuffer);
						float[] tmp = { this.side_ };
						this.metaBuffer = Utils.push(float[].class,
								this.metaBuffer, tmp);
					}
				}
				if (diffY < 0) {
					for (int i = 0; i < -diffY; ++i) {
						this.metaBuffer = Utils.pop(float[].class,
								this.metaBuffer);
						float[] tmp = { this.side_ };
						this.metaBuffer = Utils.unshift(float[].class,
								this.metaBuffer, tmp);
					}
				}
			}
		}
		this.needTiles_();
	}

	/**
	 * Request all missing tiles to fill the buffer.
	 * 
	 * @private
	 */
	private void needTiles_() {

		// clear buffering queue
		this.bufferRequests_ = new ArrayList<Tile>();

		long batchTime = new Date().getTime();
		for (int d = Math.min(this.side_, this.tileCount_) / 2; d > 0; --d) {
			needAround(batchTime + d, d);
		}
	}

	private void needAround(long batchTime, int d) {
		for (int x = -d; x < d; ++x) {
			needOne(batchTime, x, -d);
			needOne(batchTime, x, d - 1);
		}
		for (int y = -d; y < d; ++y) {
			needOne(batchTime, -d, y);
			needOne(batchTime, d - 1, y);
		}
	}

	/**
	 * need tiles - requeue
	 * 
	 * @param batchTime
	 * @param x
	 * @param y
	 */
	private void needOne(long batchTime, int x, int y) {
		int centerOffset = (this.degenerated_ ? this.tileCount_ : this.side_) / 2;
		x += centerOffset;
		y += centerOffset;
		if (this.metaBuffer[y]!=null && this.metaBuffer[y][x] != 1.0f) { // loaded -> dont touch it !
			this.needTile_(this.offX + x, this.offY + y, batchTime);
		}
	}

	private void needTile_(int x, int y, long requestTime) {
		x = x % this.tileCount_;
		y = y % this.tileCount_;

		Tile tile = this.tileCache_.retrieveTile(this.zoom_, x, y, requestTime);
		if (tile.state == Tile.State.LOADED) {
			// Tile is in the cache -> put it into buffering queue
			this.bufferRequests_.add(tile);
		}
	}

	/**
	 * Change TileProvider on-the-fly. Does NOT take care of resizing underlying
	 * buffer!
	 * 
	 * @param {TileProvider} tileprovider TileProvider to be set.
	 */
	public void changeTileProvider(TileProvider tileprovider) {
		this.tileProvider_ = tileprovider;
		this.tileCache_.setTileProvider(tileprovider);
		this.bufferRequests_ = new ArrayList<Tile>();
		this.resetMeta_();
	}

	/**
	 * Processes tiles - Buffers some tiles and ensures that the right amount of
	 * tiles is loading into the TileCache.
	 * 
	 * @param {number} tilesToBuffer Number of tiles to be buffered.
	 * @param {number} tilesToBeLoading Number of tiles to be should be loading.
	 * @return {number} Number of buffered tiles.
	 */
	public int processTiles(int tilesToBuffer, int tilesToBeLoading) {
		int buffered = 0;
		if (this.bufferRequests_.size() > 0) {
			int n = Math.min(this.bufferRequests_.size(), tilesToBuffer);
			for (int i = 0; i < n; i++) {
				this.bufferTile_(Utils.pop(this.bufferRequests_));
				buffered++;
			}
		}

		this.tileCache_.processLoadRequests(tilesToBeLoading);

		return buffered;
	}

	private void bufferTile_(Tile tile) {
		if (this.buffer == null) {
			Log.w(TAG, "Wanted to buffer tile on level without buffer!");
			return;
		}

		if (tile.zoom != this.zoom_) {
			Log.w(TAG, "Mismatched zoom!");
			return;
		}

		int x = tile.x - this.offX;
		int y = tile.y - this.offY;

		int count = 1 << this.zoom_;
		x = x % count;
		y = y % count;

		if (x < 0 || x >= this.side_ || y < 0 || y >= this.side_) {
			Log.w(TAG, "Tile out of bounds!");
			return;
		}
		
		int tileSize = this.tileProvider_.getTileSize();

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.buffer.texture);

		int xPos = (x + this.offX % this.side_) * tileSize;
		int yPos = (this.side_ - (y + this.offY % this.side_) - 1)
				* tileSize;

		GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, xPos, yPos, tile.image);

		this.metaBuffer[y][x] = 1;
	}

	public int getBufferRequestCount() {
		return this.bufferRequests_.size();
	}

}
