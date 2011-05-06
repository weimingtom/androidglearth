package org.earth.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.earth.geom.Vec3;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class MyGLUtils {
	private static String TAG = "Utils";

	public static float[] multMatrix(float[] m1, float[] m2) {
		// TODO
		float[] m3 = new float[16];
		Matrix.multiplyMM(m3, 0, m1, 0, m2, 0);
		m1 = m3;
		return m1;
	}

	public static float[] multMatrixVector(float[] m1, float[] m2) {
		// TODO
		float[] m3 = new float[16];
		Matrix.multiplyMV(m3, 0, m1, 0, m2, 0);
		m1 = m3;
		return m1;
	}

	public static float[] inverseMatrix(float[] m1) {
		float[] m2 = new float[16];
		Matrix.invertM(m2, 0, m1, 0);
		return m2;
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
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
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

		float[] m2 = { x / (float) (viewportWidth * 2 - 1),
				(float) (1 - 2 * y) / (float) viewportHeight, // Y axis has to
																// be flipped
				z * 2 - 1, 1 };
		/**
		 * @type {goog.math.Matrix}
		 */
		float[] result = multMatrixVector(invertedMVP, m2);

		if (result[3] == 0)
			return null;

		result = multMatrixFloat(result, 1 / result[3]);

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
}
