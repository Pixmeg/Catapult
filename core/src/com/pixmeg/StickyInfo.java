package com.pixmeg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.physics.box2d.Body;

public class StickyInfo {

    public DemoScreen demoScreen;

    public Body arrowBody;
    public Body targetBody;

    ParticleEffect effect;

    private FrameBuffer fbo;
    private SpriteBatch batch;

    float x,y;

    Sprite sprite;

    public float time;

    public StickyInfo(DemoScreen demoScreen){
        this.demoScreen = demoScreen;
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888,(int)Constants.V_WIDTH,(int)Constants.V_HEIGHT,false);
        batch = demoScreen.batch;
    }


    private void renderFbo(){
        fbo.begin();
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        effect.draw(batch, Gdx.graphics.getDeltaTime());
        fbo.end();
        batch.end();

        sprite = new Sprite(fbo.getColorBufferTexture());


        sprite.flip(false,true);
        sprite.setBounds(demoScreen.camera.position.x - 400, demoScreen.camera.position.y-240,800,480);

    }


    public void update(SpriteBatch batch, float delta){
        time += delta;

        renderFbo();
        batch.setProjectionMatrix(demoScreen.camera.combined);
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    public void setEffect(ParticleEffect effect, float x, float y) {
        this.effect = effect;
        this.x = x;
        this.y = y;

    }
}
