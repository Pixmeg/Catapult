package com.pixmeg.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixmeg.Constants;
import com.pixmeg.DemoScreen;
import com.pixmeg.States;

public class Arrow extends Sprite {
    private DemoScreen demoScreen;
    private Body body;
    private World world;

    private Vector2 anchor;
    private float angle,distance;

    private TextureRegion region;

    public States currentState;


    public Arrow(DemoScreen demoScreen, TextureRegion region){
        this.demoScreen = demoScreen;
        world = demoScreen.getWorld();

        anchor = demoScreen.anchor;
        angle = demoScreen.angle;
        distance = demoScreen.distance;

        this.region = region;

        currentState = States.FLYING;

        setRegion(region);
        createArrow();

        setOriginCenter();
        setRotation(body.getAngle()*MathUtils.radiansToDegrees);
        setBounds(body.getPosition().x*Constants.PPM - 3*getRegionWidth()/8,body.getPosition().y*Constants.PPM - 3*getRegionHeight()/8,3*getRegionWidth()/4,3*getRegionHeight()/4);


    }

    public void update(float delta){
        setOriginCenter();
        setRotation(body.getAngle()*MathUtils.radiansToDegrees);
        setBounds(body.getPosition().x*Constants.PPM - 3*getRegionWidth()/8,body.getPosition().y*Constants.PPM - 3*getRegionHeight()/8,3*getRegionWidth()/4,3*getRegionHeight()/4);


    }

    public void createArrow(){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(anchor.x/ Constants.PPM,anchor.y/Constants.PPM));
        bdef.angle = angle;
        body = world.createBody(bdef);
        body.setUserData(this);
        body.setAngularDamping(3);

        PolygonShape shape = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2( -1.4f,     0 );
        vertices[1] = new Vector2(     0, -0.1f );
        vertices[2] = new Vector2(  2f,     0 );
        vertices[3] = new Vector2(     0,  0.1f );

        shape.set(vertices);


        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;

        Filter filter = new Filter();
        filter.categoryBits = Constants.ARROW_BIT;
        filter.maskBits = Constants.GROUND_BIT  | Constants.ORK_BIT | Constants.BRIDGE_BIT;

        Fixture fixture = body.createFixture(fdef);
        fixture.setFilterData(filter);
        fixture.setUserData(this);

        shape.dispose();

        float velX = Constants.MAX_STRENGTH * MathUtils.cos(angle) * distance/Constants.MAX_DISTANCE;
        float velY = Constants.MAX_STRENGTH * MathUtils.sin(angle) * distance/Constants.MAX_DISTANCE;

        body.setLinearVelocity(velX,velY);

    }

    public Body getBody() {
        return body;
    }

}
