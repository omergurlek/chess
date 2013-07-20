package com.ogurlek.chess.model;


public class Board {
	private Tile[][] tiles = new Tile[8][8];

	public Board(){
		this.populate(false);
	}

	public Board(Board oldBoard){
		this.populate(true);
		
		Tile[][] oldTiles = oldBoard.getTiles();
		Tile tempNewTile;
		Tile tempOldTile;
		Piece tempPiece;
		
		for(int x=0; x<8; x++){
			for(int y=0; y<8; y++){
				tempOldTile = oldTiles[x][y];
				tempNewTile = this.tiles[x][y];
				if(tempOldTile.isOccupied()){
					tempPiece = tempOldTile.getPiece().clone();
					tempPiece.setTile(tempNewTile);
					tempNewTile.occupy(tempPiece);
				}
			}
		}
	}

	private void populate(boolean empty){
		PieceColor tempColor = null;
		PieceType tempType = null;
		Piece tempPiece = null;

		for(int y=0; y<8; y++)
		{
			if(y<=1)
				tempColor = PieceColor.BLACK;
			else
				tempColor = PieceColor.WHITE;

			for(int x=0; x<8; x++)
			{
				tiles[x][y] = new Tile(x, y);

				if(!empty){
					if((y <= 1) || (y >= 6)){
						if((y == 1) || (y == 6))
							tempType = PieceType.PAWN;
						else{
							switch(x){
							case 0:
							case 7: tempType = PieceType.ROOK; break;
							case 1:
							case 6: tempType = PieceType.KNIGHT; break;
							case 2:
							case 5: tempType = PieceType.BISHOP; break;
							case 3: tempType = PieceType.QUEEN; break;
							case 4: tempType = PieceType.KING; break;
							}
						}

						tempPiece = new Piece(tiles[x][y], tempType, tempColor);
						tiles[x][y].occupy(tempPiece);
					}
				}
			}
		}
	}

	public boolean isOccupied(int x, int y){
		Tile tile = getTile(x, y);

		if(tile != null)
			return tile.isOccupied();
		else
			return true;
	}

	public Tile[][] getTiles(){
		return tiles;
	}

	public Tile getTile(int x, int y){
		if((x>=8) || (x<0) || (y>=8) || (y<0)){
			return null;
		}
		else
			return tiles[x][y];
	}

	public Piece[] getPieces(PieceColor color){
		Piece[] pieces = new Piece[16];
		Piece piece = null;
		int i = 0;

		for(Tile[] row : this.tiles){
			for(Tile tile : row){
				if(tile.isOccupied()){
					piece = tile.getPiece();

					if(piece.getColor() == color){
						pieces[i] = piece;
						i++;
					}
				}
			}
		}

		return pieces;
	}

	public Piece getKing(PieceColor color) {
		Piece[] pieces = new Piece[16];
		pieces = getPieces(color);

		for(Piece piece : pieces){
			if(piece.getType() == PieceType.KING){
				return piece;
			}
		}

		return null;
	}
}
