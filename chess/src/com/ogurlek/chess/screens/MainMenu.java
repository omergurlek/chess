package com.ogurlek.chess.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ogurlek.chess.Chess;
import com.ogurlek.chess.tweens.ButtonTween;

public class MainMenu implements Screen {

	private static final int VIRTUAL_WIDTH = 480;
	private static final int VIRTUAL_HEIGHT = 640;

	OrthographicCamera camera;
	Rectangle viewport;
	Chess game;
	Stage stage;
	BitmapFont black;
	BitmapFont white;
	TextureAtlas atlas;
	Skin skin;
	SpriteBatch batch;
	TextButton button;
	TweenManager manager;
	Label label;
	
	public MainMenu(Chess game){
		this.game = game;
	}
	
	@Override
	public void render(float delta) {
		camera.update();
        camera.apply(Gdx.gl10);
 
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
        
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		manager.update(delta);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		stage.draw();
		batch.end();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void resize(int width, int height) {
		Vector2 newVirtualRes= new Vector2(0f, 0f);
		Vector2 crop = new Vector2(width, height);

		newVirtualRes.set(Scaling.fit.apply((float)VIRTUAL_WIDTH, (float)VIRTUAL_HEIGHT, (float)width, (float)height));

		crop.sub(newVirtualRes);
		crop.mul(.5f);
		
		viewport = new Rectangle(crop.x, crop.y, newVirtualRes.x, newVirtualRes.y);
	}
	
	private void buttonClicked(){
		game.setScreen(new GameScreen(game));
	}

	@Override
	public void show() {
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		camera.translate(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2);

		batch = new SpriteBatch();
		atlas = new TextureAtlas("data/button.pack");
		skin = new Skin();
		skin.addRegions(atlas);
		white = new BitmapFont(Gdx.files.internal("data/whitefont.fnt"), false);
		black = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);
		
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		Gdx.input.setInputProcessor(stage);
		
		TextButtonStyle style = new TextButtonStyle();
		style.up = skin.getDrawable("buttonnormal");
		style.down = skin.getDrawable("buttonpressed");
		style.font = black;
		
		button = new TextButton("Start Game", style);
		button.setColor(0, 0, 0, 0);
		button.setWidth(300);
		button.setHeight(80);
		button.setX(Gdx.graphics.getWidth() / 2 - (button.getWidth() / 2));
		button.setY(Gdx.graphics.getHeight() / 2 - (button.getHeight() / 2));
		
		button.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				buttonClicked();
			}
		});
		
        Tween.registerAccessor(TextButton.class, new ButtonTween());
        manager = new TweenManager();
        Tween.to(button, ButtonTween.ALPHA, 1f).target(1).ease(TweenEquations.easeInQuad).start(manager);
		
        LabelStyle ls = new LabelStyle(white, Color.WHITE);
        label = new Label("Chess", ls);
        label.setX(0);
        label.setY(Gdx.graphics.getHeight() / 2 + 100);
        label.setWidth(Gdx.graphics.getWidth());
        label.setAlignment(Align.center);

		stage.addActor(button);
		stage.addActor(label);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		skin.dispose();
		atlas.dispose();
		white.dispose();
		black.dispose();
		stage.dispose();
	}
}
