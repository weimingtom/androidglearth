package org.earth.scene;

import org.earth.gl.Context;
import org.earth.texturing.Tile;
import org.earth.texturing.TileProvider;

import android.opengl.GLES20;
import android.util.Log;

public class ClipLevelN {

	protected static final String TAG = "ClipLevelN";
	public int texture;
	private int tileCount;
	private int tileSize;
	private TileProvider tileprovider;
	private int zoom;

	public ClipLevelN(TileProvider tileprovider, Context context, int zoom) {
		this.tileprovider = tileprovider;
		this.zoom = zoom;

		int textures[] = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		this.texture = textures[0];

		tileCount = 1 << zoom;
		tileSize = tileprovider.getTileSize();

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		// GLES20.glPixelStorei(gl.UNPACK_FLIP_Y_WEBGL, 1);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, tileSize
				* tileCount, tileSize * tileCount, 0, GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE, null);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

		for (int x = 0; x < tileCount; ++x) {
			for (int y = 0; y < tileCount; ++y) {
				loadPart(x, y);
			}
		}
	}

	public void dispose() {
		int[] a = { this.texture };
		GLES20.glDeleteTextures(1, a, 0);
	}

	// TODO
	public void handleLoadedTile(Tile tile) {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0,
				tile.x * tileSize, (tileCount - tile.y - 1) * tileSize,
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, tile.image);
	}

	// TODO
	public void loadPart(int x, int y) {
		Tile tile = new Tile(x, y, 0) {

			@Override
			public void onload(TileProvider tileprovider) {
				if (!GLES20.glIsTexture(texture))
					return; // late tile
				handleLoadedTile(this);
			}

			@Override
			public void onerror(TileProvider tileprovider) {
				if (!GLES20.glIsTexture(texture))
					return; // late tile
				this.failed++;
				if (this.failed >= 3) {

					Log.w(TAG, "The tile failed to load 3x - giving " + "up. ("
							+ tileprovider.name + ")");
					return;
				}

				this.imagesrc = tileprovider.getTileURL(zoom, x, y);
			}
		};
		tile.imagesrc = tileprovider.getTileURL(zoom, x, y); // start loading
		tile.load();
	}

}
