package com.pixmeg.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.pixmeg.Constants;
import com.pixmeg.DemoScreen;

public class Catapult {
    private DemoScreen demoScreen;
    private Body frameBody, leftWheelBody, rightWheelBody;
    private WheelJoint leftWheelJoint, rightWheelJoint;
    private World world;

    private Vector2 position;


    public Catapult(DemoScreen demoScreen, Vector2 catapultPosition){
        this.demoScreen = demoScreen;
        world = demoScreen.getWorld();
        position = catapultPosition;

        createBody();


    }

    private void createBody(){

        //----------------------> catapult frame

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(position.x/ Constants.PPM,position.y/Constants.PPM);
        frameBody = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.CATAPULT_FRAME_WIDTH/(2*Constants.PPM),Constants.CATAPULT_FRAME_HEIGHT/(2*Constants.PPM));

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.filter.categoryBits = Constants.CATAPULT_BIT;
        fdef.filter.maskBits = Constants.GROUND_BIT | Constants.BRIDGE_BIT | Constants.ORK_BIT;

        frameBody.createFixture(fdef);
        shape.dispose();

        //---------------------> wheels

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        leftWheelBody = world.createBody(bodyDef);
        rightWheelBody = world.createBody(bodyDef);

        CircleShape circleShapeLeft = new CircleShape();
        circleShapeLeft.setRadius(Constants.LEFT_WHEEL_RADIUS/Constants.PPM);

        FixtureDef fixturedDefLeft = new FixtureDef();
        fixturedDefLeft.shape = circleShapeLeft;
        fixturedDefLeft.density = 1;
        fixturedDefLeft.filter.categoryBits = Constants.CATAPULT_BIT;
        fixturedDefLeft.filter.maskBits = Constants.GROUND_BIT | Constants.BRIDGE_BIT | Constants.ORK_BIT;

        leftWheelBody.createFixture(fixturedDefLeft);

        CircleShape circleShapeRight = new CircleShape();
        circleShapeRight.setRadius(Constants.RIGHT_WHEEL_RADIUS/Constants.PPM);

        FixtureDef fixturedDefRight = new FixtureDef();
        fixturedDefRight.shape = circleShapeRight;
        fixturedDefRight.density = 1;
        fixturedDefRight.filter.categoryBits = Constants.CATAPULT_BIT;
        fixturedDefRight.filter.maskBits = Constants.GROUND_BIT | Constants.BRIDGE_BIT | Constants.ORK_BIT;
        rightWheelBody.createFixture(fixturedDefRight);

        circleShapeRight.dispose();

        //---------------------> wheel joint

        WheelJointDef jdef = new WheelJointDef();
        jdef.enableMotor = true;
        jdef.motorSpeed = 0;
        jdef.maxMotorTorque = 20;

        jdef.bodyA = frameBody;
        jdef.bodyB = leftWheelBody;
        jdef.collideConnected = false;

        jdef.localAnchorA.set(-35/Constants.PPM,-30/Constants.PPM);
        leftWheelJoint = (WheelJoint) world.createJoint(jdef);

        jdef.bodyB = rightWheelBody;
        jdef.localAnchorA.set(30/Constants.PPM,-25/Constants.PPM);
        rightWheelJoint = (WheelJoint)world.createJoint(jdef);

        }

    public Body getFrameBody() {
        return frameBody;
    }

    public Body getLeftWheelBody() {
        return leftWheelBody;
    }

    public Body getRightWheelBody() {
        return rightWheelBody;
    }

    public WheelJoint getLeftWheelJoint() {
        return leftWheelJoint;
    }

    public WheelJoint getRightWheelJoint() {
        return rightWheelJoint;
    }
}
