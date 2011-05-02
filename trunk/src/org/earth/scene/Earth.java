package org.earth.scene;

import org.earth.ShaderBank;
import org.earth.gl.Context;
import org.earth.gl.SegmentedPlane;
import org.earth.gl.Shader;
import org.earth.gl.Utils;
import org.earth.texturing.GenericTileProvider;
import org.earth.texturing.MapQuestTileProvider;
import org.earth.texturing.TileProvider;

import com.badlogic.gdx.backends.android.AndroidGL20;

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
	 * Defines how many zoom levels the terrain is "delayed" - for texture level
	 * 8 we don't need level 8 terrain.
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
	public float[] offset = { 0f, 0f };
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
		SegmentedPlane[] segPlanes_ = {
				new SegmentedPlane(this.context, 1, 1, 1, false), // 0
				new SegmentedPlane(this.context, 4, 4, 16, true), // 1
				new SegmentedPlane(this.context, 6, 6, 8, true), // 2
				new SegmentedPlane(this.context, 8, 8, 8, true), // 3
				new SegmentedPlane(this.context, 10, 10, 8, false), // 4
				new SegmentedPlane(this.context, 32, 32, 8, false) };
		this.segPlanes_ = segPlanes_;

		String fragmentShaderCode = ShaderBank.getShaderCode("earth-fs.glsl");
		String vertexShaderCode = ShaderBank.getShaderCode("earth-vs.glsl");

		vertexShaderCode = vertexShaderCode.replace(
				"%BUFFER_SIDE_FLOAT%",
				String.valueOf(org.earth.Utils.toFixed(
						this.getBufferSideSize_(true), 1)));

		vertexShaderCode = vertexShaderCode.replace("%TERRAIN_BOOL%",
				this.terrain ? "1" : "0");
		if (this.terrain) {
			vertexShaderCode = vertexShaderCode.replace(
					"%BUFFER_SIDE_T_FLOAT%",
					String.valueOf(org.earth.Utils.toFixed(
							this.getBufferSideSize_(true), 1)));
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
		GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, linked, 0);
		if (linked[0] == 0) {
			throw new Exception("Shader program err: "
					+ GLES20.glGetProgramInfoLog(shaderProgram));
		}

		/**
		 * @type {!we.scene.LocatedProgram}
		 */
		this.locatedProgram = new LocatedProgram(shaderProgram, this.context,
				this.terrain);
	}

	private float getBufferSideSize_(boolean opt_terrain) {
		return (opt_terrain ? this.clipStackT_ : this.clipStackA_)
				.getSideLength();
	}

	public String getInfoText() {
		return "BufferQueue size: " + this.clipStackA_.getQueueSizesText()
				+ "; Loading tiles: "
				+ this.currentTileProvider_.loadingTileCounter;
	}

	private void changeTileProvider(TileProvider tileprovider,
			boolean opt_firstRun) {
		this.currentTileProvider_ = tileprovider;
		this.clipStackA_.changeTileProvider(this.currentTileProvider_);
		// TODO
		// this.currentTileProvider_.copyrightInfoChangedHandler =
		// goog.bind(this.scene.updateCopyrights, this);

		if (!opt_firstRun) {
			this.scene.recalcTilesVertically();
			this.scene.updateCopyrights();

			this.scene.camera.setZoom(this.scene.camera.getZoom()); // revalidate
		}
	}

	public TileProvider getCurrentTileProvider() {
		return currentTileProvider_;
	}

	/**
	 * Calculates which tiles are needed and tries to buffer them
	 * 
	 * @private
	 */
	private void updateTiles_() {
		this.tileCount = 1 << (int) this.scene.camera.getZoom();

		float[] cameraTarget = this.scene.camera.getTarget(this.scene);
		if (cameraTarget == null) {
			// If camera is not pointed at Earth, just fallback to latlon now
			cameraTarget = this.scene.camera.getPosition();
		}
		this.offset[0] = (int) Math.floor(cameraTarget[1] / (2 * Math.PI)
				* this.tileCount);
		this.offset[1] = (int) Math.floor(Scene
				.projectLatitude(cameraTarget[0])
				/ (Math.PI * 2)
				* this.tileCount);

		this.clipStackA_.moveCenter(cameraTarget[0], cameraTarget[1],
				(int) Math.floor(this.scene.camera.getZoom()));
		if (this.terrain) {
			this.clipStackT_
					.moveCenter(
							cameraTarget[0],
							cameraTarget[1],
							(int) (Math.floor(this.scene.camera.getZoom()) - TERRAIN_ZOOM_DIFFERENCE));
		}
	};

	public void draw() {
		this.updateTiles_();

		int zoom = (int) Math.floor(this.scene.camera.getZoom());

		this.tileCount = 1 << zoom;

		this.context.rotate001(-this.scene.camera.roll);
		this.context.rotate100(-this.scene.camera.tilt);
		this.context.rotate001(-this.scene.camera.heading);
		this.context.translate(0, 0, -1 - this.scene.camera.getAltitude()
				/ EARTH_RADIUS);
		this.context.rotate100(this.scene.camera.getLatitude());
		this.context.rotate010(-this.scene.camera.getLongitude());

		GLES20.glUseProgram(this.locatedProgram.program);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
				this.clipStackA_.getBuffer(zoom, 0));
		GLES20.glUniform1i(this.locatedProgram.bufferL0Uniform, 0);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
				this.clipStackA_.getBuffer(zoom, 1));
		GLES20.glUniform1i(this.locatedProgram.bufferL1Uniform, 1);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
				this.clipStackA_.getBuffer(zoom, 2));
		GLES20.glUniform1i(this.locatedProgram.bufferL2Uniform, 2);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
				this.clipStackA_.leveln.texture);
		GLES20.glUniform1i(this.locatedProgram.bufferLnUniform, 3);

		GLES20.glUniform1fv(this.locatedProgram.metaL0Uniform,
				this.clipStackA_.getMeta(zoom, 0).length,
				this.clipStackA_.getMeta(zoom, 0), 0);
		GLES20.glUniform1fv(this.locatedProgram.metaL1Uniform,
				this.clipStackA_.getMeta(zoom, 1).length,
				this.clipStackA_.getMeta(zoom, 1), 0);
		GLES20.glUniform1fv(this.locatedProgram.metaL2Uniform,
				this.clipStackA_.getMeta(zoom, 2).length,
				this.clipStackA_.getMeta(zoom, 2), 0);

		GLES20.glUniform2fv(this.locatedProgram.levelOffsetsUniform,
				this.clipStackA_.getOffsets(zoom, 3).length,
				this.clipStackA_.getOffsets(zoom, 3), 0);

		if (this.terrain) {

			int terrainZoom = (int) org.earth.Utils.clamp(zoom
					- TERRAIN_ZOOM_DIFFERENCE, 2,
					this.terrainProvider_.getMaxZoomLevel());

			GLES20.glUniform1f(this.locatedProgram.degradationTUniform, zoom
					- terrainZoom);

			GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
					this.clipStackT_.getBuffer(terrainZoom, 0));
			GLES20.glUniform1i(this.locatedProgram.bufferL0TUniform, 4);

			GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
					this.clipStackT_.getBuffer(terrainZoom, 1));
			GLES20.glUniform1i(this.locatedProgram.bufferL1TUniform, 5);

			GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
					this.clipStackT_.leveln.texture);
			GLES20.glUniform1i(this.locatedProgram.bufferLnTUniform, 6);

			GLES20.glUniform1fv(this.locatedProgram.metaL0TUniform,
					this.clipStackT_.getMeta(terrainZoom, 0).length,
					this.clipStackT_.getMeta(terrainZoom, 0), 0);
			GLES20.glUniform1fv(this.locatedProgram.metaL1TUniform,
					this.clipStackT_.getMeta(terrainZoom, 1).length,
					this.clipStackT_.getMeta(terrainZoom, 1), 0);

			GLES20.glUniform2fv(this.locatedProgram.levelOffsetsTUniform,
					this.clipStackT_.getOffsets(terrainZoom, 2).length,
					this.clipStackT_.getOffsets(terrainZoom, 2), 0);
		}

		float[] mvpm = Utils.transposeMatrix(this.context.flushMVPM());

		SegmentedPlane plane = this.segPlanes_[Math.min(zoom,
				this.segPlanes_.length - 1)];

		AndroidGL20 agl20 = new AndroidGL20();

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, plane.vertexBuffer.bufferId);
		agl20.glVertexAttribPointer(
				this.locatedProgram.vertexPositionAttribute,
				plane.vertexBuffer.itemSize, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,
				plane.texCoordBuffer.bufferId);
		agl20.glVertexAttribPointer(this.locatedProgram.textureCoordAttribute,
				plane.texCoordBuffer.itemSize, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glUniformMatrix4fv(this.locatedProgram.mvpMatrixUniform,
				mvpm.length, false, mvpm, 0);
		GLES20.glUniform1f(this.locatedProgram.tileCountUniform, this.tileCount);

		GLES20.glUniform2fv(this.locatedProgram.offsetUniform,
				this.offset.length, this.offset, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,
				plane.indexBuffer.bufferId);
		// if (Math.floor(goog.now() / 10000) % 2 === 1)
		agl20.glDrawElements(GLES20.GL_TRIANGLES, plane.numIndices,
				GLES20.GL_UNSIGNED_SHORT, 0);
		Utils.checkGlError("glDrawElements");
		// else
		// gl.drawElements(gl.LINES, plane.numIndices, gl.UNSIGNED_SHORT, 0);
	}

}
