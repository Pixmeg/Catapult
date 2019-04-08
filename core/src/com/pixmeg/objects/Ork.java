package com.pixmeg.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixmeg.Constants;
import com.pixmeg.DemoScreen;
import com.pixmeg.States;

public class Ork extends Sprite {

    private DemoScreen demoScreen;
    private Body body;
    private World world;

    private TextureRegion region;

    private Vector2 position;

    public States previousState, currentState;

    public float index;
    public float stateTimer;

    public String name;

    public float health = Constants.ORK_HEALTH;

    public boolean DEAD;

    private ParticleEffect effect;
    private FrameBuffer fbo;
    private SpriteBatch batch;

    Sprite sprite;


    public Ork(DemoScreen demoScreen, TextureRegion region, Vector2 orkPosition, String name){
        this.demoScreen = demoScreen;
        world = demoScreen.getWorld();
        this.region = region;

        position = orkPosition;

        this.name = name;

        currentState = States.STANDING;
        previousState = currentState;

        setRegion(region);
        createBody();

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888,(int)Constants.V_WIDTH,(int)Constants.V_HEIGHT,false);
        batch = demoScreen.batch;

    }

    public void update(float delta){

        if(DEAD){
            renderFbo();
            batch.setProjectionMatrix(demoScreen.camera.combined);
            batch.begin();
            sprite.draw(batch);
            batch.end();
        }

        else {
            setBounds(body.getPosition().x * Constants.PPM + getRegionWidth() / 4 + 5, body.getPosition().y * Constants.PPM - Constants.ORK_HEIGHT / 2 - 2, -getRegionWidth(), getRegionHeight());
        }
    }

    private void createBody(){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(position.x/ Constants.PPM,position.y/Constants.PPM);
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.ORK_WIDTH/(2*Constants.PPM),Constants.ORK_HEIGHT/(2*Constants.PPM));

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.filter.categoryBits = Constants.ORK_BIT;
        fdef.filter.maskBits = Constants.GROUND_BIT | Constants.ARROW_BIT | Constants.BRIDGE_BIT | Constants.CATAPULT_BIT;

        body.createFixture(fdef).setUserData(this);
        shape.dispose();

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

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public Body getBody() {
        return body;
    }

    public void setIndex(float index) {
        this.index = index;
    }
}
