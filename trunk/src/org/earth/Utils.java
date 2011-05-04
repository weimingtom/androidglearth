package org.earth;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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

	public static float clamp(float number, float min, float max) {
		return Math.max(min, Math.min(max, number));
	}

	/**
	 * The toFixed() method formats a number to use a specified number of trailing decimals.
	 * @param bufferSideSize_
	 * @param i
	 * @return
	 */
	public static float toFixed(float number, int i) {
		int tmp = (int) (number*(10^i));
		return ((float)tmp)/(10^i);
	}

	public static float[] flatten(float[][] metaBuffer) {
		int size = 0;
		for (int i = 0; i<metaBuffer.length; i++) {
			size+=metaBuffer[i].length;
		}
		if(size>0) {
			float[] tmp = new float[size];
			size = 0;
			for (int i = 0; i<metaBuffer.length; i++) {
				System.arraycopy(metaBuffer[i], 0, tmp, size, metaBuffer[i].length);
				size+=metaBuffer[i].length;
			}
			return tmp;
		} else
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
			System.arraycopy(elems, 0, temp, src.length, elems.length);
			return temp;
		} else {
			return src;
		}
	}
	
	public static <E> E[] push(Class<E> c, E[] src, E ... elems) {
		if(elems != null) {
			@SuppressWarnings("unchecked")
			E[] temp = (E[]) Array.newInstance(c,src.length+elems.length);
			System.arraycopy(src, 0, temp, 0, src.length);
			return temp;
		} else {
			return src;
		}
	}
	
	/**
	 * The unshift() method adds new elements to the beginning of an array
	 * @param src
	 * @param elems
	 * @return the new array
	 */
	public static float[] unshift(float[] src, float ... elems) {
		if(elems != null) {
			float[] temp = new float[src.length+elems.length];
			System.arraycopy(elems, 0, temp, 0, elems.length);
			System.arraycopy(src, 0, temp, elems.length, src.length);
			return temp;
		} else {
			return src;
		}
	}

	/**
	 * The unshift() method adds new elements to the beginning of an array
	 * @param src
	 * @param elems
	 * @return the new array
	 */
	public static <E> E[] unshift(Class<E> c, E[] src, E ... elems) {
		if(elems != null) {
			@SuppressWarnings("unchecked")
			E[] temp = (E[]) Array.newInstance(c,src.length+elems.length);
			System.arraycopy(elems, 0, temp, 0, elems.length);
			System.arraycopy(src, 0, temp, elems.length, src.length);
			return temp;
		} else {
			return src;
		}
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
		// TODO
		return (List<E>) values;
	}

	public static <E> E randomElement(E ... elems) {
		Random rand = new Random();
		return elems[rand.nextInt(elems.length-1)];
	}

	public static <E> E[] pop(Class<E> class1, E[] metaBuffer) {
		// TODO Auto-generated method stub
		return null;
	}

    /** read an asset file as Text File and return a string */
    public static String readStringAsset(Context context, String filename) {
        try {
            InputStream iStream = context.getAssets().open(filename);
            return readStringInput(iStream);
        } catch (IOException e) {
            Log.e("Utils", "Shader " + filename + " cannot be read");
            return "";
        }
    }

    /** read string input stream */
    public  static String readStringInput(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();

        byte[] buffer = new byte[4096];
        for (int n; (n = in.read(buffer)) != -1;) {
            sb.append(new String(buffer, 0, n));
        }
        return sb.toString();
    }
}
