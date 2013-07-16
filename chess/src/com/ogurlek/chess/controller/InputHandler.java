package com.ogurlek.chess.controller;

import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor {

	GameWorld world;
	
	public InputHandler(GameWorld world){
		this.world = world;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if((screenY >= 110) && (screenY <= 590))	{
			world.touchedBoard(screenX, screenY - 110);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
}
