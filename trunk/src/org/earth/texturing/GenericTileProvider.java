package org.earth.texturing;

import java.net.MalformedURLException;
import java.net.URL;

import org.earth.Utils;

public class GenericTileProvider extends TileProvider {

	private int minZoom;
	private int maxZoom;
	private int tileSize;
	private String url;
	private boolean flipY;

	public GenericTileProvider(String name, String url, int minZoom,
			int maxZoom, int tileSize) {
		super(name);
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.tileSize = tileSize;
		this.url = url;
		this.flipY =  true;
	}

	@Override
	public int getMinZoomLevel() {
		return this.minZoom;
	}

	@Override
	public int getMaxZoomLevel() {
		return this.maxZoom;
	}

	@Override
	public int getTileSize() {
		return tileSize;
	}

	@Override
	public URL getTileURL(int zoom, int x, int y) {
		URL end;
		try {
			end = new URL(
				url.replace("{z}", String.valueOf((int)zoom))
			  .replace("{x}", String.valueOf((int)x))
			  .replace("{y}", String.valueOf((int)(this.flipY ? ((1 << zoom) - y - 1) : y)))
			  );
			 return end;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		  if (this.subdomains.length > 0) {
//		    url = url.replace("{sub}",
//		        /** @type {string} */ (we.utils.randomElement(this.subdomains)));
//		  }
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
