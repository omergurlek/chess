package com.ogurlek.chess.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ogurlek.chess.model.board.Board;
import com.ogurlek.chess.model.board.Tile;
import com.ogurlek.chess.model.pieces.Piece;
import com.ogurlek.chess.model.pieces.PieceColor;
import com.ogurlek.chess.model.pieces.PieceType;

public class Renderer {

	Board board;
	SpriteBatch batch;
	Texture boardTexture;
	Sprite boardSprite;
	TextureAtlas atlas;
	Skin skin;
	
	public Renderer(Board board){
		this.board = board;
		batch = new SpriteBatch();
		atlas = new TextureAtlas("data/chesspieces.pack");
		skin = new Skin();
		skin.addRegions(atlas);
		
		boardTexture = new Texture("data/board.png");
		boardTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		boardSprite = new Sprite(boardTexture);
		boardSprite.setY(40);
	}
	
	public void render(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		Tile[][] tiles = board.getTiles();
		Piece tempPiece;
		PieceType tempPieceType;
		Sprite tempSprite = null;
		
		batch.begin();
		
		boardSprite.draw(batch);
		
		for(int y=0; y<8; y++){
			for(int x=0; x<8; x++){
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
