package com.ogurlek.chess.model;


public class Board {
	private Tile[][] tiles = new Tile[8][8];

	public Board(){
		this.populate();
	}

	private void populate(){
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
}
