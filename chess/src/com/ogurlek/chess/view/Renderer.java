package com.ogurlek.chess.view;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ogurlek.chess.Chess;
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

	private static final int VIRTUAL_WIDTH = 480;
	private static final int VIRTUAL_HEIGHT = 640;
	private static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	
	GameWorld world;
	Chess game;
	OrthographicCamera camera;
	Rectangle viewport;
	SpriteBatch batch;
	Texture boardTexture;
	Sprite boardSprite;
	Texture yellowTexture;
	Sprite yellowSprite;
	Texture redTexture;
	Sprite redSprite;
	Texture blueTexture;
	Sprite blueSprite;
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

		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		camera.translate(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2);

		batch = new SpriteBatch();

		piecesAtlas = new TextureAtlas("data/chesspieces.pack");
		buttonAtlas = new TextureAtlas("data/button.pack");
		skin = new Skin();
		skin.addRegions(piecesAtlas);
		skin.addRegions(buttonAtlas);

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		boardTexture = new Texture("data/board.png");
		yellowTexture = new Texture("data/yellowtile.png");
		redTexture = new Texture("data/redtile.png");
		blueTexture = new Texture("data/bluetile.png");

		white = new BitmapFont(Gdx.files.internal("data/whitefont.fnt"), false);
		black = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);

		boardSprite = new Sprite(boardTexture);
		yellowSprite = new Sprite(yellowTexture);
		redSprite = new Sprite(redTexture);
		blueSprite = new Sprite(blueTexture);

		setupStage();
	}

	@SuppressWarnings("deprecation")
	public void resize(int width, int height) {
		Vector2 newVirtualRes= new Vector2(0f, 0f);
		Vector2 crop = new Vector2(width, height);

		newVirtualRes.set(Scaling.fit.apply((float)VIRTUAL_WIDTH, (float)VIRTUAL_HEIGHT, (float)width, (float)height));

		crop.sub(newVirtualRes);
		crop.mul(.5f);
		
		viewport = new Rectangle(crop.x, crop.y, newVirtualRes.x, newVirtualRes.y);
		this.world.updateViewport(viewport, crop.x, crop.y);
		
		System.out.println("viewport width: "+newVirtualRes.x+" height: "+newVirtualRes.y);
		System.out.println("crop x: "+crop.x+" crop y: "+crop.y);
	}

	public void render(float delta){
		camera.update();
        camera.apply(Gdx.gl10);
 
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		Tile[][] tiles = world.getBoard().getTiles();
		MovementMap movement = world.getMovement();
		Piece tempPiece;
		PieceType tempPieceType;
		Sprite tempSprite = null;

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		boardSprite.draw(batch);

		// Drawing Board and Pieces
		for(int y=0; y<8; y++){
			for(int x=0; x<8; x++){
				if(movement.getMovement(x,y) == Movement.MOVE){
					yellowSprite.setX(x*60);
					yellowSprite.setY(420 - (y*60));
					yellowSprite.draw(batch);
				}
				if(movement.getMovement(x,y) == Movement.ATTACK){
					redSprite.setX(x*60);
					redSprite.setY(420 - (y*60));
					redSprite.draw(batch);
				}
				else if(movement.getMovement(x,y) == Movement.SELF){
					blueSprite.setX(x*60);
					blueSprite.setY(420 - (y*60));
					blueSprite.draw(batch);
				}

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
		button.setX((Gdx.graphics.getWidth() / 2 - (button.getWidth() / 2)));
		button.setY(Gdx.graphics.getHeight() - 110);

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
		checkLabel.setY(Gdx.graphics.getHeight() - 100);
		checkLabel.setWidth(Gdx.graphics.getWidth());
		checkLabel.setAlignment(Align.center);
		checkLabel.setVisible(false);

		turnLabel = new Label("White Player's Turn", ls);
		turnLabel.setX(0);
		turnLabel.setY(Gdx.graphics.getHeight() - 50);
		turnLabel.setWidth(Gdx.graphics.getWidth());
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
