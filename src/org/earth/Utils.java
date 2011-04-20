package org.earth;

public class Utils {
	public static final float LN2 = 0.6931471805599453f;

	public static float standardLongitudeRadians(float lon) {
		float standard = Utils.modulo(lon, 2 * Math.PI);
		return (float) (standard > Math.PI ? standard - 2 * Math.PI : standard);
	}

	private static float modulo(float lon, double d) {
		// TODO Auto-generated method stub
		return 0;
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

}
