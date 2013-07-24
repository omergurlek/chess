package com.ogurlek.chess.view;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.ogurlek.chess.Chess;
import com.ogurlek.chess.GlobalSettings;
import com.ogurlek.chess.controller.GameWorld;
import com.ogurlek.chess.model.Movement;
import com.ogurlek.chess.model.MovementMap;
import com.ogurlek.chess.model.Piece;
import com.ogurlek.chess.model.PieceColor;
import com.ogurlek.chess.model.PieceType;
import com.ogurlek.chess.model.Tile;
import com.ogurlek.chess.screens.MainMenu;
import com.ogurlek.chess.tweens.ButtonTween;

public class Renderer {

	GameWorld world;
	Chess game;
	OrthographicCamera camera;
	SpriteBatch batch;
	TextureAtlas piecesAtlas;
	TextureAtlas buttonAtlas;
	Skin skin;
	BitmapFont black;
	BitmapFont white;
	TextButton button;
	Stage stage;
	TweenManager manager;
	Label checkLabel;
	Label turnLabel;

	public Renderer(GameWorld world, Chess game){
		this.world = world;
		this.game = game;

		camera = new OrthographicCamera(GlobalSettings.VIRTUAL_WIDTH, GlobalSettings.VIRTUAL_HEIGHT);
		camera.translate(GlobalSettings.VIRTUAL_WIDTH/2, GlobalSettings.VIRTUAL_HEIGHT/2);

		batch = new SpriteBatch();

		piecesAtlas = new TextureAtlas("data/chess.pack");
		buttonAtlas = new TextureAtlas("data/button.pack");
		skin = new Skin();
		skin.addRegions(piecesAtlas);
		skin.addRegions(buttonAtlas);

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		stage.setCamera(camera);

		white = new BitmapFont(Gdx.files.internal("data/whitefont.fnt"), false);
		black = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);

		setupStage();
	}

	public void resize(int width, int height) {
		float ratio = (float) height / (float) width;

		float vHeight;
		float vWidth;

		if(ratio > GlobalSettings.ASPECT_RATIO){
			vHeight = GlobalSettings.VIRTUAL_WIDTH * ratio;
			vWidth = GlobalSettings.VIRTUAL_WIDTH;
		}
		else{
			vHeight = GlobalSettings.VIRTUAL_HEIGHT;
			vWidth = GlobalSettings.VIRTUAL_HEIGHT / ratio;
		}

		camera.viewportHeight = vHeight;
		camera.viewportWidth = vWidth;

		this.world.updateViewport(vWidth, vHeight);
	}

	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		Tile[][] tiles = world.getBoard().getTiles();
		MovementMap movement = world.getMovement();
		Movement tempMove;
		Piece tempPiece;
		PieceType tempPieceType;
		Sprite tempSprite = null;

		camera.update();
		stage.act();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// Draw board
		tempSprite = skin.getSprite("board");
		tempSprite.draw(batch);

		for(int y=0; y<8; y++){
			for(int x=0; x<8; x++){
				tempMove = movement.getMovement(x,y);

				// Draw movement markers
				if(tempMove != null){
					switch(tempMove){
					case MOVE: tempSprite = skin.getSprite("yellowtile"); break;
					case ATTACK: tempSprite = skin.getSprite("redtile"); break;
					case SELF: tempSprite = skin.getSprite("bluetile");	break;
					case LEFT_CASTLE:
					case RIGHT_CASTLE: tempSprite = skin.getSprite("purpletile"); break;
					}

					tempSprite.setX(x*60);
					tempSprite.setY(420 - (y*60));
					tempSprite.draw(batch);
				}

				// Draw chess pieces
				if(tiles[x][y].isOccupied()){
					tempPiece = tiles[x][y].getPiece();
					tempPieceType = tempPiece.getType();

					switch(tempPieceType){
					case PAWN: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("pawn") : skin.getSprite("bpawn"); break;
					case ROOK: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("rook") : skin.getSprite("brook"); break;
					case KNIGHT: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("knight") : skin.getSprite("bknight"); break;
					case BISHOP: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("bishop") : skin.getSprite("bbishop"); break;
					case QUEEN: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("queen") : skin.getSprite("bqueen"); break;
					case KING: tempSprite = (tempPiece.getColor() == PieceColor.WHITE) ? skin.getSprite("king") : skin.getSprite("bking"); break;
					}

					tempSprite.setX((x * 60) + 5);
					tempSprite.setY(440 - (y * 60));
					tempSprite.draw(batch);
				}
			}
		}

		batch.end();

		manager.update(delta);

		// Drawing the HUD
		batch.begin();
		stage.draw();
		batch.end();
	}

	public void setupStage(){
		TextButtonStyle style = new TextButtonStyle();
		style.up = skin.getDrawable("buttonnormal");
		style.down = skin.getDrawable("buttonpressed");
		style.font = black;

		button = new TextButton("Replay", style);
		button.setWidth(150);
		button.setHeight(40);
		button.setColor(0,0,0,0);
		button.setDisabled(true);
		button.setX((camera.viewportWidth / 2 - (button.getWidth() / 2)));
		button.setY(camera.viewportHeight - 110);

		button.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				buttonClicked();
			}
		});

		Tween.registerAccessor(TextButton.class, new ButtonTween());
		manager = new TweenManager();

		LabelStyle ls = new LabelStyle(white, Color.WHITE);
		checkLabel = new Label("Check!", ls);
		checkLabel.setX(0);
		checkLabel.setY(camera.viewportHeight  - 100);
		checkLabel.setWidth(camera.viewportWidth);
		checkLabel.setAlignment(Align.center);
		checkLabel.setVisible(false);

		turnLabel = new Label("White Player's Turn", ls);
		turnLabel.setX(0);
		turnLabel.setY(camera.viewportHeight - 50);
		turnLabel.setWidth(camera.viewportWidth);
		turnLabel.setAlignment(Align.center);

		stage.addActor(button);
		stage.addActor(checkLabel);
		stage.addActor(turnLabel);
	}

	public Stage getHUD(){
		return this.stage;
	}

	public void switchTurns(PieceColor color){
		String turnText = (color == PieceColor.BLACK) ? "Black Player's Turn" : "White Player's Turn";
		turnLabel.setText(turnText);

		checkLabel.setVisible(false);
	}

	public void check(){
		checkLabel.setVisible(true);
	}

	public void checkmate(PieceColor color){
		String victoryText = (color == PieceColor.BLACK) ? "Black Player wins!" : "White Player wins!";
		turnLabel.setText(victoryText);

		button.setDisabled(false);
		Tween.to(button, ButtonTween.ALPHA, 1f).target(1).ease(TweenEquations.easeInQuad).start(manager);
	}

	private void buttonClicked(){
		game.setScreen(new MainMenu(game));
	}
}
