package org.earth.scene;

import org.earth.texturing.TileProvider;

public class Earth {
	/**
	 * TODO: define this somewhere else?
	 * @define {number} Average radius of Earth in meters.
	 */
	public static final float EARTH_RADIUS = 6371009;
	
	/**
	 * @define {boolean} Enable terrain rendering.
	 */
	public static final boolean TERRAIN = true;
	
	/**
	 * @define {number} Defines how many zoom levels the terrain is "delayed" -
	 *                  for texture level 8 we don't need level 8 terrain.
	 */
	public static final int TERRAIN_ZOOM_DIFFERENCE = 3;

	public Earth(Scene scene, TileProvider opt_tileProvider) {
		// TODO Auto-generated constructor stub
	}

	public TileProvider getCurrentTileProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public void draw() {
		// TODO Auto-generated method stub
		
	}

}
