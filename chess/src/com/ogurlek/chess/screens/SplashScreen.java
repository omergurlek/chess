package com.ogurlek.chess.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.ogurlek.chess.Chess;
import com.ogurlek.chess.GlobalSettings;
import com.ogurlek.chess.tweens.SpriteTween;

public class SplashScreen implements Screen{

	OrthographicCamera camera;
	Rectangle viewport;
	Texture splashTexture;
	Sprite splashSprite;
	SpriteBatch batch;
	Chess game;
	TweenManager manager;

	public SplashScreen(Chess game)	{
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		manager.update(delta);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		splashSprite.draw(batch);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		float ratio = (float) height / (float) width;

		float vHeight;
		float vWidth;
		
		if(ratio > GlobalSettings.ASPECT_RATIO){
			vHeight = GlobalSettings.VIRTUAL_WIDTH * ratio;
			vWidth = GlobalSettings.VIRTUAL_WIDTH;
		}
		else{
			vHeight = GlobalSettings.VIRTUAL_HEIGHT;
			vWidth = GlobalSettings.VIRTUAL_HEIGHT / ratio;
		}

		camera.viewportHeight = vHeight;
		camera.viewportWidth = vWidth;
	}

	@Override
	public void show() {
		camera = new OrthographicCamera(GlobalSettings.VIRTUAL_WIDTH, GlobalSettings.VIRTUAL_HEIGHT);
		camera.translate(GlobalSettings.VIRTUAL_WIDTH/2, GlobalSettings.VIRTUAL_HEIGHT/2);

		splashTexture = new Texture("data/splashscreen.png");
		splashTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		splashSprite = new Sprite(splashTexture);
        splashSprite.setColor(1, 1, 1, 0);
        splashSprite.setY(camera.viewportHeight / 2 - (splashSprite.getHeight() / 2));

		batch = new SpriteBatch();
		
        Tween.registerAccessor(Sprite.class, new SpriteTween());
        
        manager = new TweenManager();
        
        TweenCallback cb = new TweenCallback() {                        
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                        tweenCompleted();
                }
        };
        
        Tween.to(splashSprite, SpriteTween.ALPHA, 2f).target(1).ease(TweenEquations.easeInQuad).repeatYoyo(1, 2.5f).setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE).start(manager);
	}
	
	private void tweenCompleted(){
		game.setScreen(new MainMenu(game));
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}
