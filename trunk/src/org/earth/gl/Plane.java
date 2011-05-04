package org.earth.gl;

import android.opengl.GLES20;

public class Plane extends Mesh {
	
	public Plane(Context context, int width, int height) {
		 /** @inheritDoc */
		  this.vertexBuffer = MyGLUtils.createBuffer(12*4);

		  /** @inheritDoc */
		  this.texCoordBuffer = MyGLUtils.createBuffer(8*4);

		  GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vertexBuffer.bufferId);
		  
		  float [] vertices = {
		    width, height, 0.0f,
		    0.0f, height, 0.0f,
		    width, 0.0f, 0.0f,
		    0.0f, 0.0f, 0.0f
		  };
		  
		  vertexBuffer.buffer.asFloatBuffer().put(vertices);
		  
		  GLES20.glBufferData(
			  GLES20.GL_ARRAY_BUFFER,
			  vertexBuffer.buffer.capacity(),
			  vertexBuffer.buffer,
		      GLES20.GL_STATIC_DRAW
		  );
		  this.vertexBuffer.itemSize = 3;
		  this.vertexBuffer.numItems = 4;


		  GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.texCoordBuffer.bufferId);

		  float [] coords = {
		    1.0f, 1.0f,
		    0.0f, 1.0f,
		    1.0f, 0.0f,
		    0.0f, 0.0f
		  };
		  
		  texCoordBuffer.buffer.asFloatBuffer().put(coords);
		  
		  GLES20.glBufferData(
			GLES20.GL_ARRAY_BUFFER,
			texCoordBuffer.buffer.capacity(),
			texCoordBuffer.buffer,
			GLES20.GL_STATIC_DRAW
		  );
		  this.texCoordBuffer.itemSize = 2;
		  this.texCoordBuffer.numItems = 4;
	}
}
