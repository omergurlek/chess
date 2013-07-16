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
	int[][] movement;

	public GameWorld(){
		this.state = GameState.TURN_WHITE;
		this.board = new Board();
		this.selectedPiece = null;
		this.movement = new int[8][8];
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
				mapMovement(selectedPiece);
				state = GameState.MOVE_WHITE;
			}
			break;
		case MOVE_WHITE: 
			//placeholder chesspiece movement
			if(move(selectedPiece, touchedTile))
				state = GameState.TURN_BLACK;
			break;
		case TURN_BLACK: 
			if(occupied && (touchedPiece.getColor() == PieceColor.BLACK)){
				selectedPiece = touchedPiece;
				mapMovement(selectedPiece);
				state = GameState.MOVE_BLACK;
			}
			break;
		case MOVE_BLACK:
			//placeholder chesspiece movement
			if(move(selectedPiece, touchedTile))
				state = GameState.TURN_WHITE;
			break;
		}
	}

	private void clearMovementArray(){
		movement = new int[8][8];
	}

	private void mapMovement(Piece piece){
		int[][] emptyArray = new int[8][8];
		this.movement = mapMovementArray(piece, emptyArray, false);
	}

	private int[][] mapMovementArray(Piece piece, int[][] movement, boolean guarding){
		Tile tile = piece.getTile();

		movement[tile.getX()][tile.getY()] = 2;

		switch(piece.getType()){
		case PAWN: return mapPawnMovement(piece, movement, guarding);
		case ROOK: return mapRookMovement(piece, movement);
		case KNIGHT: return mapKnightMovement(piece, movement);
		case BISHOP: return mapBishopMovement(piece, movement);
		case QUEEN: return mapQueenMovement(piece, movement);
		case KING: return mapKingMovement(piece, movement, guarding);
		default: return movement;
		}
	}

	private int[][] mapPawnMovement(Piece piece, int[][] movement, boolean guarding){
		Tile tile = piece.getTile();
		int x = tile.getX();
		int y = tile.getY();
		PieceColor color = piece.getColor();
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
		int doublerow = (color == PieceColor.WHITE) ? 6 : 1;
		int direction = (color == PieceColor.WHITE) ? 1 : -1; 
		Tile targetTile = null;
		PieceColor targetColor = null;

		// If planning a move
		if(!guarding){
			// Front pawn movement
			boolean frontOccupied = this.board.isOccupied(x, y-direction);
			if(!frontOccupied){
				movement[x][y-direction] = 1;
			}

			// Double movement possibility
			if(y == doublerow){
				frontOccupied = this.board.isOccupied(x, y-(2 * direction));

				if(!frontOccupied){
					movement[x][y-(2 * direction)] = 1;
				}
			}

			// Front right pawn attack
			if(this.board.isOccupied(x+1,y-direction)){
				targetTile = board.getTile(x+1, y-direction);

				if(targetTile != null){
					targetColor = targetTile.getPiece().getColor();

					if(targetColor == opposite){
						movement[x+1][y-direction] = 1;
					}
				}
			}

			// Front left pawn attack
			if(this.board.isOccupied(x-1,y-direction)){
				targetTile = board.getTile(x-1, y-direction);

				if(targetTile != null){
					targetColor = targetTile.getPiece().getColor();

					if(targetColor == opposite){
						movement[x-1][y-direction] = 1;
					}
				}
			}
		}
		// If guarding against a moving king
		else{
			// Front right pawn guard
			if(!this.board.isOccupied(x+1,y-direction)){
				targetTile = board.getTile(x+1, y-direction);

				if(targetTile != null){
					movement[x+1][y-direction] = 1;
				}
			}

			// Front left pawn guard
			if(!this.board.isOccupied(x-1,y-direction)){
				targetTile = board.getTile(x-1, y-direction);

				if(targetTile != null){
					movement[x-1][y-direction] = 1;
				}
			}
		}

		return movement;
	}

	private int[][] mapKnightMovement(Piece piece, int[][] movement){
		return movement;
	}

	private int[][] mapRookMovement(Piece piece, int[][] movement){
		return movement;
	}

	private int[][] mapBishopMovement(Piece piece, int[][] movement){
		return movement;
	}

	private int[][] mapQueenMovement(Piece piece, int[][] movement){
		return mapBishopMovement(piece, mapRookMovement(piece, movement));
	}

	private int[][] mapKingMovement(Piece piece, int[][] movement, boolean guarding){
		Tile tile = piece.getTile();
		int x = tile.getX();
		int y = tile.getY();
		PieceColor color = piece.getColor();
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
		Tile targetTile = null;
		PieceColor targetColor = null;

		// Prepare dangerous King movements
		Piece[] enemies = this.board.getPieces(opposite);

		// If planning a move
		if(!guarding){
			int[][] enemyMovements = new int[8][8];
			for(Piece enemy : enemies){
				if(enemy != null)
					enemyMovements = mapMovementArray(enemy, enemyMovements, true);
			}

			// Map King movements
			for(int i=-1; i<2; i++){
				for(int j=-1; j<2; j++){
					if(!isDangerous(x+i,y+j,enemyMovements)){
						targetTile = board.getTile(x+i, y+j);
						if(targetTile != null){
							if(this.board.isOccupied(x+i, y+j)){
								targetColor = targetTile.getPiece().getColor();

								if(targetColor == opposite){
									movement[x+i][y+j] = 1;
								}
							}
							else{
								movement[x+i][y+j] = 1;
							}
						}
					}
				}
			}
		}
		// If guarding against a moving king
		else{
			for(int i=-1; i<2; i++){
				for(int j=-1; j<2; j++){
					if(!this.board.isOccupied(x+i,y+j)){
						targetTile = board.getTile(x+i, y+j);
						if(targetTile != null){
							movement[x+i][y+j] = 1;
						}
					}
				}
			}
		}

		return movement;
	}

	private boolean isDangerous(int x, int y, int[][] enemyMovements){
		if((x>=8) || (x<0) || (y>=8) || (y<0)){
			return true;
		}
		else if(enemyMovements[x][y] == 1)
			return true;
		else
			return false;
	}
	
	private void checkForCheck(){
		// to-do check state
	}
	
	private void checkForCheckmate(){
		// to-do check-mate state
	}

	private boolean move(Piece piece, Tile newTile){
		int move = movement[newTile.getX()][newTile.getY()];

		if(move == 1){
			Tile oldTile = piece.getTile();
			oldTile.free();

			if(newTile.isOccupied()){
				newTile.getPiece().destroy();
				newTile.free();
			}

			piece.setTile(newTile);
			newTile.occupy(piece);
			
			checkForCheck();
			
			clearMovementArray();
			return true;
		}
		else {
			if(move == 2)
				cancelMovement();

			return false;
		}
	}

	private void cancelMovement(){
		clearMovementArray();

		if(this.state == GameState.MOVE_WHITE)
			this.state = GameState.TURN_WHITE;
		else
			this.state = GameState.TURN_BLACK;
	}

	public Board getBoard(){
		return this.board;
	}

	public int[][] getMovement(){
		return movement;
	}
}
