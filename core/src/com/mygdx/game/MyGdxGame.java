package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;

public class MyGdxGame extends InputAdapter implements ApplicationListener {
	public PerspectiveCamera cam;
	final float[] startPos = {150f, -9f, 0f};
	public ModelInstance model;
	public ModelInstance instance;
	public ModelBatch modelBatch;
	public Environment environment;
	final float bound = 45f;
	float[] pos = {startPos[0], startPos[1], startPos[2]};
	float[] Vpos = new float[3];
	float speed = 0f;
	float direction = 0f;
	protected Label label;
	protected Label crosshair;
	protected BitmapFont font;
	protected Stage stage;
	protected long startTime;
	protected long hits;
	final float zone = 12f;
	boolean isUnder = false;
	long underFire;
	float angle = 0f;
	float angleD = 0f;
	float angleQ = 0f;
	float Q = 0f;

	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 10f, 10f, 20f));
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, 1920, 1080);
		cam.position.set(startPos[0], startPos[1], startPos[2]);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		JsonReader read = new JsonReader();
		G3dModelLoader loader = new G3dModelLoader(read);
		model = new ModelInstance(loader.loadModel(Gdx.files.internal("hero.g3dj")));
		/*
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createCone(40f, 160f, 40f, 6,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		 */
		instance = new ModelInstance(model);
		instance.transform.setToRotation(Vector3.Z, angle);
		for (int i = 0; i < 3; i++){
			Vpos[i] = getSpeed();
		}
		instance.transform.setToRotation(Vector3.Z, 90).translate(-5,0,0);
		instance.transform.setToScaling(0.1f, 0.1f, 0.1f).translate(0, 400, 0);
		font = new BitmapFont();
		label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		Gdx.input.setInputProcessor(new InputMultiplexer(this));
		stage = new Stage();
		stage.addActor(label);
		startTime = System.currentTimeMillis();
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		angle = (angle + speed) % 360;
		angleD = (angleD + direction) % 360;
		angleQ = (angleQ + Q) % 360;
		instance.transform.rotate(Vector3.Z, speed);
		instance.transform.rotate(Vector3.X, direction);
		instance.transform.rotate(Vector3.Y, Q);
		System.out.println(angle + " " + angleD + " " + angleQ);



		cam.position.set(pos[0], pos[1], pos[2]);
		cam.update();
		System.out.println(speed);
		modelBatch.begin(cam);
		modelBatch.render(instance, environment);
		modelBatch.end();
		StringBuilder builder = new StringBuilder();
		builder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
		long time = System.currentTimeMillis() - startTime;
		builder.append("| Game time: ").append(time);
		label.setText(builder);
		stage.draw();
	}

	private float getSpeed() {
		System.out.println(speed);
		return speed;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public boolean keyDown(int button) {
		boolean isWPressed = Gdx.input.isKeyPressed(Input.Keys.W);
		boolean isAPressed = Gdx.input.isKeyPressed(Input.Keys.A);
		boolean isSPressed = Gdx.input.isKeyPressed(Input.Keys.S);
		boolean isDPressed = Gdx.input.isKeyPressed(Input.Keys.D);
		boolean isQPressed = Gdx.input.isKeyPressed(Input.Keys.Q);
		boolean isEPressed = Gdx.input.isKeyPressed(Input.Keys.E);
		if (isWPressed) {speed = 2f;}
		if (isSPressed) {speed = -2f;}
		if (isAPressed) {direction = -2f;}
		if (isDPressed) {direction = 2f;}
		if (isQPressed) {Q = -2f;}
		if (isEPressed) {Q = 2f;}
		return true;
	}

	@Override
	public boolean keyUp (int keycode) {
		speed = 0f;
		Q = 0f;
		direction = 0f;
		return false;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
	}
}