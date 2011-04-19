package org.earth.scene;

import org.earth.geom.Vec3;
import org.earth.gl.Context;
import org.earth.gl.Utils;
import org.earth.texturing.TileProvider;

public class Scene {

	private static final double MIN_ZOOM = 1;
	private Context context;
	private boolean infobox_;
	private boolean tpCopyrightElement_;
	private boolean tpLogoImg_;
	private boolean additionalCopyright_;
	private Earth earth;
	private float tilesVertically;
	private Camera camera;

	public Scene(Context context, boolean opt_infobox,
			boolean opt_copyrightbox, boolean opt_logobox,
			TileProvider opt_tileProvider, boolean opt_copyright) {
		this.context = context;
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

	private void updateCopyrights() {
		// TODO Auto-generated method stub

	}

	public double getMinZoom() {
		return Math.max(MIN_ZOOM, this.earth.getCurrentTileProvider()
				.getMinZoomLevel());
	}

	public double getMaxZoom() {
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

			float val = ldotc * ldotc - cdotc + 1;

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
		Vec3 orig = Utils.unprojectPoint(x, y, 0, this.context.mvpmInverse,
				this.context.viewportWidth, this.context.viewportHeight);
		Vec3 dir = Utils.unprojectPoint(x, y, 1, this.context.mvpmInverse,
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

		float lon = (float) Math.asin(bod.x / Math.sqrt(1 - bod.y * bod.y));

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
}
