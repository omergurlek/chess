package com.ogurlek.chess.screens;

import com.badlogic.gdx.Screen;
import com.ogurlek.chess.Chess;
import com.ogurlek.chess.controller.GameWorld;
import com.ogurlek.chess.view.Renderer;

public class GameScreen implements Screen{

	Chess game;
	GameWorld world;
	Renderer render;
	
	public GameScreen(Chess game){
		this.game = game;
		this.world = new GameWorld();
		this.render = new Renderer(world, game);
		this.world.addHUDhandler(this.render.getHUD());
		this.world.setRenderer(render);
	}
	
	@Override
	public void render(float delta) {
		this.render.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		this.render.resize(width, height);
	}

	@Override
	public void show() {
		
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
