package com.pixmeg;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.pixmeg.objects.Arrow;
import com.pixmeg.objects.Ork;

public class WorldContactListener implements ContactListener {

    private DemoScreen demoScreen;
    private Array<StickyInfo> stickyPairs;
    private Array<Arrow>  arrowArray;
    private TextureAtlas atlas;

    public WorldContactListener(DemoScreen demoScreen, Array<StickyInfo> stickyPairs, Array<Arrow> arrowArrow, TextureAtlas atlas){
        this.demoScreen = demoScreen;
        this.stickyPairs = stickyPairs;
        this.arrowArray = arrowArrow;
        this.atlas = atlas;
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {


    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int collision = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collision){
            case Constants.ARROW_BIT | Constants.ORK_BIT:
                if(fixA.getFilterData().categoryBits == Constants.ARROW_BIT  && impulse.getNormalImpulses()[0] > Constants.ORK_HARDNESS ){
                    if(((Ork)fixB.getUserData()).currentState != States.DIE) {
                        ((Ork) fixB.getUserData()).health -= 3;
                        ((Ork) fixB.getUserData()).currentState = States.HURT;
                    }

                    ((Arrow) fixA.getUserData()).setRegion(atlas.findRegion("spear1"));
                    System.out.println(((Ork)fixB.getUserData()).name+"     "+((Ork)fixB.getUserData()).currentState);

                    Filter filter = fixA.getFilterData();
                    filter.maskBits = Constants.GROUND_BIT | Constants.ARROW_BIT | Constants.BRIDGE_BIT;
                    fixA.setFilterData(filter);

                    StickyInfo si = new StickyInfo(demoScreen);
                    si.targetBody = fixB.getBody();
                    si.arrowBody = fixA.getBody();
                    stickyPairs.add(si);
                }
                else if(fixB.getFilterData().categoryBits == Constants.ARROW_BIT  && impulse.getNormalImpulses()[0] > Constants.ORK_HARDNESS)
                {
                    if(((Ork)fixA.getUserData()).currentState != States.DIE) {
                        ((Ork) fixA.getUserData()).health -= 3;
                        ((Ork) fixA.getUserData()).currentState = States.HURT;
                    }

                    ((Arrow) fixB.getUserData()).setRegion(atlas.findRegion("spear1"));

                    System.out.println(((Ork)fixA.getUserData()).name+"     "+((Ork)fixA.getUserData()).currentState);


                    Filter filter = fixB.getFilterData();
                    filter.maskBits = Constants.GROUND_BIT  | Constants.BRIDGE_BIT;
                    fixB.setFilterData(filter);

                    StickyInfo si = new StickyInfo(demoScreen);
                    si.targetBody = fixA.getBody();
                    si.arrowBody = fixB.getBody();
                    stickyPairs.add(si);
                }

                else {
                    if(fixA.getFilterData().categoryBits == Constants.ARROW_BIT){
                        if(((Ork)fixB.getUserData()).currentState != States.DIE){
                            ((Arrow)fixA.getUserData()).currentState = States.DIE;
                            ((Ork)fixB.getUserData()).currentState = States.HURT;
                            ((Ork)fixB.getUserData()).health -= 1;
                        }

                    }
                    else {
                        if(((Ork)fixA.getUserData()).currentState != States.DIE) {
                            ((Arrow) fixB.getUserData()).currentState = States.DIE;
                            ((Ork) fixA.getUserData()).currentState = States.HURT;
                            ((Ork)fixA.getUserData()).health -= 1;
                        }
                    }
                }


        }




    }
}
