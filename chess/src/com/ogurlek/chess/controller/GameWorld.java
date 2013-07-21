package com.ogurlek.chess.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ogurlek.chess.model.Board;
import com.ogurlek.chess.model.GameState;
import com.ogurlek.chess.model.Movement;
import com.ogurlek.chess.model.MovementMap;
import com.ogurlek.chess.model.Piece;
import com.ogurlek.chess.model.PieceColor;
import com.ogurlek.chess.model.Tile;
import com.ogurlek.chess.view.Renderer;

public class GameWorld {

	GameState state;
	Board board;
	InputMultiplexer im;
	Piece selectedPiece;
	MovementMap movement;
	Renderer render;

	public GameWorld(){
		this.state = GameState.TURN_WHITE;
		this.board = new Board();
		this.selectedPiece = null;
		this.movement = new MovementMap();
		
		this.im = new InputMultiplexer(new InputHandler(this));
		Gdx.input.setInputProcessor(this.im);
	}
	
	public void setRenderer(Renderer render){
		this.render = render;
	}
	
	public void addHUDhandler(Stage hud){
		this.im.addProcessor(hud);
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
		case TURN_WHITE_CHECK:
			if(occupied && (touchedPiece.getColor() == PieceColor.WHITE)){
				selectedPiece = touchedPiece;
				mapMovement(selectedPiece);
				state = GameState.MOVE_WHITE;
			}
			break;
		case MOVE_WHITE: 
			move(selectedPiece, touchedTile, this.movement, false);
			break;
		case TURN_BLACK: 
		case TURN_BLACK_CHECK:
			if(occupied && (touchedPiece.getColor() == PieceColor.BLACK)){
				selectedPiece = touchedPiece;
				mapMovement(selectedPiece);
				state = GameState.MOVE_BLACK;
			}
			break;
		case MOVE_BLACK:
			move(selectedPiece, touchedTile, this.movement, false);
			break;
		}
	}

	private void clearMovementArray(){
		movement = new MovementMap();
	}

	private void mapMovement(Piece piece){
		MovementMap move = new MovementMap();
		this.movement = mapMovementArray(piece, move, this.board, false);
	}

	private MovementMap mapMovementArray(Piece piece, MovementMap movement, Board board, boolean guarding){
		Tile tile = piece.getTile();

		if(!guarding)
			movement.addSelf(tile.getX(), tile.getY());

		switch(piece.getType()){
		case PAWN: return mapPawnMovement(piece, movement, board, guarding);
		case ROOK: return mapRookMovement(piece, movement, board, guarding);
		case KNIGHT: return mapKnightMovement(piece, movement, board, guarding);
		case BISHOP: return mapBishopMovement(piece, movement, board, guarding);
		case QUEEN: return mapQueenMovement(piece, movement, board, guarding);
		case KING: return mapKingMovement(piece, movement, board, guarding);
		default: return movement;
		}
	}

	private MovementMap mapPawnMovement(Piece piece, MovementMap movement, Board board, boolean guarding){
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
			boolean frontOccupied = board.isOccupied(x, y-direction);
			if(!frontOccupied){
				movement.addMove(x, y-direction);
			}

			// Double movement possibility
			if(y == doublerow){
				frontOccupied = board.isOccupied(x, y-(2 * direction));

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
					if(board.isOccupied(x+side,y-direction)){

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

		if(!guarding)
			movement = removeInvalidMovements(piece, movement);

		return movement;
	}

	private MovementMap mapKnightMovement(Piece piece, MovementMap movement, Board board, boolean guarding){
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

		if(!guarding)
			movement = removeInvalidMovements(piece, movement);

		return movement;
	}

	private MovementMap mapContinuousMovement(Piece piece, MovementMap movement, Board board, boolean guarding, int[][] directionMultipliers){
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

		if(!guarding)
			movement = removeInvalidMovements(piece, movement);

		return movement;
	}

	private MovementMap mapRookMovement(Piece piece, MovementMap movement, Board board, boolean guarding){
		int[][] directionMultipliers = {{1,0}, {-1,0}, {0,1}, {0,-1}};

		return mapContinuousMovement(piece, movement, board, guarding, directionMultipliers);
	}

	private MovementMap mapBishopMovement(Piece piece, MovementMap movement, Board board, boolean guarding){
		int[][] directionMultipliers = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};

		return mapContinuousMovement(piece, movement, board, guarding, directionMultipliers);
	}

	private MovementMap mapQueenMovement(Piece piece, MovementMap movement, Board board, boolean guarding){
		return mapBishopMovement(piece, mapRookMovement(piece, movement, board, guarding), board, guarding);
	}

	private MovementMap mapKingMovement(Piece piece, MovementMap movement, Board board, boolean guarding){
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
						MovementMap enemyMovements = getPlayerMovements(opposite, board);

						if(!isDangerous(x+i,y+j,enemyMovements)){
							if(board.isOccupied(x+i, y+j)){
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

	private MovementMap removeInvalidMovements(Piece piece,	MovementMap movement) {
		PieceColor color = piece.getColor();
		Board hypothetical = new Board(this.board);
		Piece hypoPiece = piece.clone();
		hypothetical.placePiece(hypoPiece);

		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				Movement move = movement.getMovement(i, j);
				if(move == Movement.ATTACK || move == Movement.MOVE){
					move(hypoPiece, hypothetical.getTile(i, j), movement, true);

					if(isCheck(color, hypothetical)){
						movement.removeMovement(i, j);

						hypothetical = new Board(this.board);
						hypoPiece = piece.clone();
						hypothetical.placePiece(hypoPiece);
					}
				}
			}
		}

		return movement;
	}


	private MovementMap getPlayerMovements(PieceColor color, Board board){
		Piece[] pieces = board.getPieces(color);

		MovementMap playerMovements = new MovementMap();
		for(Piece piece : pieces){
			if(piece != null)
				playerMovements = mapMovementArray(piece, playerMovements, board, true);
		}

		return playerMovements;
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

	private boolean isCheck(PieceColor color, Board board){
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE; 
		MovementMap enemyMovements = getPlayerMovements(opposite, board);
		Piece king = board.getKing(color);
		int x = king.getTile().getX();
		int y = king.getTile().getY();

		if(enemyMovements.getMovement(x, y) == Movement.ATTACK)
			return true;
		else
			return false;
	}

	private boolean isCheckmate(PieceColor color){
		Piece king = this.board.getKing(color);

		// Can King move anywhere?
		MovementMap kingMovements = mapKingMovement(king, new MovementMap(), this.board, false);

		for(int x=0; x<8; x++){
			for(int y=0; y<8; y++){
				Movement move = kingMovements.getMovement(x, y);
				if(move == Movement.MOVE || move == Movement.ATTACK)
					return false;
			}
		}

		// Loop through all legal moves to see if a no-check move exists
		Piece[] pieces = this.board.getPieces(color);
		MovementMap pieceMovements;

		for(Piece piece : pieces){
			if(piece != null){
				pieceMovements = new MovementMap();
				pieceMovements = mapMovementArray(piece, pieceMovements, this.board, false);

				if(pieceMovements.hasMove())
					return false;
			}
		}

		return true;
	}

	private void gameOver(PieceColor winner){
		this.render.checkmate(winner);
	}

	private void move(Piece piece, Tile newTile, MovementMap movement, boolean hypothetical){
		PieceColor color = piece.getColor();
		PieceColor opposite = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
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

			if(!hypothetical){
				if(isCheck(opposite, this.board)){
					if(isCheckmate(opposite)){
						gameOver(color);
					}
					else{
						this.state = (color == PieceColor.WHITE) ? GameState.TURN_BLACK_CHECK : GameState.TURN_WHITE_CHECK;
						this.render.switchTurns(opposite);
						this.render.check();
					}
				}

				else{
					this.state = (color == PieceColor.WHITE) ? GameState.TURN_BLACK : GameState.TURN_WHITE;
					this.render.switchTurns(opposite);
				}

				clearMovementArray();
			}
		}
		else {
			if(move == Movement.SELF)
				cancelMovement();
		}
	}

	private void cancelMovement(){
		clearMovementArray();

		if(this.state == GameState.MOVE_WHITE)
			this.state = GameState.TURN_WHITE;
		else
			this.state = GameState.TURN_BLACK;
	}

	public GameState getState(){
		return this.state;
	}

	public Board getBoard(){
		return this.board;
	}

	public MovementMap getMovement(){
		return this.movement;
	}
}
