package org.earth.ui;

import org.earth.gl.MyGLUtils;
import org.earth.scene.Scene;

import android.view.MotionEvent;

public class SceneDragger {
	private Scene scene_;
	private boolean dragging_;
	private float oldX_;
	private float oldY_;

	public SceneDragger(Scene scene) {
		this.scene_ = scene;
	}

	public void handleTouch(MotionEvent e) {

		// Stop inertial animation
		// if (this.inertialAnimation_) {
		// this.inertialAnimation_.stop(false);
		// this.inertialAnimation_.dispose();
		// this.inertialAnimation_ = null;
		// }

		if (e.getAction() != MotionEvent.ACTION_DOWN && oldX_ != 0.0f && oldY_ != 0.0f && e.getPointerCount() == 1) {
			onMouseMove_(e);
		} else {
			this.oldX_ = e.getX();
			this.oldY_ = e.getY();
		}

		this.dragging_ = true;

		// Unregister onMouseMove_
		// if (!goog.isNull(this.listenKey_)) {
		// goog.events.unlistenByKey(this.listenKey_);
		// this.listenKey_ = null;
		// }

		// Register onMouseMove_
		// this.listenKey_ = goog.events.listen(
		// goog.dom.getOwnerDocument(this.scene_.context.canvas),
		// goog.events.EventType.MOUSEMOVE,
		// goog.bind(this.onMouseMove_, this));

		// e.preventDefault();
	}

	public void onMouseMove_(MotionEvent e) {
		final int xDiff = (int) (e.getX() - this.oldX_);
		final int yDiff = (int) (e.getY() - this.oldY_);

		MyGLUtils.runOnGlThread(new Runnable() {
			@Override
			public void run() {
				scenePixelMove_(xDiff, yDiff, false);
			}
		});

		this.oldX_ = e.getX();
		this.oldY_ = e.getY();
	}

	/**
	 * Move the scene in given direction defined in actial window pixel
	 * coordinates
	 * 
	 * @param {number} xDiff Difference of position in pixels in x-axis.
	 * @param {number} yDiff Difference of position in pixels in y-axis.
	 * @param {boolean} tilt Tilt?
	 * @private
	 */
	public void scenePixelMove_(int xDiff, int yDiff, boolean tilt) {
		if (tilt) {
			this.scene_.camera.tilt += ((float) yDiff / (float) this.scene_.context.canvas
					.getHeight()) * Math.PI / 2.0f;
			this.scene_.camera.heading += ((float) xDiff / (float) this.scene_.context.canvas
					.getWidth()) * Math.PI;
		} else {
			// PI * (How much is 1px on the screen?) * (How much is visible?)
			float factor = (float) (Math.PI
					* (1.0f / (float) this.scene_.context.canvas.getHeight()) * (this.scene_.tilesVertically / Math
					.pow(2, this.scene_.camera.getZoom())));

			// camera transformations
			rotateAxes(xDiff, yDiff, this.scene_.camera.roll);
			yDiff /= Math.max(Math.abs(Math.cos(this.scene_.camera.tilt)), 0.1);
			rotateAxes(xDiff, yDiff, -this.scene_.camera.heading);

			this.scene_.camera.setPosition(this.scene_.camera.getLatitude()
					+ yDiff * factor, this.scene_.camera.getLongitude() - xDiff
					* 2 * factor);

		}
	}

	// TODO
	public void rotateAxes(int xDiff, int yDiff, float angle) {
		int x = xDiff;
		xDiff = (int) (x * Math.cos(angle) - yDiff * Math.sin(angle));
		yDiff = (int) (x * Math.sin(angle) + yDiff * Math.cos(angle));
	}
}
