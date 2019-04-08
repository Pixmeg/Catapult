package com.pixmeg.AI;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.pixmeg.Constants;

public class SteeringEntity implements Steerable<Vector2> {

    private Body body;
    private float boundingRadius;
    private boolean tagged;
    private float maxLinearSpeed, maxLinearAcceleration;
    private float maxAngularSpeed, maxAngularAcceleration;

    private SteeringBehavior<Vector2> behavior;
    public SteeringAcceleration<Vector2> steeringAcceleration;

    public SteeringEntity(Body body, float boundingRadius, float maxLinearSpeed, float maxLinearAcceleration, float maxAngularSpeed, float maxAngularAcceleration) {
        this.body = body;
        this.boundingRadius = boundingRadius;
        this.maxLinearSpeed = maxLinearSpeed;
        this.maxLinearAcceleration = maxLinearAcceleration;
        this.maxAngularSpeed = maxAngularSpeed;
        this.maxAngularAcceleration = maxAngularAcceleration;

        steeringAcceleration = new SteeringAcceleration<Vector2>(new Vector2());
    }

    public void update(float delta){
        if(behavior != null){
            behavior.calculateSteering(steeringAcceleration);
            applySteering(steeringAcceleration,delta);
        }
    }

    private void applySteering(SteeringAcceleration<Vector2> steering, float time){
        boolean anyAcceleration = false;

        if(!steering.linear.isZero()){
            Vector2 force = steering.linear.scl(time);
            body.setLinearVelocity(new Vector2(steering.linear.x,0));
            anyAcceleration = true;
        }

        if(steering.angular != 0){
            body.applyTorque(steering.angular*time,true);
            anyAcceleration = true;
        }

        if(anyAcceleration){
            Vector2 velocity = body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            if(currentSpeedSquare > maxLinearSpeed*maxLinearSpeed){
                body.setLinearVelocity(velocity.scl(maxLinearSpeed/(float)Math.sqrt(currentSpeedSquare)));
            }

            if(body.getAngularVelocity() > maxAngularSpeed){
                body.setAngularVelocity(maxAngularSpeed);
            }
        }

    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 1;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x*Constants.PPM,body.getPosition().y*Constants.PPM);
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(vector.y,vector.x);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.y = (float)Math.sin(angle);
        outVector.x = (float)Math.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    public Body getBody() {
        return body;
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior) {
        this.behavior = behavior;
    }
}
