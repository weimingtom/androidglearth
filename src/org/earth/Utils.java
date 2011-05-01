package org.earth;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Random;

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

	/**
	 * The splice() method adds and/or removes elements to/from an array
	 * @param fs
	 * @param index
	 * @param howmany
	 * @param elems
	 * @return the modified array
	 */
	public static float[] splice(float[] src, int index, int howmany, float ... elems) {
		float[] temp = null;
		if(elems != null) {
			// TODO
			temp = new float[src.length-howmany+elems.length];
			System.arraycopy(src, 0, temp, 0, index);
			System.arraycopy(elems, 0, temp, index, elems.length);
			System.arraycopy(src, index, temp, index+elems.length, src.length-index-howmany);
		} else {
			// TODO
			temp = new float[src.length-howmany];
			System.arraycopy(src, 0, temp, 0, index);
			System.arraycopy(src, index, temp, index, src.length-index-howmany);
		}
		return temp;
	}
	
	public static float[] push(float[] src, float ... elems) {
		if(elems != null) {
			float[] temp = new float[src.length+elems.length];
			System.arraycopy(src, 0, temp, 0, src.length);
			return temp;
		} else {
			return src;
		}
	}
	
	public static void unshift(float[] fs, int i) {
		// TODO Auto-generated method stub
		
	}

	public static <E> E shift(List<E> arrayList) {
		E obj = arrayList.get(0);
		arrayList.remove(0);
	    return obj;
	}
	
	public static <E> E[] shift(Class<E> c, E[] src) {
		@SuppressWarnings("unchecked")
		E[] temp = (E[]) Array.newInstance(c, src.length-1);
		System.arraycopy(src, 1, temp, 0, temp.length);
		return temp;
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
