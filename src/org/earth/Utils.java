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

}
