package org.earth;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.earth.gl.Context;
import org.earth.gl.MyGLUtils;
import org.earth.scene.Scene;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * GLES20Renderer: the OGLES 2.0 Thread.
 */
public class GLES20Renderer implements GLSurfaceView.Renderer {

    private Activity mActivity;

    private Scene scene;

    private Timer mTimer;
    
	private EarthGLSurfaceView mGLSurfaceView;

	private Context context;

    GLES20Renderer(Activity activity, EarthGLSurfaceView mGLSurfaceView) {
        mActivity = activity;
        this.mGLSurfaceView = mGLSurfaceView;
        mTimer = new Timer();
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        mTimer.addMark();
        mTimer.logFPS();
        
        scene.draw();
        
        MyGLUtils.runGlRunnables();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
    	context = new Context(mActivity, mGLSurfaceView, null);
    	try {
			scene = new Scene(context, false, false, false, null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	context.resize();
    }

	public void handleTouch(MotionEvent event) {
		//mProgramme1.handleTouch(event);
	}

}
