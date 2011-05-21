package org.earth.texturing;

import java.net.MalformedURLException;
import java.net.URL;

import org.earth.Utils;

public class OSMTileProvider extends TileProvider {

	public OSMTileProvider() {
		super("OpenStreetMaps");
	}
	
	public OSMTileProvider(String name) {
		super(name);
	}

	@Override
	public int getMaxZoomLevel() {
		return 18;
	}

	@Override
	public int getTileSize() {
		return 256;
	}

	@Override
	public URL getTileURL(int zoom, int x, int y) {
		StringBuffer buff = new StringBuffer();
		buff.append("http://").append(Utils.randomElement("a", "b", "c"))
			.append(".tile.openstreetmap.org/").append(zoom).append("/")
			.append(x).append("/").append(y).append(".png");
		try {
			return new URL(buff.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void tileLoadedHandler(Tile tile) {
		// TODO Auto-generated method stub
		System.out.println("TILE_LOADED");
	}

	@Override
	public void appendCopyrightContent() {
//		  goog.dom.append(element, '© ',
//			      goog.dom.createDom('a',
//			      {href: 'http://www.openstreetmap.org/'},
//			      'OpenStreetMap'),
//			      ' contributors, CC-BY-SA');
	}

}
