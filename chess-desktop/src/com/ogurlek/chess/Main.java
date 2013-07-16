package com.ogurlek.chess;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Chess";
		cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 640;
		
		new LwjglApplication(new Chess(), cfg);
	}
}
