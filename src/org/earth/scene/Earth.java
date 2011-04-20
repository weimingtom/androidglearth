package org.earth.scene;

import org.earth.ShaderBank;
import org.earth.gl.Context;
import org.earth.gl.SegmentedPlane;
import org.earth.gl.Shader;
import org.earth.texturing.GenericTileProvider;
import org.earth.texturing.MapQuestTileProvider;
import org.earth.texturing.TileProvider;

import android.opengl.GLES20;
import android.util.Log;

public class Earth {
	private static String TAG = "Earth";
	/**
	 * TODO: define this somewhere else?
	 * 
	 * Average radius of Earth in meters.
	 */
	public static final float EARTH_RADIUS = 6371009;

	/**
	 * Enable terrain rendering.
	 */
	public static final boolean TERRAIN = true;

	/**
	 * Defines how many zoom levels the terrain is "delayed" -
	 *         for texture level 8 we don't need level 8 terrain.
	 */
	public static final int TERRAIN_ZOOM_DIFFERENCE = 3;

	public Context context;

	public Scene scene;

	private TileProvider currentTileProvider_;

	private ClipStack clipStackA_;

	public boolean terrain;

	private GenericTileProvider terrainProvider_;

	private ClipStack clipStackT_;

	/**
	 * This equals 1 << this.scene.getZoom() !
	 */
	public int tileCount;
	public int [] offset = {0, 0};
	private SegmentedPlane[] segPlanes_;
	public LocatedProgram locatedProgram;

	public Earth(Scene scene, TileProvider opt_tileProvider) throws Exception {
		this.context = scene.context;
		this.scene = scene;
		if (opt_tileProvider != null) {
			this.currentTileProvider_ = opt_tileProvider;
		} else {
			this.currentTileProvider_ = new MapQuestTileProvider();
		}
		this.clipStackA_ = new ClipStack(this.currentTileProvider_,
				this.context, 8, 3, 1, 19);
		this.terrain = TERRAIN && this.context.isVTFSupported();

		if (this.terrain) {
			this.terrainProvider_ = new GenericTileProvider("CleanTOPO2",
					"http://webglearth.googlecode.com/svn/resources/terrain/CleanTOPO2/"
							+ "{z}/{x}/{y}.png", 3, 5, 256);
			this.clipStackT_ = new ClipStack(this.terrainProvider_,
					this.context, 2, 3, 2, 5);
		} else {
			Log.w(TAG, "VTF not supported..");
		}

		this.changeTileProvider(this.currentTileProvider_, true);

		this.tileCount = 1;
		SegmentedPlane [] segPlanes_  = {new SegmentedPlane(this.context, 1, 1, 1, false), 	//0
		                     new SegmentedPlane(this.context, 4, 4, 16, true), 				//1
		                     new SegmentedPlane(this.context, 6, 6, 8, true),  				//2
		                     new SegmentedPlane(this.context, 8, 8, 8, true),  				//3
		                     new SegmentedPlane(this.context, 10, 10, 8, false),      		//4
		                     new SegmentedPlane(this.context, 32, 32, 8, false)};
		this.segPlanes_ = segPlanes_;
		
		String fragmentShaderCode = ShaderBank.getShaderCode("earth-fs.glsl");
		String vertexShaderCode = ShaderBank.getShaderCode("earth-vs.glsl");
		
		  vertexShaderCode = vertexShaderCode.replace("%BUFFER_SIDE_FLOAT%",
				  String.valueOf(org.earth.Utils.toFixed(this.getBufferSideSize_(true),1)));

			  vertexShaderCode = vertexShaderCode.replace("%TERRAIN_BOOL%",
			      this.terrain ? "1" : "0");
			  if (this.terrain) {
			    vertexShaderCode = vertexShaderCode.replace("%BUFFER_SIDE_T_FLOAT%",
			        String.valueOf(org.earth.Utils.toFixed(this.getBufferSideSize_(true),1)));
			  }
			  int fsshader = Shader.create(this.context, fragmentShaderCode,
			      GLES20.GL_FRAGMENT_SHADER);
			  int vsshader = Shader.create(this.context, vertexShaderCode,
				  GLES20.GL_VERTEX_SHADER);

			  int shaderProgram = GLES20.glCreateProgram();
			  if (shaderProgram == 0) {
			    throw new Exception("Unknown");
			  }
			  GLES20.glAttachShader(shaderProgram, vsshader);
			  GLES20.glAttachShader(shaderProgram, fsshader);

			  GLES20.glBindAttribLocation(shaderProgram, 0, "aVertexPosition");

			  GLES20.glLinkProgram(shaderProgram);
			  int[] linked = new int[1];
			  GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS,linked,0);
			  if (linked[0] == 0) {
			    throw new Exception("Shader program err: " + GLES20.glGetProgramInfoLog(shaderProgram));
			  }

			  /**
			   * @type {!we.scene.LocatedProgram}
			   */
			  this.locatedProgram = new LocatedProgram(shaderProgram,
			                                                    this.context, this.terrain);
	}

	private float getBufferSideSize_(boolean opt_terrain) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void changeTileProvider(TileProvider currentTileProvider_2,
			boolean b) {
		// TODO Auto-generated method stub

	}

	public TileProvider getCurrentTileProvider() {
		// TODO Auto-generated method stub
		return currentTileProvider_;
	}

	public void draw() {
		// TODO Auto-generated method stub

	}

}
