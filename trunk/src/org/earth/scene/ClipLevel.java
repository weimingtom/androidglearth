package org.earth.scene;

import java.util.ArrayList;
import java.util.Arrays;

import org.earth.Utils;
import org.earth.gl.Context;
import org.earth.texturing.Tile;
import org.earth.texturing.TileCache;
import org.earth.texturing.TileProvider;

public class ClipLevel {

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
		for (int y = 0; y < this.side_; ++y) {
			float[] array = { this.side_ };
			this.metaBuffer[y] = array;
		}
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
				// TODO
//				if (diffX > 0) {
//					for (int i = 0; i < this.side_; ++i) {
//						Utils.splice(this.metaBuffer[i], 0, diffX);
//						this.metaBuffer[i]=.push.apply(this.metaBuffer[i],
//								new Array(diffX));
//					}
//				}
//				if (diffX < 0) {
//					for (int i = 0; i < this.side_; ++i) {
//						Utils.splice(this.metaBuffer[i], this.side_ + diffX,
//								-diffX);
//						this.metaBuffer[i].unshift.apply(this.metaBuffer[i],
//								new Array(-diffX));
//					}
//				}
//				if (diffY > 0) {
//					for (int i = 0; i < diffY; ++i) {
//						this.metaBuffer.shift();
//						this.metaBuffer.push(new Array(this.side_));
//					}
//				}
//				if (diffY < 0) {
//					for (int i = 0; i < -diffY; ++i) {
//						this.metaBuffer.pop();
//						this.metaBuffer.unshift(new Array(this.side_));
//					}
//				}
			}
		}
		this.needTiles_();
	}

	private void needTiles_() {
		// TODO Auto-generated method stub

	}

	public void changeTileProvider(TileProvider tileprovider) {

	}

	public int processTiles(int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getBufferRequestCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
