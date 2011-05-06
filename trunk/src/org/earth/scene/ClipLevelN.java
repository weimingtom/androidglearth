package org.earth.scene;

import org.earth.gl.Context;
import org.earth.gl.MyGLUtils;
import org.earth.texturing.Tile;
import org.earth.texturing.TileProvider;

import android.opengl.GLES20;
import android.opengl.GLUtils;
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
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, tileSize
				* tileCount, tileSize * tileCount, 0, GLES20.GL_RGB,
				GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
		MyGLUtils.checkGlError("glTexImage2D");
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
		try {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
			MyGLUtils.checkGlError("glBindTexture");
			
			System.out.println("Format "+GLUtils.getInternalFormat(tile.image));
			System.out.println("Type "+GLUtils.getType(tile.image));
			
			GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0,
					tile.x * tileSize, (tileCount - tile.y - 1) * tileSize, tile.image, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5);	
			MyGLUtils.checkGlError("texSubImage2D");
			tile.image.recycle();
		} catch (Exception e) {
			e.printStackTrace();
			tile.onerror(tileprovider);
		}
		
	}

	// TODO
	public void loadPart(int x, int y) {
		Tile tile = new Tile(x, y, 0) {

			@Override
			public void onload(TileProvider tileprovider) {
				if (!GLES20.glIsTexture(texture))
					return; // late tile
				
				final Tile t =this;
				
				MyGLUtils.runOnGlThread(new Runnable() {
					@Override
					public void run() {
						handleLoadedTile(t);
					}
				});
				
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
		tile.load(tileprovider);
	}

}
