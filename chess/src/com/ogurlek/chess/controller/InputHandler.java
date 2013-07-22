package com.ogurlek.chess.controller;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;

public class InputHandler implements InputProcessor {

	GameWorld world;
	Rectangle viewport;
	float cropX;
	float cropY;
	
	public InputHandler(GameWorld world){
		this.world = world;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public void updateViewPort(Rectangle viewport, float cropX, float cropY) {
		this.viewport = viewport;
		this.cropX = cropX;
		this.cropY = cropY;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		float viewportX = (float) screenX - cropX;
		float viewportY = (float) screenY - cropY;
		
		float viewportWidth = this.viewport.getWidth();
		float viewportHeight = this.viewport.getHeight();
		float hudSpace = viewportHeight - viewportWidth;
		float boardLimit = hudSpace + viewportWidth;
		
		if((viewportX >= 0) && (viewportX <= viewportWidth) && (viewportY >= hudSpace) && (viewportY <= boardLimit)){
			world.touchedBoard(viewportX, viewportY - hudSpace);
			return true;
		}
		else{
			return false;
		}
	
//		if((screenY >= 160) && (screenY <= 640)){
//			world.touchedBoard(screenX, screenY - 160);
//			return true;
//		}
//		else{
//			return false;
//		}
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
