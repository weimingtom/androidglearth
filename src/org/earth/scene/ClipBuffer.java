package org.earth.scene;

import org.earth.gl.Context;
import org.earth.gl.MyGLUtils;

import android.opengl.GLES20;
import android.opengl.GLUtils;

public class ClipBuffer {
	private int width_;
	private int height_;
	public int texture;

	public ClipBuffer(Context context, int width, int height) {
		this.width_ = width;
		this.height_ = height;
		
		int textures[] = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		texture = textures[0];
		
		this.create_();
	}
	
	/**
	 * Resizes this buffer. If dimensions are unchanged, does nothing.
	 * @param {number} width New width.
	 * @param {number} height New height.
	 */
	public void resize(int width, int height) {
	  if (this.width_ == width && this.height_ == height)
	    return;

	  this.width_ = width;
	  this.height_ = height;

	  int textures[] = {texture};
	  GLES20.glDeleteTextures(1,textures,0);

	  textures = new int[1];
	  GLES20.glGenTextures(1, textures, 0);
	  texture = textures[0];

	  this.create_();
	};

	private void create_() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,  this.texture);
		MyGLUtils.checkGlError("glBindTexture");
		
		// TODO
		//GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		//MyGLUtils.checkGlError("glPixelStorei");
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		
		//GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0,GLES20.GL_RGB, null, GLES20.GL_UNSIGNED_SHORT, 0);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,GLES20.GL_RGB, this.width_, this.height_, 0,
				GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
		MyGLUtils.checkGlError("glTexImage2D");

	}
}
