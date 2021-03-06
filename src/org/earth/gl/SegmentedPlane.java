package org.earth.gl;

import java.util.ArrayList;

import android.opengl.GLES20;

/**
 * Object representing a segmented plane.
 * @param {!we.gl.Context} context WebGL context.
 * @param {number} width Width of plane in segments.
 * @param {number} height Height of plane in segments.
 * @param {number} subdiv Subdivision of each segment.
 * @param {boolean=} opt_nolod If true, the plane is created with same amount of
 *                             subdivision even in more excentric areas.
 */
public class SegmentedPlane extends Mesh {
	
	private ArrayList<Float> vertices_;
	private ArrayList<Float> coords_;
	private ArrayList<Integer> indices_;

	public SegmentedPlane(Context context, int width, int height, int subdiv, boolean opt_nolod) {
		this.vertices_ = new ArrayList<Float>();
		this.coords_ =  new ArrayList<Float>();
		this.indices_ = new ArrayList<Integer>();
		
		//this.generateTile_(0,0,subdiv,[false,true,false,false]);
	  for (float x = ((float)-width) / 2.0f; x < (float)width / 2.0f; ++x)
	  {
	    for (float y = ((float)-height) / 2.0f; y < (float)height / 2.0f; ++y)
	    {
	    	int thisSubdiv = calcSubdiv(opt_nolod, subdiv, x, y);
	      boolean [] doubles = {y + 1 < (float)height / 2.0f &&
	                     calcSubdiv(opt_nolod, subdiv, x, y + 1) > thisSubdiv,
	                     x + 1 < (float)width / 2.0f &&
	                     calcSubdiv(opt_nolod, subdiv, x + 1, y) > thisSubdiv,
	                     y - 1 > (float)-height / 2.0f &&
	                     calcSubdiv(opt_nolod, subdiv, x, y - 1) > thisSubdiv,
	                     x - 1 > (float)-width / 2.0f &&
	                     calcSubdiv(opt_nolod, subdiv, x - 1, y) > thisSubdiv};
	      this.generateTile_(x, y, thisSubdiv, doubles);
	    }
	  }
	  
	  /** @inheritDoc */
	  this.vertexBuffer = MyGLUtils.createBuffer(vertices_.size()*4);
	  this.vertexBuffer.buffer.asFloatBuffer().put(MyGLUtils.toFloatArray(this.vertices_));

	  /** @inheritDoc */
	  this.texCoordBuffer = MyGLUtils.createBuffer(coords_.size()*4);
	  this.texCoordBuffer.buffer.asFloatBuffer().put(MyGLUtils.toFloatArray(this.coords_));

	  /** @inheritDoc */
	  this.indexBuffer = MyGLUtils.createBuffer(indices_.size()*4);
	  this.indexBuffer.buffer.asIntBuffer().put(MyGLUtils.toIntArray(this.indices_));
	  
	  GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vertexBuffer.bufferId);
	  GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.vertexBuffer.buffer.capacity(),
			  this.vertexBuffer.buffer, GLES20.GL_STATIC_DRAW);
	  MyGLUtils.checkGlError("glBufferData");
	  this.vertexBuffer.itemSize = 2;

	  GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.texCoordBuffer.bufferId);
	  GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.texCoordBuffer.buffer.capacity(),
			  this.texCoordBuffer.buffer, GLES20.GL_STATIC_DRAW);
	  MyGLUtils.checkGlError("glBufferData");
	  this.texCoordBuffer.itemSize = 2;

	  GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer.bufferId);
	  GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer.buffer.capacity(),
			  this.indexBuffer.buffer, GLES20.GL_STATIC_DRAW);
	  MyGLUtils.checkGlError("glBufferData");

	  /** @inheritDoc */
	  this.numIndices = this.indices_.size();
	  
	  vertices_.clear();
	  indices_.clear();
	  coords_.clear();
	}
	
	/**
	 * @param {number} offX Offset of the tile.
	 * @param {number} offY Offset of the tile.
	 * @param {number} subdiv Subdivision of this tile.
	 * @param {Array.<boolean>} doubles TRBL.
	 * @private
	 */
	private void generateTile_(float offX, float offY, int subdiv, boolean[] doubles) {
		  /** @type {number} */
		  int offIndices = this.vertices_.size() / 2;

		  for (int y = 0; y <= subdiv; ++y) {
		    for (int x = 0; x <= subdiv; ++x) {
		      this.vertices_.add(offX + (float)x / (float)subdiv);
		      this.vertices_.add(offY + (float)y / (float)subdiv);
		      this.coords_.add((float)x / (float)subdiv);
		      
		      // TODO
			  // v axis texture coordinates are not inverted in android
		      this.coords_.add(1.0f - (float)y / (float)subdiv);
		      //this.coords_.add((float)y / (float)subdiv);
		    }
		  }

		  /** @type {Array.<number>} */
		  int [] additionStarts = {0, 0, 0, 0};
		  if (doubles[0]) { //TOP
		    additionStarts[0] = this.vertices_.size() / 2;
		    for (int x = 0; x < subdiv; ++x) {
		    	this.vertices_.add(offX + (float)(x + 0.5f) / (float)subdiv);
		    	this.vertices_.add(offY + 1.0f);
		    	this.coords_.add((float)((x + 0.5f) / (float)subdiv));
		    	
			    // TODO
				// v axis texture coordinates are inverted in android
		    	this.coords_.add(0.0f);
		    	//this.coords_.add(1.0f);
		    }
		  }
		  if (doubles[1]) { //RIGHT
		    additionStarts[1] = this.vertices_.size() / 2;
		    for (int y = 0; y < subdiv; ++y) {
		      this.vertices_.add(offX + 1.0f);
		      this.vertices_.add(offY + (float)(y + 0.5f) / (float) subdiv);
		      this.coords_.add((float)1);
		      
		      // TODO
			  // v axis texture coordinates are inverted in android
		      this.coords_.add((float)(1 - (y + 0.5f) / (float)subdiv));
		      //this.coords_.add((float) (y + 0.5f) / (float)subdiv);
		    }
		  }
		  if (doubles[2]) { //BOTTOM
		    additionStarts[2] = this.vertices_.size() / 2;
		    for (int x = 0; x < subdiv; ++x) {
		    	this.vertices_.add((float) (offX + (float)(x + 0.5f) / (float)subdiv));
		    	this.vertices_.add((float) offY);
		    	this.coords_.add((float)(x + 0.5f) / (float)subdiv);
		    	
		    	// TODO
			    // v axis texture coordinates are inverted in android
			    this.coords_.add(1.0f);
		    	//this.coords_.add(0.0f);
		    }
		  }
		  if (doubles[3]) { //LEFT
		    additionStarts[3] = this.vertices_.size() / 2;
		    for (int y = 0; y < subdiv; ++y) {
		    	this.vertices_.add((float) offX);
		    	this.vertices_.add(offY + (float)(y + 0.5f) / (float)subdiv);
		    	this.coords_.add(0.0f);
		    	
		    	// TODO
			    // v axis texture coordinates are inverted in android
			    this.coords_.add((float)(1 - (y + 0.5f) / (float)subdiv));
		    	//this.coords_.add((float)(y + 0.5f) / (float)subdiv);
		    }
		  }
		  
		//TRIANGLE version
		  int line = subdiv + 1;
		  for (int y = 0; y < subdiv; ++y) {
		    for (int x = 0; x < subdiv; ++x) {
		      int base = offIndices + y * line + x;
		      this.indices_.add(base);
		      // insert transition triangles
		      boolean bottom = y == 0 && doubles[2];
		      boolean left = x == 0 && doubles[3];
		      if (bottom && !left) {
		        this.indices_.add(additionStarts[2] + x);
		        this.indices_.add(base + line);
		        finishTriangle();
		        this.indices_.add(additionStarts[2] + x);
		      } else if (left && !bottom) {
		        this.indices_.add(base + 1);
		        this.indices_.add(additionStarts[3] + y);
		        finishTriangle();

		        this.indices_.add(additionStarts[3] + y);
		      } else if (left && bottom) {
		        this.indices_.add(additionStarts[2] + x);
		        this.indices_.add(additionStarts[3] + y);
		        finishTriangle();

		        this.indices_.add(additionStarts[3] + y);
		        this.indices_.add(additionStarts[2] + x);
		        this.indices_.add(base + line);
		        finishTriangle();

		        this.indices_.add(additionStarts[2] + x);
		      }
		      this.indices_.add(base + 1);
		      this.indices_.add(base + line);
		      finishTriangle();

		      this.indices_.add(base + line + 1);
		      // insert transition triangles
		      boolean top = y == subdiv - 1 && doubles[0];
		      boolean right = x == subdiv - 1 && doubles[1];
		      if (top && !right) {
		        this.indices_.add(additionStarts[0] + x);
		        this.indices_.add(base + 1);
		        finishTriangle();
		        this.indices_.add(additionStarts[0] + x);
		      } else if (right && !top) {
		        this.indices_.add(base + line);
		        this.indices_.add(additionStarts[1] + y);
		        finishTriangle();

		        this.indices_.add(additionStarts[1] + y);
		      } else if (top && right) {
		        this.indices_.add(additionStarts[0] + x);
		        this.indices_.add(additionStarts[1] + y);
		        finishTriangle();

		        this.indices_.add(additionStarts[1] + y);
		        this.indices_.add(additionStarts[0] + x);
		        this.indices_.add(base + 1);
		        finishTriangle();

		        this.indices_.add(additionStarts[0] + x);
		      }
		      this.indices_.add(base + line);
		      this.indices_.add(base + 1);
		      finishTriangle();
		    }
		  }
	}

	private void finishTriangle() {
		 /*
	    // Useful for debugging - Uncomment this if you want to render
	    // this segplane as gl.LINES instead of gl.TRIANGLES
	    // (gets compiled-out if commented)
	    var last = this.indices_.pop();
	    var prelast = this.indices_.pop();
	    var preprelast = this.indices_.pop();
	    this.indices_.push(preprelast, prelast, prelast, last, last, preprelast);
	    */
	}

	public int nearestLowerPOT(float num) {
		return (int) Math.max(1.0f, Math.pow(2.0f, Math.ceil(Math.log(num) / org.earth.Utils.LN2)));
	}
	
	public int calcSubdiv(boolean nolod, int subdiv, float x, float y) {
		return nolod ? subdiv :
	        nearestLowerPOT((float) ((float)subdiv / Math.max(1, Math.sqrt(x * x + y * y))));
	}
}
