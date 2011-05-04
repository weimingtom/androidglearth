package org.earth.scene;

import org.earth.Utils;
import org.earth.gl.Context;
import org.earth.texturing.TileProvider;

import android.util.Log;

public class ClipStack {

	private static final String TAG = "ClipStack";
	public ClipLevelN leveln;
	private ClipBuffer[] buffers_;
	private Context context_;
	private int side_;
	private int minLevel_;
	private int maxLevel_;
	private ClipLevel[] levels_;
	private int buffersOffset_;

	/**
	 * @param {!we.texturing.TileProvider} tileprovider TileProvider to be
	 *        cached.
	 * @param {!we.gl.Context} context WebGL context.
	 * @param {number} side Length of one side in tiles side * tileSize has to
	 *        be power of two.
	 * @param {number} buffers Number of stack buffers.
	 * @param {number} minLevel Zoom of the first ClipLevel.
	 * @param {number} maxLevel Zoom of the last ClipLevel.
	 * @constructor
	 */
	public ClipStack(TileProvider tileprovider, Context context, int side,
			int buffers, int minLevel, int maxLevel) {
		this.context_ = context;
		this.side_ = side;
		this.minLevel_ = minLevel;
		this.maxLevel_ = maxLevel;
		int tileSize = tileprovider.getTileSize();

		this.buffers_ = new ClipBuffer[buffers];
		for (int n = 0; n < buffers; ++n) {
			this.buffers_[n] = (new ClipBuffer(context, this.side_ * tileSize,
					this.side_ * tileSize));
		}

		this.levels_ = new ClipLevel[maxLevel + 1 - minLevel];
		for (int z = minLevel; z <= maxLevel; ++z) {
			this.levels_[z-minLevel] = new ClipLevel(tileprovider, context, side, z);
		}

		for (int n = 0; n < buffers; ++n) {
			this.levels_[n].buffer = this.buffers_[n];
		}

		this.buffersOffset_ = 0;

		this.leveln = new ClipLevelN(tileprovider, context, 2);
	}

	/**
	 * Change TileProvider on-the-fly.
	 * 
	 * @param {!we.texturing.TileProvider} tileprovider TileProvider to be set.
	 */
	public void changeTileProvider(TileProvider tileprovider) {
		this.leveln.dispose();
		this.leveln = new ClipLevelN(tileprovider, this.context_, 2);

		int tileSize = tileprovider.getTileSize();
		int size = this.side_ * tileSize;
		for (ClipBuffer b : this.buffers_) {
			b.resize(size, size);
		}
		for (ClipLevel l : this.levels_) {
			l.changeTileProvider(tileprovider);
		}
	}

	/**
	 * This method can be used to move center of this clipstack it also shifts
	 * the buffers when needed and buffers some tiles.
	 * 
	 * @param {number} lat Latitude.
	 * @param {number} lon Longitude.
	 * @param {number} zoomLevel Zoom level. If not in range of this clipstack,
	 *        it gets clamped to the neareset one.
	 */
	public void moveCenter(float lat, float lon, int zoomLevel) {
		zoomLevel = (int) Utils
				.clamp(zoomLevel, this.minLevel_, this.maxLevel_);

		// shift buffers
		int bufferShift = zoomLevel
				- (this.minLevel_ + this.buffersOffset_ + this.buffers_.length - 1);
		while (bufferShift > 0) {
			ClipBuffer freedBuffer = this.levels_[this.buffersOffset_].buffer;
			this.levels_[this.buffersOffset_].disable();
			this.levels_[++this.buffersOffset_ + this.buffers_.length - 1].buffer = freedBuffer;
			bufferShift--;
		}
		while (bufferShift < 0 && this.buffersOffset_ > 0) {
			ClipBuffer freedBuffer = this.levels_[this.buffersOffset_
					+ this.buffers_.length - 1].buffer;
			this.levels_[this.buffersOffset_ + this.buffers_.length - 1]
					.disable();
			this.levels_[--this.buffersOffset_].buffer = freedBuffer;
			bufferShift++;
		}

		// move centers
		int tileCount = 1 << zoomLevel;

		int buffQuota = 1;

		float posX = (float) ((lon / (2 * Math.PI) + 0.5) * tileCount);
		float posY = (float) ((0.5 - Math.log(Math.tan(lat / 2.0 + Math.PI
				/ 4.0))
				/ (Math.PI * 2)) * tileCount);
		for (int i = zoomLevel - this.minLevel_; i >= this.buffersOffset_; i--) {
			this.levels_[i].moveCenter(posX, posY);
			posX /= 2;
			posY /= 2;
			buffQuota -= this.levels_[i].processTiles((buffQuota >= 0) ? 1 : 0,
					5);
		}
	}

	/**
	 * @param {number} zoomLevel Floored Zoom level.
	 * @param {number} fallback Fallback.
	 * @return {Array.<number>} meta data.
	 */
	public float[] getMeta(int zoomLevel, int fallback) {
		if (zoomLevel > this.maxLevel_)
			Log.w(TAG, "zoomLevel too high");
		if (zoomLevel - fallback < this.minLevel_) {
			return new float[this.side_ * this.side_];
		}
		return org.earth.Utils.flatten(this.levels_[zoomLevel - this.minLevel_
				- fallback].metaBuffer);
	}

	/**
	 * @param {number} zoomLevel Floored Zoom level.
	 * @param {number} fallback Fallback.
	 * @return {WebGLTexture} texture.
	 */
	public int getBuffer(int zoomLevel, int fallback) {
		if (zoomLevel > this.maxLevel_)
			Log.w(TAG, "zoomLevel too high");
		if (zoomLevel - fallback < this.minLevel_) {
			return 0;
		}
		return this.levels_[zoomLevel - this.minLevel_ - fallback].buffer.texture;
	}

	/**
	 * @param {number} zoomLevel Floored Zoom level.
	 * @param {number} count Count.
	 * @return {Array.<number>} offset data.
	 */
	public float[] getOffsets(int zoomLevel, int count) {
		if (zoomLevel > this.maxLevel_)
			Log.w(TAG, "zoomLevel too high");
		float[] result = new float[count];
		int n = 0;
		for (int i = zoomLevel - this.minLevel_; i > zoomLevel - this.minLevel_
				- count; --i) {
			result[n] = Math.round(this.levels_[Math.max(0, i)].offX);
			n++;
			result[n] = Math.round(this.levels_[Math.max(0, i)].offY);
			n++;
		}
		return result;
	}

	/**
	 * @return {string} Queue sizes description.
	 */
	public String getQueueSizesText() {
		String result = "";
		for (int i = 0; i < this.levels_.length; ++i) {
			result += (i + this.minLevel_) + ":"
					+ this.levels_[i].getBufferRequestCount() + " ";
		}
		return result;
	}

	/**
	 * @return {number} Length of one side in tiles.
	 */
	public int getSideLength() {
		return this.side_;
	}

}
