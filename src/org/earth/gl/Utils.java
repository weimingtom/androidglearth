package org.earth.gl;

import android.opengl.Matrix;

public class Utils {
	public static void multMatrix(float[] m1, float[] m2) {
		// TODO
		float[] m3 = new float[16];
		Matrix.multiplyMM(m3 , 0, m1, 0, m2, 0);
		m1=m3;
	}
}
