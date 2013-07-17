package com.ogurlek.chess.controller;

import com.badlogic.gdx.Gdx;
import com.ogurlek.chess.model.Board;
import com.ogurlek.chess.model.GameState;
import com.ogurlek.chess.model.Movement;
import com.ogurlek.chess.model.MovementMap;
import com.ogurlek.chess.model.Piece;
import com.ogurlek.chess.model.PieceColor;
import com.ogurlek.chess.model.Tile;

public class GameWorld {

	GameState state;
	Board board;
	Piece selectedPiece;
	MovementMap movement;

	public GameWorld(){
		this.state = GameState.TURN_WHITE;
		this.board = new Board();
		this.selectedPiece = null;
		this.movement = new MovementMap();
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
		movement = new MovementMap();
	}

	private void mapMovement(Piece piece){
		MovementMap move = new MovementMap();
		this.movement = mapMovementArray(piece, move, false);
	}

	private MovementMap mapMovementArray(Piece piece, MovementMap movement, boolean guarding){
		Tile tile = piece.getTile();

		movement.addSelf(tile.getX(), tile.getY());

		switch(piece.getType()){
		case PAWN: return mapPawnMovement(piece, movement, guarding);
		case ROOK: return mapRookMovement(piece, movement, guarding);
		case KNIGHT: return mapKnightMovement(piece, movement, guarding);
		case BISHOP: return mapBishopMovement(piece, movement, guarding);
		case QUEEN: return mapQueenMovement(piece, movement, guarding);
		case KING: return mapKingMovement(piece, movement, guarding);
		default: return movement;
		}
	}

	private MovementMap mapPawnMovement(Piece piece, MovementMap movement, boolean guarding){
		Tile tile = piece.getTile();
		int x = tile.getX();
		int y = tile.getY();
		PieceColor color = piece.getColor();
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
		int doublerow = (color == PieceColor.WHITE) ? 6 : 1;
		int direction = (color == PieceColor.WHITE) ? 1 : -1; 
		int[] sides = {1, -1};
		Tile targetTile = null;
		PieceColor targetColor = null;

		// If planning a move
		if(!guarding){

			// Front pawn movement
			boolean frontOccupied = this.board.isOccupied(x, y-direction);
			if(!frontOccupied){
				movement.addMove(x, y-direction);
			}

			// Double movement possibility
			if(y == doublerow){
				frontOccupied = this.board.isOccupied(x, y-(2 * direction));

				if(!frontOccupied){
					movement.addMove(x, y-(2 * direction));
				}
			}
		}

		// Pawn attacks
		for(int side : sides){
			targetTile = board.getTile(x+side, y-direction);

			if(targetTile != null){

				if(!guarding){
					if(this.board.isOccupied(x+side,y-direction)){

						targetColor = targetTile.getPiece().getColor();

						if(targetColor == opposite){
							movement.addAttack(x+side, y-direction);
						}
					}
				}

				// If guarding against a moving king
				else{
					movement.addAttack(x+side, y-direction);
				}
			}
		}

		return movement;
	}

	private MovementMap mapKnightMovement(Piece piece, MovementMap movement, boolean guarding){
		Tile tile = piece.getTile();
		int x = tile.getX();
		int y = tile.getY();
		PieceColor color = piece.getColor();
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
		int[][] possibleMovements = {{1,2}, {1,-2}, {-1,2}, {-1,-2}, {2,1}, {2,-1}, {-2,1}, {-2,-1}};
		Tile targetTile = null;

		for(int[] pair : possibleMovements){
			targetTile = board.getTile(x+pair[0], y+pair[1]);

			if(targetTile != null){

				// If planning a move
				if(!guarding){
					if(targetTile.isOccupied()){
						if(targetTile.getPiece().getColor() == opposite){
							movement.addAttack(x+pair[0], y+pair[1]);
						}
					}

					else{
						movement.addMove(x+pair[0], y+pair[1]);
					}
				}

				// If guarding against a moving king
				else{
					movement.addAttack(x+pair[0], y+pair[1]);
				}
			}
		}

		return movement;
	}
	
