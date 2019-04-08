package com.pixmeg;
// Assets used in this project are licensed, You are NOT allowed to use them for any commercial or promotional purposes. But you can use them for personal purpose. And if you require the same asset for commercial purpose you can visit https://www.gamedevmarket.net/tag/catapult/

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameClass extends Game {
	public AssetManager manager;
    public SpriteBatch batch;
    public Screen screen;


    @Override
	public void create () {
        manager = new AssetManager();

        manager.load("images/textureAtlas.atlas", TextureAtlas.class);
        manager.finishLoading();

		batch = new SpriteBatch();

		//For demo version loadDemoScreen()
		loadMainScreen();
	}

	public void loadMainScreen(){
        screen = new MainScreen(this);
        setScreen(screen);
    }

    public void loadDemoScreen(){
        screen = new DemoScreen(this);
        setScreen(screen);
    }

	@Override
	public void dispose () {
        manager.dispose();
		batch.dispose();
		screen.dispose();
	}
}
