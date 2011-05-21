package org.earth;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class EarthGLSurfaceView extends GLSurfaceView {

	private GLES20Renderer myrenderer;

	public EarthGLSurfaceView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		myrenderer.handleTouch(event);
		return true;
	}
	
	@Override
	public void setRenderer(Renderer renderer) {
		this.myrenderer = (GLES20Renderer) renderer;
		super.setRenderer(renderer);
	}
	
}
