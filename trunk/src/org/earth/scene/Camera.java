package org.earth.scene;

public class Camera {
	public Scene scene_;
	public float latitude_;
	public float longitude_;
	public float altitude_;
	public float zoom_;
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

}
