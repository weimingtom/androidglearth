package org.earth;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.earth.gl.Context;
import org.earth.gl.MyGLUtils;
import org.earth.scene.Scene;
import org.earth.ui.SceneDragger;
import org.earth.ui.SceneZoomer;

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

	private SceneDragger sceneDragger;

	private SceneZoomer sceneZoomer;

    GLES20Renderer(Activity activity, EarthGLSurfaceView mGLSurfaceView) {
        mActivity = activity;
        this.mGLSurfaceView = mGLSurfaceView;
        mTimer = new Timer();
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        mTimer.addMark();
        mTimer.logFPS();
        
        this.context.renderFrame();
        
        MyGLUtils.runGlRunnables();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
    	if(context == null)
    		context = new Context(mActivity, mGLSurfaceView, null);
        context.setPerspective(50.0f, 0.000001f, 5.0f);
    	try {
    		if(scene == null)
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
		if(this.sceneDragger == null) {
			this.sceneDragger = new SceneDragger(scene);
		}
		sceneDragger.handleTouch(event);
		if(this.sceneZoomer == null) {
			this.sceneZoomer = new SceneZoomer(scene);
		}
		sceneZoomer.handleTouch(event);
		//mProgramme1.handleTouch(event);
		
	}

}
