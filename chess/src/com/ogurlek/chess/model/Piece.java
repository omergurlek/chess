package com.ogurlek.chess.model;


public class Piece implements Cloneable {
	private Tile tile;
	private PieceType type;
	private PieceColor color;
	private boolean moved;
	
	public Piece(Tile tile, PieceType type, PieceColor color){
		this.tile = tile;
		this.type = type;
		this.color = color;
		this.moved = false;
	}
	
	public Tile getTile(){
		return this.tile;
	}
	
	public void setTile(Tile tile){
		this.tile = tile;
	}
	
	public PieceType getType(){
		return this.type;
	}
	
	public void setType(PieceType type){
		this.type = type;
	}
	
	public PieceColor getColor(){
		return this.color;
	}

	public void destroy() {
		this.tile = null;
	}
	
	public boolean hasMoved(){
		return this.moved;
	}
	
	public void setMoved(boolean moved){
		this.moved = moved;
	}
	
	@Override
	public Piece clone() {
		return new Piece(this.tile, this.type, this.color);
	}
}
