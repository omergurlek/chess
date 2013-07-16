package com.ogurlek.chess.controller;

import com.badlogic.gdx.Gdx;
import com.ogurlek.chess.model.Board;
import com.ogurlek.chess.model.GameState;
import com.ogurlek.chess.model.Piece;
import com.ogurlek.chess.model.PieceColor;
import com.ogurlek.chess.model.Tile;

public class GameWorld {

	GameState state;
	Board board;
	Piece selectedPiece;

	public GameWorld(){
		this.state = GameState.TURN_WHITE;
		this.board = new Board();
		this.selectedPiece = null;
        Gdx.input.setInputProcessor(new InputHandler(this));
	}

	public void touchedBoard(int x, int y){
		int tileX = x / 60;
		int tileY = y / 60;

		Tile touchedTile = board.getTile(tileX, tileY);
		boolean occupied = touchedTile.isOccupied();
		Piece touchedPiece = null;

		if(occupied)
			touchedPiece = touchedTile.getPiece();

		switch(state){
		case TURN_WHITE: 
			if(occupied && (touchedPiece.getColor() == PieceColor.WHITE)){
				selectedPiece = touchedPiece;
				state = GameState.MOVE_WHITE;
			}
			break;
		case MOVE_WHITE: 
			//placeholder chesspiece movement
			if(!occupied){
				move(selectedPiece, touchedTile);
				state = GameState.TURN_BLACK;
			}
			break;
		case TURN_BLACK: 
			if(occupied && (touchedPiece.getColor() == PieceColor.BLACK)){
				selectedPiece = touchedPiece;
				state = GameState.MOVE_BLACK;
			}
			break;
		case MOVE_BLACK:
			//placeholder chesspiece movement
			if(!occupied){
				move(selectedPiece, touchedTile);
				state = GameState.TURN_WHITE;
			}
			break;
		}
	}

	public void move(Piece piece, Tile newTile){
		Tile oldTile = piece.getTile();
		oldTile.free();

		piece.setTile(newTile);
		newTile.occupy(piece);
	}

	public Board getBoard(){
		return this.board;
	}
}
