package com.ogurlek.chess.screens;

import com.badlogic.gdx.Screen;
import com.ogurlek.chess.Chess;
import com.ogurlek.chess.view.Board;
import com.ogurlek.chess.view.Renderer;

public class GameScreen implements Screen{

	Chess game;
	Board board;
	Renderer render;
	
	public GameScreen(Chess game){
		this.game = game;
		this.board = new Board();
		this.render = new Renderer(board);
	}
	
	@Override
	public void render(float delta) {
//		board.update();
		render.render();
	}

	@Override
	public void resize(int width, int height) {
		
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
