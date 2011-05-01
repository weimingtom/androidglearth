package org.earth.texturing;

import java.net.URL;

public class GenericTileProvider extends TileProvider {
	
	public GenericTileProvider(String name, String string2, int i, int j,
			int k) {
		super(name);
	}

	@Override
	public int getMaxZoomLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public URL getTileURL(int zoom, int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void tileLoadedHandler(Tile tile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void appendCopyrightContent() {
		// TODO Auto-generated method stub
		
	}

}
