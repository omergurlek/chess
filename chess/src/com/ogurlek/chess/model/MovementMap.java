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
	
	public void addLeftCastle(int x, int y) {
		this.map[x][y] = Movement.LEFT_CASTLE;
	}
	
	public void addRightCastle(int x, int y) {
		this.map[x][y] = Movement.RIGHT_CASTLE;
	}
	
	public void addSelf(int x, int y){
		this.map[x][y] = Movement.SELF;
	}
	
	public Movement getMovement(int x, int y){
		return this.map[x][y];
	}
	
	public void removeMovement(int x, int y){
		this.map[x][y] = null;
	}

	public boolean hasMove() {
		for(int x=0; x<8; x++){
			for(int y=0; y<8; y++){
				Movement move = map[x][y];
				
				if(move == Movement.MOVE || move == Movement.ATTACK)
					return true;
			}
		}
		return false;
	}
}
