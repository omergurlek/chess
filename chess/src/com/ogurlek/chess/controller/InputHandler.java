package com.ogurlek.chess.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.ogurlek.chess.GlobalSettings;

public class InputHandler implements InputProcessor {

	GameWorld world;
	float vWidth;
	float vHeight;
	float topMargin;
	float leftMargin;
	
	public InputHandler(GameWorld world){
		this.world = world;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}


	public void updateViewPort(float vWidth, float vHeight) {
		this.vWidth = vWidth;
		this.vHeight = vHeight;
		
		this.topMargin = (vHeight - GlobalSettings.VIRTUAL_HEIGHT) / 2;
		this.leftMargin = (vWidth - GlobalSettings.VIRTUAL_WIDTH) / 2;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		float percentX = (float) screenX / (float) Gdx.graphics.getWidth();
		float percentY = (float) screenY / (float) Gdx.graphics.getHeight(); 
		
		float viewportX = percentX * vWidth;
		float viewportY = percentY * vHeight;
				
		float realX = viewportX - leftMargin;
		float realY = viewportY - topMargin;
				
		if((realX >= 0) && (realX <= GlobalSettings.VIRTUAL_WIDTH) && (realY >= GlobalSettings.HUD_SPACE) && (realY <= GlobalSettings.VIRTUAL_HEIGHT)){
			world.touchedBoard(realX, realY - GlobalSettings.HUD_SPACE);
			return true;
		}
		else{
			return false;
		}
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
