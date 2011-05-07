package org.earth.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.earth.geom.Vec3;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

public class MyGLUtils {
	private static String TAG = "Utils";

	public static float[] multMatrix(float[] m1, float[] m2) {
		// TODO
		float[] m1i = new float[16];
		Matrix.transposeM(m1i, 0, m1, 0);
		float[] m2i = new float[16];
		Matrix.transposeM(m2i, 0, m2, 0);
		float[] m3 = new float[16];
		
		Matrix.multiplyMM(m3, 0, m1i, 0, m2i, 0);
		
		float[] m3i = new float[16];
		Matrix.transposeM(m3i, 0, m3, 0);
		return m3i;
	}

	public static float[] multMatrixVector(float[] m1, float[] m2) {
		// TODO
		float[] m3 = new float[4];
		float[] m1i = new float[16];
		Matrix.transposeM(m1i, 0, m1, 0);
		
		Matrix.multiplyMV(m3, 0, m1i, 0, m2, 0);
		
		return m3;
	}

	public static float[] inverseMatrix(float[] m1) {
		float[] m2 = new float[16];
		float[] m1i = new float[16];
		Matrix.transposeM(m1i, 0, m1, 0);
		
		Matrix.invertM(m2, 0, m1i, 0);
		
		float[] m2i = new float[16];
		Matrix.transposeM(m2i, 0, m2, 0);
		return m2i;
	}

	public static GLBuffer createBuffer(int size) {
		GLBuffer myBuffer = new GLBuffer();
		myBuffer.buffer = ByteBuffer.allocateDirect(size).order(
				ByteOrder.nativeOrder());
		int buffer[] = new int[1];
		GLES20.glGenBuffers(1, buffer, 0);
		checkGlError("glGenBuffers");
		myBuffer.bufferId = buffer[0];
		return myBuffer;
	}

	public static void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			String errorStr = GLU.gluErrorString(error);
			Log.e(TAG, op + ": glError " + error + " " + errorStr);
			throw new RuntimeException(op + ": glError " + error + " " + errorStr);
		}
	}

	public static float[] toFloatArray(ArrayList<Float> arraylist) {
		float[] tmp = new float[arraylist.size()];
		int i = 0;
		for (float f : arraylist) {
			tmp[i] = f;
			i++;
		}
		return tmp;
	}

	public static int[] toIntArray(ArrayList<Integer> arraylist) {
		int[] tmp = new int[arraylist.size()];
		int i = 0;
		for (int f : arraylist) {
			tmp[i] = f;
			i++;
		}
		return tmp;
	}

	/**
	 * 
	 * @param {number} x Screen-space coordinate X.
	 * @param {number} y Screen-space coordinate Y.
	 * @param {number} z Screen-space coordinate Z (depth).
	 * @param {goog.math.Matrix} invertedMVP Inverted ModelView-Projections
	 *        matrix.
	 * @param {number} viewportWidth Width of viewport in pixels.
	 * @param {number} viewportHeight Height of viewport in pixels.
	 * @return {?goog.math.Vec3} Point location in model-space.
	 */
	public static Vec3 unprojectPoint(float x, float y, float z,
			float[] invertedMVP, int viewportWidth, int viewportHeight) {
		if (invertedMVP == null)
			return null;

		float[] m2 = { x /  ((float)viewportWidth) * 2.0f - 1.0f,
				 1.0f - (2.0f * y) / (float) viewportHeight, // Y axis has to
																// be flipped
				z * 2.0f - 1.0f, 1.0f };
		/**
		 * @type {goog.math.Matrix}
		 */
		float[] result = multMatrixVector(invertedMVP, m2);

		if (result[3] == 0)
			return null;

		result = multMatrixFloat(result, 1.0f / result[3]);

		return new Vec3(/** @type {number} */
		result[0],
		/** @type {number} */
		result[1],
		/** @type {number} */
		result[2]);
	}

	public static float[] multMatrixFloat(float[] result, float f) {
		for (int i = 0; i < result.length; i++) {
			result[i] = f * result[i];
		}
		return result;
	}

	public static float[] transposeMatrix(float[] m) {
		float[] mTrans = new float[m.length];
		Matrix.transposeM(mTrans, 0, m, 0);
		return m;
	}

	private static CopyOnWriteArrayList<Runnable> glRunnables = new CopyOnWriteArrayList<Runnable>();
	public static void runOnGlThread(Runnable runnable) {
		glRunnables.add(runnable);
	}
	
	public static void runGlRunnables() {
		// TODO
		for(Runnable runnable : glRunnables) {
			runnable.run();
		}
		glRunnables.clear();
	}
}
