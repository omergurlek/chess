package com.ogurlek.chess.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ogurlek.chess.controller.GameWorld;
import com.ogurlek.chess.model.Piece;
import com.ogurlek.chess.model.PieceColor;
import com.ogurlek.chess.model.PieceType;
import com.ogurlek.chess.model.Tile;

public class Renderer {

	GameWorld world;
	SpriteBatch batch;
	Texture boardTexture;
	Sprite boardSprite;
	Texture redTexture;
	Sprite redSprite;
	Texture blueTexture;
	Sprite blueSprite;
	TextureAtlas atlas;
	Skin skin;

	public Renderer(GameWorld world){
		this.world = world;
		batch = new SpriteBatch();
		atlas = new TextureAtlas("data/chesspieces.pack");
		skin = new Skin();
		skin.addRegions(atlas);

		boardTexture = new Texture("data/board.png");
		boardTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		redTexture = new Texture("data/redtile.png");
		blueTexture = new Texture("data/bluetile.png");
		
		boardSprite = new Sprite(boardTexture);
		boardSprite.setY(40);
		redSprite = new Sprite(redTexture);
		blueSprite = new Sprite(blueTexture);
	}

	public void render(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		Tile[][] tiles = world.getBoard().getTiles();
		int[][] movement = world.getMovement();
		Piece tempPiece;
		PieceType tempPieceType;
		Sprite tempSprite = null;

		batch.begin();

		boardSprite.draw(batch);

		for(int y=0; y<8; y++){
			for(int x=0; x<8; x++){
				if(movement[x][y] == 1){
					redSprite.setX(x*60);
					redSprite.setY(460 - (y*60));
					redSprite.draw(batch);
				}
				else if(movement[x][y] == 2){
					blueSprite.setX(x*60);
					blueSprite.setY(460 - (y*60));
					blueSprite.draw(batch);
				}

				if(tiles[x][y].isOccupied()){
					tempPiece = tiles[x][y].getPiece();
					tempPieceType = tempPiece.getType();

					switch(tempPieceType){
					case PAWN: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("pawn") : skin.getSprite("bpawn"); break;
					case ROOK: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("rook") : skin.getSprite("brook"); break;
					case KNIGHT: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("knight") : skin.getSprite("bknight"); break;
					case BISHOP: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("bishop") : skin.getSprite("bbishop"); break;
					case QUEEN: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("queen") : skin.getSprite("bqueen"); break;
					case KING: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("king") : skin.getSprite("bking"); break;
					}

					tempSprite.setX((x * 60) + 5);
					tempSprite.setY(480 - (y * 60));
					tempSprite.draw(batch);
				}
			}
		}

		batch.end();
	}
}
