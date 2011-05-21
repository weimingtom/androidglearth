package org.earth.scene;

import org.earth.gl.Context;
import org.earth.gl.MyGLUtils;

import android.opengl.GLES20;

public class LocatedProgram {
	public int program;
	public int bufferL0Uniform;
	public int bufferL1Uniform;
	public int bufferL2Uniform;
	public int bufferLnUniform;
	public int metaL0Uniform;
	public int metaL1Uniform;
	public int metaL2Uniform;
	public int levelOffsetsUniform;
	public int degradationTUniform;
	public int bufferL0TUniform;
	public int bufferL1TUniform;
	public int bufferLnTUniform;
	public int metaL0TUniform;
	public int metaL1TUniform;
	public int levelOffsetsTUniform;
	public int vertexPositionAttribute;
	public int textureCoordAttribute;
	public int mvpMatrixUniform;
	public int tileCountUniform;
	public int offsetUniform;

	public LocatedProgram(int program, Context context, boolean terrain) {
		this.program = program;

		this.vertexPositionAttribute = GLES20.glGetAttribLocation(this.program,
				"aVertexPosition");
		GLES20.glEnableVertexAttribArray(this.vertexPositionAttribute);

		this.textureCoordAttribute = GLES20.glGetAttribLocation(this.program,
				"aTextureCoord");
		GLES20.glEnableVertexAttribArray(this.textureCoordAttribute);

		this.mvpMatrixUniform = this.getValidatedUniformLocation_(this.program,
				"uMVPMatrix");

		this.metaL0Uniform = this.getValidatedUniformLocation_(this.program,
				"uMetaL0");

		this.metaL1Uniform = this.getValidatedUniformLocation_(this.program,
				"uMetaL1");

		this.metaL2Uniform = this.getValidatedUniformLocation_(this.program,
				"uMetaL2");

		this.levelOffsetsUniform = this.getValidatedUniformLocation_(
				this.program, "uOffL");

		this.bufferL0Uniform = this.getValidatedUniformLocation_(this.program,
				"uBufferL0");

		this.bufferL1Uniform = this.getValidatedUniformLocation_(this.program,
				"uBufferL1");

		this.bufferL2Uniform = this.getValidatedUniformLocation_(this.program,
				"uBufferL2");

		this.bufferLnUniform = this.getValidatedUniformLocation_(this.program,
				"uBufferLn");
		
		this.tileCountUniform = this.getValidatedUniformLocation_(
				this.program, "uTileCount");
		
		this.offsetUniform = this.getValidatedUniformLocation_(
				this.program, "uOffset");

		if (terrain) {
			this.degradationTUniform = this.getValidatedUniformLocation_(
					this.program, "uDegradationT");
			this.metaL0TUniform = this.getValidatedUniformLocation_(
					this.program, "uMetaL0T");
			this.metaL1TUniform = this.getValidatedUniformLocation_(
					this.program, "uMetaL1T");
			this.levelOffsetsTUniform = this.getValidatedUniformLocation_(
					this.program, "uOffLT");
			this.bufferL0TUniform = this.getValidatedUniformLocation_(
					this.program, "uBufferL0T");
			this.bufferL1TUniform = this.getValidatedUniformLocation_(
					this.program, "uBufferL1T");
			this.bufferLnTUniform = this.getValidatedUniformLocation_(
					this.program, "uBufferLnT");
		}
	}

	private int getValidatedUniformLocation_(int program, String name) {
		int result = GLES20.glGetUniformLocation(program, name);
		MyGLUtils.checkGlError("glGetUniformLocation");
//		if (result == 0) {
//			Log.w(TAG, "Invalid name " + name);
//		}
		return result;
	}

}
