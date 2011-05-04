package org.earth.gl;

import org.earth.geom.Vec3;
import org.earth.scene.Scene;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class Context {
	public GLSurfaceView canvas;
	public Object opt_fpsbox;
	public Object opt_onfail;
	public int viewportWidth;
	public int viewportHeight;
	public float fov;
	public float zNear_;
	public float zFar_;
	public float aspectRatio;
	public float[] projectionMatrix = new float[16];
	public float[] modelViewMatrix;
	public float[] mvpm;
	public float[] mvpmInverse;
	public int fps;
	public int averageFrameTime;
	public int lastFpsCalc_;
	public int framesSinceLastFpsCalc_;
	public int frameTimeSinceLastFpsCalc_;
	public Scene scene;
	public Activity activity;

	/**
	 * Object wrapping a GL context.
	 * @param {!Element} canvas Canvas element.
	 * @param {Element=} opt_fpsbox Element to output fps information to.
	 * @constructor
	 */
	public Context(Activity activity, GLSurfaceView canvas, Object opt_fpsbox) {
		this.activity = activity;
		this.canvas = canvas;
		this.opt_fpsbox = opt_fpsbox;

		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClearDepthf(1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);

		this.viewportWidth = canvas.getWidth();
		this.viewportHeight = canvas.getHeight();

		GLES20.glViewport(0, 0, this.viewportWidth, this.viewportHeight);

		this.fov = 0;
		this.zNear_ = 0;
		this.zFar_ = 1;
		this.aspectRatio = this.viewportWidth / this.viewportHeight;
		Matrix.setIdentityM(projectionMatrix, 0);
		this.modelViewMatrix = this.projectionMatrix;
		this.mvpm = this.projectionMatrix;
		this.mvpmInverse = this.projectionMatrix;

		this.fps = 0;
		this.averageFrameTime = 0;
		this.lastFpsCalc_ = 0;
		this.framesSinceLastFpsCalc_ = 0;
		this.frameTimeSinceLastFpsCalc_ = 0;
	}

	public void tryGetContext() {
		// TODO
	}

	public boolean isVTFSupported() {
		int[] arr = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, arr, 0);
		return arr[0] != 0;
	}

	public void setPerspective(float fovy, float zNear, float zFar) {
		this.fov = (float) Math.toRadians(fovy);
		this.zNear_ = zNear;
		this.zFar_ = zFar;
		this.aspectRatio = this.viewportWidth / this.viewportHeight;

		this.setPerspectiveInternal_();
	}

	private void setPerspectiveInternal_() {
		float f = (float) (1 / Math.tan(this.fov / 2));
		float[] tmp = { f / this.aspectRatio, 0, 0, 0, 0, f, 0, 0, 0, 0,
				(this.zFar_ + this.zNear_) / (this.zNear_ - this.zFar_),
				2 * this.zFar_ * this.zNear_ / (this.zNear_ - this.zFar_), 0,
				0, -1, 0 };
		this.projectionMatrix = tmp;
	}
	
	public void resize() {
		  this.viewportWidth = this.canvas.getWidth() ;
		  this.viewportHeight = this.canvas.getHeight();
		  GLES20.glViewport(0, 0, this.viewportWidth, this.viewportHeight);
		  this.aspectRatio = this.viewportWidth / this.viewportHeight;
		  this.setPerspectiveInternal_();
		  this.scene.recalcTilesVertically();
	}
	
	private void loadIdentity() {
		Matrix.setIdentityM(modelViewMatrix, 0);
	}
	
	public void translate(float x, float y, float z) {
		float [] mmmatrix = {
			1, 0, 0, x,
			0, 1, 0, y,
			0, 0, 1, z,
			0, 0, 0, 1
		};
		Utils.multMatrix(this.modelViewMatrix,mmmatrix);
	}
	
	public void rotate(float angle, float x, float y, float z) {
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float [] mmmatrix = {
			x * x * (1 - c) + c, x * y * (1 - c) - z * s, x * z * (1 - c) + y * s, 0,
			y * x * (1 - c) + z * s, y * y * (1 - c) + c, y * z * (1 - c) - x * s, 0,
			z * x * (1 - c) - y * s, z * y * (1 - c) + x * s, z * z * (1 - c) + c, 0,
			0, 0, 0, 1
		};
		Utils.multMatrix(this.modelViewMatrix,mmmatrix);
	}
	
	public void rotate010(float angle) {
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float [] mmmatrix = {
			c, 0, s, 0,
			0, 1, 0, 0,
			-s, 0, c, 0,
			0, 0, 0, 1
		};
		Utils.multMatrix(this.modelViewMatrix,mmmatrix);
	}
	
	public void rotate100(float angle) {
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float [] mmmatrix = {
			1, 0, 0, 0,
			0, c, -s, 0,
			0, s, c, 0,
			0, 0, 0, 1
		};
		Utils.multMatrix(this.modelViewMatrix,mmmatrix);
	}
	
	public void rotate001(float angle) {
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float [] mmmatrix = {
			c, -s, 0, 0,
			s, c, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		};
		Utils.multMatrix(this.modelViewMatrix,mmmatrix);
	}
	
	public void lookAt(Vec3 eye, Vec3 center, Vec3 up) {
		Vec3 fw = center.subtract(eye).normalize();

		Vec3 side = Vec3.cross(fw, up).normalize();
		up = Vec3.cross(side, fw);
		
		float [] mmmatrix = {
			side.x, side.y, side.z, 0, //-eye.x * (side.x + side.y + side.z),
			up.x, up.y, up.z, 0, //-eye.y * (up.x + up.y + up.z)
			-fw.x, -fw.y, -fw.z, 0, //eye.z * (fw.x + fw.y + fw.z)
			0, 0, 0, 1
		};
		Utils.multMatrix(this.modelViewMatrix,mmmatrix);
		
		this.translate(-eye.x, -eye.y, -eye.z);
	}
	
	public float[] flushMVPM() {
		this.mvpm = Utils.multMatrix(this.projectionMatrix,this.modelViewMatrix);
		this.mvpmInverse = Utils.inverseMatrix(this.mvpm);
		return this.mvpm;
	}
	
	public void renderFrame() {
		// TODO
//		if (we.CALC_FPS && !goog.isNull(this.fpsbox_)) {
//		    /** @type {number} */
//		    var time = goog.now();
//		    if (this.lastFpsCalc_ < goog.now() - 2000) {
//		      this.fps = 1000 *
//		          this.framesSinceLastFpsCalc_ / (goog.now() - this.lastFpsCalc_);
//		      this.averageFrameTime =
//		          this.frameTimeSinceLastFpsCalc_ / this.framesSinceLastFpsCalc_;
//		      this.lastFpsCalc_ = goog.now();
//		      this.framesSinceLastFpsCalc_ = 0;
//		      this.frameTimeSinceLastFpsCalc_ = 0;
//
//		      this.fpsbox_.innerHTML =
//		          this.averageFrameTime.toFixed(2) +
//		          ' ms / fps: ' +
//		          this.fps.toFixed(2);
//		    }
//
//		    this.framesSinceLastFpsCalc_++;
//		  }
		
		  GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		  this.loadIdentity();
		 
		  if (this.scene == null) {
			  Log.e(Context.class.getName(), "Scene is not set");
		  }

		  this.scene.draw();

		// TODO
//		  if (we.CALC_FPS && !goog.isNull(this.fpsbox_)) {
//		    this.frameTimeSinceLastFpsCalc_ += goog.now() - time;
//		  }
	}

}
