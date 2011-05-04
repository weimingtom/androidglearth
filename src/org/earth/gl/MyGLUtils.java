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
		Matrix.multiplyMM(m3 , 0, m1, 0, m2, 0);
		m1=m3;
		return m1;
	}
	
	public static float[] multMatrixVector(float[] m1, float[] m2) {
		// TODO
		float[] m3 = new float[16];
		Matrix.multiplyMV(m3 , 0, m1, 0, m2, 0);
		m1=m3;
		return m1;
	}

	public static float[] inverseMatrix(float[] m1) {
		float[] m2 = new float[16];
		Matrix.invertM(m2,0,m1,0);
		return m2;
	}

	public static GLBuffer createBuffer(int size) {
		GLBuffer myBuffer = new GLBuffer();
		myBuffer.buffer = ByteBuffer
			.allocateDirect(
				size)
			.order(ByteOrder.nativeOrder());
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

	public static float [] toFloatArray(ArrayList<Float> arraylist) {
		float [] tmp = new float [arraylist.size()];
		int i = 0;
		for(float f: arraylist) {
			tmp[i]=f;
			i++;
		}
		return tmp;
	}

	public static int[] toIntArray(ArrayList<Integer> arraylist) {
		int [] tmp = new int [arraylist.size()];
		int i = 0;
		for(int f: arraylist) {
			tmp[i]=f;
			i++;
		}
		return tmp;
	}

	public static Vec3 unprojectPoint(float x, float y, int i,
			float[] mvpmInverse, int viewportWidth, int viewportHeight) {
		// TODO Auto-generated method stub
		return null;
	}

	public static float[] multMatrixFloat(float[] result, float f) {
		for(int i = 0; i<result.length; i++) {
			result[i]=f*result[i];
		}
		return result;
	}

	public static float[] transposeMatrix(float[] flushMVPM) {
		// TODO Auto-generated method stub
		return null;
	}
}
