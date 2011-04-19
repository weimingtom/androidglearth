package org.earth.gl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {

	public static int load(Context context, URL url) throws Exception {
		// First setup the integer array to hold texture numbers which OpenGL
		// generates
		int texture[] = new int[1];
		
		// Generate and bind to the texture
		GLES20.glGenTextures(1, texture, 0);
		int textureId = texture[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		
		InputStream is = url.openStream();
		Bitmap tmpBmp;
		try {
			tmpBmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Ignore.
			}
		}

		// Setup optional texture parameters
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		Utils.checkGlError("glTexParameterf GL_TEXTURE_MIN_FILTER");
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		Utils.checkGlError("glTexParameterf GL_TEXTURE_MAG_FILTER");

		// Set the texture image
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, tmpBmp, 0);
		Utils.checkGlError("texImage2D");

		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		Utils.checkGlError("glGenerateMipmap");

		tmpBmp.recycle();
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		
		return textureId;
	}
}
