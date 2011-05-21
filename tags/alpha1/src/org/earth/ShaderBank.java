package org.earth;

import android.app.Activity;


public class ShaderBank {

	public static String getShaderCode(Activity mActivity, String filename) {
		String source = Utils.readStringAsset(mActivity, filename);
		return source;
	}

}
