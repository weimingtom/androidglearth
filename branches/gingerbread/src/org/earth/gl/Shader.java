package org.earth.gl;

import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	private static String TAG = "Shader";
	
	public static int create(Context context, String shaderCode, int shaderType) throws Exception {
		int shader = GLES20.glCreateShader(shaderType);
		
		Log.i(TAG, "Compiling...");
		
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		
		Log.i(TAG, "Info: " + GLES20.glGetShaderInfoLog(shader));
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled, 0);
		 if (compiled[0] == 0) {
			Log.e(TAG, "Could not compile shader " + shaderType + ":");
			String infoLog = GLES20.glGetShaderInfoLog(shader);
			Log.e(TAG, infoLog);
			GLES20.glDeleteShader(shader);
			throw new Exception("Shader err: " + infoLog);
		  } else if (shader == 0) {
		    throw new Exception("Unknown");
		  } else {
			  Log.i(TAG,"Done");
		  }
		 return shader;
	}
}
