package com.ogurlek.chess.model;

public class MovementMap {

	Movement[][] map;
	
	public MovementMap(){
		this.map = new Movement[8][8];
	}
	
	public void addMove(int x, int y){
		this.map[x][y] = Movement.MOVE;
	}
	
	public void addAttack(int x, int y){
		this.map[x][y] = Movement.ATTACK;
	}
	
	public void addSelf(int x, int y){
		this.map[x][y] = Movement.SELF;
	}
	
	public Movement getMovement(int x, int y){
		return this.map[x][y];
	}
}