	private MovementMap mapContinuousMovement(Piece piece, MovementMap movement, boolean guarding, int[][] directionMultipliers){
		Tile tile = piece.getTile();
		int x = tile.getX();
		int y = tile.getY();
		PieceColor color = piece.getColor();
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
		Tile targetTile = null;

		for(int[] pair : directionMultipliers){
			for(int i=1; i<=8; i++){
				targetTile = board.getTile(x+(pair[0]*i), y+(pair[1]*i));

				if(targetTile != null){
					if(targetTile.isOccupied()){
						if(!guarding){
							if(targetTile.getPiece().getColor() == opposite){
								movement.addAttack(x+(pair[0]*i), y+(pair[1]*i));
							}
						}
						else{
							movement.addAttack(x+(pair[0]*i), y+(pair[1]*i));
						}

						break;
					}

					else{
						if(!guarding)
							movement.addMove(x+(pair[0]*i), y+(pair[1]*i));
						else
							movement.addAttack(x+(pair[0]*i), y+(pair[1]*i));
					}
				}
			}
		}

		return movement;
	}

	private MovementMap mapRookMovement(Piece piece, MovementMap movement, boolean guarding){
		int[][] directionMultipliers = {{1,0}, {-1,0}, {0,1}, {0,-1}};
		
		return mapContinuousMovement(piece, movement, guarding, directionMultipliers);
	}

	private MovementMap mapBishopMovement(Piece piece, MovementMap movement, boolean guarding){
		int[][] directionMultipliers = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
		
		return mapContinuousMovement(piece, movement, guarding, directionMultipliers);
	}

	private MovementMap mapQueenMovement(Piece piece, MovementMap movement, boolean guarding){
		return mapBishopMovement(piece, mapRookMovement(piece, movement, guarding), guarding);
	}

	private MovementMap mapKingMovement(Piece piece, MovementMap movement, boolean guarding){
		Tile tile = piece.getTile();
		int x = tile.getX();
		int y = tile.getY();
		PieceColor color = piece.getColor();
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
		Tile targetTile = null;
		PieceColor targetColor = null;

		// Map King movements
		for(int i=-1; i<2; i++){
			for(int j=-1; j<2; j++){
				targetTile = board.getTile(x+i, y+j);

				if(targetTile != null){

					// If planning a move
					if(!guarding){
						MovementMap enemyMovements = getEnemyMovements(opposite);

						if(!isDangerous(x+i,y+j,enemyMovements)){
							if(this.board.isOccupied(x+i, y+j)){
								targetColor = targetTile.getPiece().getColor();

								if(targetColor == opposite){
									movement.addAttack(x+i, y+j);
								}
							}

							else{
								movement.addMove(x+i, y+j);
							}
						}
					}

					// If guarding against a moving king
					else{
						movement.addAttack(x+i, y+j);
					}
				}
			}
		}

		return movement;
	}

	private MovementMap getEnemyMovements(PieceColor color){
		Piece[] enemies = this.board.getPieces(color);

		MovementMap enemyMovements = new MovementMap();
		for(Piece enemy : enemies){
			if(enemy != null)
				enemyMovements = mapMovementArray(enemy, enemyMovements, true);
		}

		return enemyMovements;
	}

	private boolean isDangerous(int x, int y, MovementMap enemyMovements){
		if((x>=8) || (x<0) || (y>=8) || (y<0)){
			return true;
		}
		else if(enemyMovements.getMovement(x,y) == Movement.ATTACK)
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
		Movement move = movement.getMovement(newTile.getX(), newTile.getY());

		if((move == Movement.MOVE) || (move == Movement.ATTACK)){
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
			if(move == Movement.SELF)
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

	public MovementMap getMovement(){
		return this.movement;
	}
}
