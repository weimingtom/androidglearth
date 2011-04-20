package org.earth.scene;

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
			this.levels_[z] = new ClipLevel(tileprovider, context, side, z);
		}

		for (int n = 0; n < buffers; ++n) {
			this.levels_[n].buffer = this.buffers_[n];
		}

		this.buffersOffset_ = 0;

		this.leveln = new ClipLevelN(tileprovider, context, 2);
	}

	public float getSideLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getQueueSizesText() {
		// TODO Auto-generated method stub
		return null;
	}

	public void changeTileProvider(TileProvider currentTileProvider_) {
		// TODO Auto-generated method stub

	}

	public void moveCenter(float f, float g, int floor) {
		// TODO Auto-generated method stub

	}

	public int getBuffer(float terrainZoom, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float [] getMeta(int zoomLevel, int fallback) {
		// TODO Auto-generated method stub
		if (zoomLevel > this.maxLevel_)
		    Log.w(TAG,"zoomLevel too high");
		  if (zoomLevel - fallback < this.minLevel_) {
		    return new float[this.side_ * this.side_];
		  }
		  return org.earth.Utils.flatten(
		      this.levels_[zoomLevel - this.minLevel_ - fallback].metaBuffer);
	}

	public float[] getOffsets(int zoom, int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
