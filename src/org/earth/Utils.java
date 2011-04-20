package org.earth;

public class Utils {
	public static final float LN2 = 0.6931471805599453f;

	public static float standardLongitudeRadians(float lon) {
		float standard = Utils.modulo(lon, (float) (2 * Math.PI));
		return (float) (standard > Math.PI ? standard - 2 * Math.PI : standard);
	}

	public static float modulo(float a, float b) {
		// TODO
		return a % b;
	}

	public static float clamp(float latitude, double d, double e) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static float toFixed(float bufferSideSize_, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static float[] flatten(float[][] metaBuffer) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void splice(float[] fs, int i, int diffX) {
		// TODO Auto-generated method stub
		
	}

}
