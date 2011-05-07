package org.earth.scene;

public class Camera {
	private Scene scene_;
	private float latitude_;
	private float longitude_;
	private float altitude_;
	private float zoom_;
	public float heading;
	public float tilt;
	public float roll;
	public boolean fixedAltitude;

	public Camera(Scene scene) {
		this.scene_ = scene;
		this.latitude_ = 0;
		this.longitude_ = 0;
		this.altitude_ = 10000000;
		this.zoom_ = 3;
		this.heading = 0;
		this.tilt = 0;
		this.roll = 0;
		this.fixedAltitude = true;
	}

	public void setPositionDegrees(float latitude, float longitude) {
		this.setPosition((float) Math.toRadians(latitude),
				(float) Math.toRadians(longitude));
	}

	public void setPosition(float latitude, float longitude) {
		this.latitude_ = org.earth.Utils.clamp(latitude, -1.5f, 1.5f);
		this.longitude_ = org.earth.Utils.standardLongitudeRadians(longitude);

		if (this.fixedAltitude) {
			this.zoom_ = -1;
			this.dispatchEvent(new CameraEvent(EventType.ZOOMCHANGED));
		} else {
			this.altitude_ = -1;
			this.dispatchEvent(new CameraEvent(EventType.ALTITUDECHANGED));
		}
	}

	public float[] getPositionDegrees() {
		float[] array = { (float) Math.toDegrees(this.latitude_),
				(float) Math.toDegrees(this.longitude_) };
		return array;
	}

	public float[] getPosition() {
		float[] array = { this.latitude_, this.longitude_ };
		return array;
	}

	public float getLatitude() {
		return latitude_;
	}

	public float getLongitude() {
		return longitude_;
	}

	public float getAltitude() {
		if (this.altitude_ == -1) {
			this.calcAltitude_();
		}
		return altitude_;
	}

	public void setAltitude(float altitude) {
		this.altitude_ = org.earth.Utils.clamp(altitude, 250, 10000000);

		if (!this.fixedAltitude) {
			this.calcZoom_(); // recount
		} else {
			this.zoom_ = -1; // invalidate
		}

		this.dispatchEvent(new CameraEvent(EventType.ALTITUDECHANGED));
		this.dispatchEvent(new CameraEvent(EventType.ZOOMCHANGED));
	}

	public void setZoom(float zoom) {
		this.zoom_ = org.earth.Utils.clamp(zoom, this.scene_.getMinZoom(),
				this.scene_.getMaxZoom());

		if (this.fixedAltitude) {
			this.calcAltitude_(); // recount
		} else {
			this.altitude_ = -1; // invalidate
		}

		this.dispatchEvent(new CameraEvent(EventType.ZOOMCHANGED));
		this.dispatchEvent(new CameraEvent(EventType.ALTITUDECHANGED));
	}

	public float getZoom() {
		if (this.zoom_ == -1) {
			this.calcZoom_();
		}
		return this.zoom_;
	}

	public float[] getTarget(Scene scene) {
		// This can be optimized a lot
		return scene.getLatLongForXY((float)scene.context.viewportWidth / 2.0f,
				(float)scene.context.viewportHeight / 2.0f, true);
	}

	private void calcZoom_() {
		float sizeISee = (float) (2 * (this.altitude_ / Earth.EARTH_RADIUS) *
        Math.tan(this.scene_.context.fov / 2));
		float sizeOfOneTile = sizeISee / this.scene_.tilesVertically;
		float o = (float) (Math.cos(Math.abs(this.latitude_)) * 2 * Math.PI);
		
		this.zoom_ = org.earth.Utils.clamp((float)(Math.log(o / sizeOfOneTile) / org.earth.Utils.LN2),
            this.scene_.getMinZoom(),
            this.scene_.getMaxZoom());
	}
	
	private void calcAltitude_() {
		float o = (float) (Math.cos(Math.abs(this.latitude_)) * 2 * Math.PI);
		float thisPosDeformation = (float) (o / Math.pow(2, this.zoom_));
		float sizeIWannaSee = thisPosDeformation * this.scene_.tilesVertically;
		this.altitude_ = (float) ((1 / Math.tan(this.scene_.context.fov / 2)) *
			(sizeIWannaSee / 2) * Earth.EARTH_RADIUS);
	}

	public void dispatchEvent(CameraEvent cameraEvent) {
		// TODO Auto-generated method stub

	}

	public enum EventType {
		ZOOMCHANGED, ALTITUDECHANGED
	}
}
