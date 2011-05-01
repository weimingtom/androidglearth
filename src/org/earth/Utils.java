package org.earth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.earth.texturing.Tile;

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

	public static <E> E shift(List<E> arrayList) {
		E obj = arrayList.get(0);
		arrayList.remove(0);
	    return obj;
	}

	public static <E> E pop(List<E> arrayList) {
		E obj = arrayList.get(arrayList.size()-1);
		arrayList.remove(arrayList.size()-1);
	    return obj;
	}
	
	public interface CollectionFilter<E> {
		public boolean filter(E e, int i, Collection<E> coll);
	}

	public static <E> List<E> filter(Collection<E> values, CollectionFilter<E> filter) {
		return null;
	}

	public static <E> E randomElement(E ... elems) {
		Random rand = new Random();
		return elems[rand.nextInt(elems.length-1)];
	}

}
