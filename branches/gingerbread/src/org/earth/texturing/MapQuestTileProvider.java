package org.earth.texturing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MapQuestTileProvider extends OSMTileProvider {

	public MapQuestTileProvider() {
		super("MapQuest OSM");
	}

	@Override
	public URL getTileURL(int zoom, int x, int y) {
		// TODO
		Random rand = new Random();
		StringBuffer buff = new StringBuffer();
		buff.append("http://otile").append(1 +rand.nextInt(3))
			.append(".mqcdn.com/tiles/1.0.0/osm/").append(zoom).append("/")
			.append(x).append("/").append(y).append(".png");
		try {
			System.out.println(buff.toString());
			return new URL(buff.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}




}
