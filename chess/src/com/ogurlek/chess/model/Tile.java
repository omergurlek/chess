package com.ogurlek.chess.model;


public class Tile {
	private int x;
	private int y;
	private boolean occupied;
	private Piece piece;

	public Tile(int x, int y){
		this.x = x;
		this.y = y;
		this.occupied = false;
	}

	public void occupy(Piece piece)	{
		this.occupied = true;
		this.piece = piece;
	}
	
	public void free(){
		this.occupied = false;
		this.piece = null;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public boolean isOccupied(){
		return this.occupied;
	}
	
	public Piece getPiece(){
		return this.piece;
	}
}
