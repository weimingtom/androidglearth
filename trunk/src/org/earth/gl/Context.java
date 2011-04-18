package org.earth.gl;

import org.earth.geom.Vec3;
import org.earth.scene.Scene;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class Context {
	private GLSurfaceView canvas;
	private Object opt_fpsbox;
	private Object opt_onfail;
	private int viewportWidth;
	private int viewportHeight;
	private float fov;
	private float zNear_;
	private float zFar_;
	private float aspectRatio;
	private float[] projectionMatrix = new float[16];
	private float[] modelViewMatrix;
	private float[] mvpm;
	private float[] mvpmInverse;
	private int fps;
	private int averageFrameTime;
	private int lastFpsCalc_;
	private int framesSinceLastFpsCalc_;
	private int frameTimeSinceLastFpsCalc_;
	private Scene scene;

	public Context(GLSurfaceView canvas, Object opt_fpsbox, Object opt_onfail) {
		this.canvas = canvas;
		this.opt_fpsbox = opt_fpsbox;
		this.opt_onfail = opt_onfail;

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
	
	public void flushMVPM() {
		
	}

	public GLSurfaceView getCanvas() {
		return canvas;
	}

	public void setCanvas(GLSurfaceView canvas) {
		this.canvas = canvas;
	}

	public Object getOpt_fpsbox() {
		return opt_fpsbox;
	}

	public void setOpt_fpsbox(Object opt_fpsbox) {
		this.opt_fpsbox = opt_fpsbox;
	}

	public Object getOpt_onfail() {
		return opt_onfail;
	}

	public void setOpt_onfail(Object opt_onfail) {
		this.opt_onfail = opt_onfail;
	}

}
