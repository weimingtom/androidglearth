package org.earth.scene;

import org.earth.geom.Vec3;
import org.earth.gl.Context;
import org.earth.gl.MyGLUtils;
import org.earth.texturing.TileProvider;

public class Scene {
	public static final float MIN_ZOOM = 1;

	public Context context;
	public boolean infobox_;
	public boolean tpCopyrightElement_;
	public boolean tpLogoImg_;
	public boolean additionalCopyright_;
	public Earth earth;
	public float tilesVertically;
	public Camera camera;

	public Scene(Context context, boolean opt_infobox,
			boolean opt_copyrightbox, boolean opt_logobox,
			TileProvider opt_tileProvider, boolean opt_copyright)
			throws Exception {
		this.context = context;
		this.context.scene = this;
		this.infobox_ = opt_infobox;
		this.tpCopyrightElement_ = opt_copyrightbox;
		this.tpLogoImg_ = opt_logobox;
		this.additionalCopyright_ = opt_copyright;
		this.earth = new Earth(this, opt_tileProvider);
		this.tilesVertically = 0;
		this.camera = new Camera(this);

		this.recalcTilesVertically();
		this.updateCopyrights();
	}

	public void updateCopyrights() {
		// TODO Auto-generated method stub

	}

	public float getMinZoom() {
		return Math.max(MIN_ZOOM, this.earth.getCurrentTileProvider()
				.getMinZoomLevel());
	}

	public float getMaxZoom() {
		return this.earth.getCurrentTileProvider().getMaxZoomLevel();
	}

	public void recalcTilesVertically() {
		this.tilesVertically = (float) (0.9 * this.context.canvas.getHeight() / this.earth
				.getCurrentTileProvider().getTileSize());
	}

	public void draw() {
		this.earth.draw();
	}

	public float[] traceDistance_(Vec3 origin, Vec3 direction) {
		try {
			Vec3 sphereCenter = ((Vec3) origin.clone()).invert(); // [0,0,0] -
																	// origin

			float ldotc = Vec3.dot(direction, sphereCenter);
			float cdotc = Vec3.dot(sphereCenter, sphereCenter);

			float val = ldotc * ldotc - cdotc + 1.0f;

			if (val < 0) {
				return null;
			} else {
				float d1 = (float) Math.min(ldotc + Math.sqrt(val), ldotc
						- Math.sqrt(val));
				float d2 = (float) Math.max(ldotc + Math.sqrt(val), ldotc
						- Math.sqrt(val));
				float[] array = { d1, d2 };
				return array;
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public float[] getLatLongForXY(float x, float y, boolean opt_radians) {
		Vec3 orig = MyGLUtils.unprojectPoint(x, y, 0, this.context.mvpmInverse,
				this.context.viewportWidth, this.context.viewportHeight);
		Vec3 dir = MyGLUtils.unprojectPoint(x, y, 1, this.context.mvpmInverse,
				this.context.viewportWidth, this.context.viewportHeight);

		if (orig == null || dir == null)
			return null;

		dir.subtract(orig);
		dir.normalize();

		float[] ds = this.traceDistance_(orig, dir);

		if (ds == null) {
			return null;
		}
		Vec3 bod = Vec3.sum(orig, dir.scale(ds[0]));

		float lon = (float) Math.asin(bod.x / Math.sqrt(1.0f - bod.y * bod.y));

		if (bod.z < 0) // The point is on the "other side" of the sphere
			lon = (float) (Math.PI - lon);

		if (opt_radians == true) {
			float[] array = { (float) Math.asin(bod.y),
					org.earth.Utils.standardLongitudeRadians(lon) };
			return array;
		} else {
			float[] array = {
					(float) Math.toDegrees(Math.asin(bod.y)),
					(float) Math.toDegrees(org.earth.Utils
							.standardLongitudeRadians(lon)) };
			return array;
		}
	}

	/**
	 * Calculates screen-space coordinates for given geo-space coordinates.
	 * 
	 * @param {number} lat Latitude in degrees.
	 * @param {number} lon Longitude in degrees.
	 * @return {?Array.<number>} Array [x, y, visibility] or null.
	 */
	public float[] getXYForLatLon(float lat, float lon) {
		lat = (float) Math.toRadians(lat);
		lon = (float) Math.toRadians(lon);

		float cosy = (float) Math.cos(lat);
		Vec3 point = new Vec3((float) Math.sin(lon) * cosy,
				(float) Math.sin(lat), (float) Math.cos(lon) * cosy);
		float[] m2 = { point.x, point.y, point.z, 1 };
		float[] result = MyGLUtils.multMatrixVector(this.context.mvpm, m2);

		if (result[3] == 0)
			return null;

		result = MyGLUtils.multMatrixFloat(result, 1 / result[3]);

		/** @type {number} */
		float x = ((result[0]) + 1) / 2 * this.context.viewportWidth;
		/** @type {number} */
		float y = ((result[1]) - 1) / (-2) * this.context.viewportHeight;

		/** @type {number} */
		float visibility = 1;

		if (x < 0 || x > this.context.viewportWidth || y < 0
				|| y > this.context.viewportHeight) {
			visibility = 0;
		} else {
			Vec3 cameraPos = MyGLUtils.unprojectPoint(0.5f, 0.5f, 0,
					this.context.mvpmInverse, 1, 1);

			if (cameraPos == null)
				return null;

			float distance = Vec3.distance(point, cameraPos);
			Vec3 direction = point.subtract(cameraPos).normalize();
			float[] ds = this.traceDistance_(cameraPos, direction);

			if (ds == null) {
				visibility = 0; // Wait.. what? This should never happen..
			} else {
				visibility = (Math.abs(distance - ds[0]) < Math.abs(distance
						- ds[1])) ? 1 : 0;
			}
		}
		float[] array = { x, y, visibility };
		return array;
	};

	public static float projectLatitude(float latitude) {
		return (float) Math.log(Math.tan(latitude / 2.0f + Math.PI / 4.0f));
	};

	public static float unprojectLatitude(float latitude) {
		return (float) (2.0f * Math.atan(Math.exp(latitude)) - Math.PI / 2.0f);
	};
}
