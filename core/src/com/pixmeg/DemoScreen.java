package com.pixmeg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixmeg.AI.SteeringEntity;
import com.pixmeg.objects.Arrow;
import com.pixmeg.objects.Catapult;
import com.pixmeg.objects.Ork;

public class DemoScreen extends ScreenAdapter implements InputProcessor {

    private GameClass gameClass;
    private AssetManager manager;
    public SpriteBatch batch;
    private Viewport viewport;
    public OrthographicCamera camera;
    private ShapeRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public Vector2 anchor;
    private Vector2 firingPosition;
    public float distance, angle;

    private Ork ork1,ork2,ork3;
    private Array<Ork> orkArray;
    private SteeringEntity ork1Steer,ork2Steer,ork3Steer,catapultSteer;
    Arrive<Vector2> arrive1,arrive2,arrive3;
    Array<SteeringEntity> steeringEntities;
    boolean moveOrk;

    Array<TextureRegion> standingArray, runningArray, attackArray, hurtArray, dieArray;
    Animation<TextureRegion> standAnime, runAnime, attackAnime, hurtAnime, dieAnime;

    TextureAtlas atlas;
    TextureRegion chest;

    private Catapult catapult;
    private Sprite frame,leftWheel,rightWheel, bow,spear;

    boolean readyToShoot;
    float currentAngle,previousAngle;
    Array<StickyInfo> stickyPairs;
    Array<Arrow> arrowArray;

    Sprite loadArrow;

    Array<StickyInfo> stickyInfos;

    ParticleEffect orkParticleEffect;


    public DemoScreen(GameClass gameClass) {
        this.gameClass = gameClass;
        manager = gameClass.manager;
        batch = gameClass.batch;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Constants.V_WIDTH,Constants.V_HEIGHT,camera);
        camera.position.set(Constants.V_WIDTH/2,Constants.V_HEIGHT/2,0);
        camera.update();
        renderer = new ShapeRenderer();

        world = new World(new Vector2(0,-10f),false);
        b2dr = new Box2DDebugRenderer();

        anchor = new Vector2(90,120);
        firingPosition = anchor.cpy();

        orkArray = new Array<Ork>();

        atlas = manager.get("images/textureAtlas.atlas",TextureAtlas.class);

        chest = atlas.findRegion("chest");

        catapult = new Catapult(this, new Vector2(400,250));
        attachCatapultTextures();

        stickyPairs = new Array<StickyInfo>();

        arrowArray = new Array<Arrow>();

        loadArrow = new Sprite(atlas.findRegion("spear"));

        stickyInfos = new Array<StickyInfo>();

        orkParticleEffect = new ParticleEffect();
        orkParticleEffect.load(Gdx.files.internal("particles/particles1.p"), Gdx.files.internal("particles"));
        orkParticleEffect.start();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);

        map = new TmxMapLoader().load("map/level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map,1);
        TiledObjectLayer.objectLayerParser(world,map.getLayers().get("object").getObjects());

        steeringEntities = new Array<SteeringEntity>();
        catapultSteer = new SteeringEntity(catapult.getFrameBody(),30,20,10,5,100);
        setUpOrkEntities();

        initAnimationArrays();

        world.setContactListener(new WorldContactListener(this,stickyPairs,arrowArray,atlas));
    }

    public void setUpOrkEntities(){
        TextureRegion region = manager.get("images/textureAtlas.atlas", TextureAtlas.class).findRegion("JUMP",0);
        ork1 = new Ork(this,region,new Vector2(2000,130),"ork1");
        orkArray.add(ork1);

        ork1Steer = new SteeringEntity(ork1.getBody(),10,100,90,5,100);

        arrive1 = new Arrive<Vector2>(ork1Steer, catapultSteer);
        arrive1.setDecelerationRadius(150);
        arrive1.setTimeToTarget(0.01f);
        arrive1.setArrivalTolerance(80);

        ork1Steer.setBehavior(arrive1);
//--------------------->
     /*   ork2 = new Ork(this,region,new Vector2(1840,130),"ork2");
        orkArray.add(ork2);

        ork2Steer = new SteeringEntity(ork2.getBody(),10,60,80,5,100);

        arrive2 = new Arrive<Vector2>(ork2Steer, catapultSteer);
        arrive2.setDecelerationRadius(150);
        arrive2.setTimeToTarget(0.01f);
        arrive2.setArrivalTolerance(80);

        ork2Steer.setBehavior(arrive2);
//--------------------->
        ork3 = new Ork(this,region,new Vector2(2050,130),"ork3");
        orkArray.add(ork3);

        ork3Steer = new SteeringEntity(ork3.getBody(),10,80,70,5,100);

        arrive3 = new Arrive<Vector2>(ork3Steer, catapultSteer);
        arrive3.setDecelerationRadius(150);
        arrive3.setTimeToTarget(0.01f);
        arrive3.setArrivalTolerance(80);

        ork3Steer.setBehavior(arrive3);*/

        steeringEntities.add(ork1Steer);
      //  steeringEntities.add(ork2Steer);
      //  steeringEntities.add(ork3Steer);
    }

    public void initAnimationArrays(){

        standingArray = new Array<TextureRegion>();
        runningArray = new Array<TextureRegion>();
        attackArray = new Array<TextureRegion>();
        hurtArray = new Array<TextureRegion>();
        dieArray = new Array<TextureRegion>();

        TextureAtlas atlas = manager.get("images/textureAtlas.atlas",TextureAtlas.class);
        for(int i = 0; i<=6; i++){
            standingArray.add(atlas.findRegion("IDLE",i));
            runningArray.add(atlas.findRegion("RUN",i));
            attackArray.add(atlas.findRegion("ATTAK",i));
            hurtArray.add(atlas.findRegion("HURT",i));
            dieArray.add(atlas.findRegion("JUMP",i));
        }

        standAnime = new Animation<TextureRegion>(1/6f,standingArray, Animation.PlayMode.LOOP_REVERSED);
        runAnime = new Animation<TextureRegion>(1/8f,runningArray, Animation.PlayMode.LOOP);
        attackAnime = new Animation<TextureRegion>(1/10f,attackArray, Animation.PlayMode.LOOP_REVERSED);
        hurtAnime = new Animation<TextureRegion>(1/10f,hurtArray, Animation.PlayMode.NORMAL);
        dieAnime = new Animation<TextureRegion>(1/6f,dieArray, Animation.PlayMode.LOOP_REVERSED);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveCamera(delta);

        mapRenderer.setView(camera);
        mapRenderer.render();

        moveCatapult();

        if(moveOrk == false) {
            if (catapult.getFrameBody().getPosition().x * Constants.PPM > 1000) {
                moveOrk = true;
                for (Ork ork : orkArray) {
                    ork.currentState = States.RUNNING;
                }
            }
        }

        if(stickyInfos.size > 0){
            moveOrk = false;
        }
        else if(stickyInfos.size == 0 && ork1.currentState != States.STANDING){
            moveOrk = true;
        }


        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        loadArrow.draw(batch);
        batch.draw(chest,Constants.CHEST_LOCATION.x,Constants.CHEST_LOCATION.y,chest.getRegionWidth()/2,chest.getRegionHeight()/2);
        for(Ork ork:orkArray){
            ork.draw(batch);
        }

        for(Arrow arrow:arrowArray){
            arrow.draw(batch);
        }

        drawCatapult(batch);


        if(ork1.currentState == States.DIE){

        }
        batch.end();

        for(StickyInfo si:stickyInfos) {
            si.update(batch,delta);
        }

       // b2dr.render(world,camera.combined.scl(Constants.PPM));
        camera.update();

        for(Arrow arrow:arrowArray){
            applyDragForce(arrow.getBody());
        }

        world.step(1/60f,6,2);

        for(StickyInfo si:stickyPairs){
            Vector2 worldCoordAnchorPoints = si.arrowBody.getWorldPoint(new Vector2(2f,0));
            WeldJointDef jdef = new WeldJointDef();
            jdef.bodyA = si.targetBody;
            jdef.bodyB = si.arrowBody;
            jdef.localAnchorA.set(jdef.bodyA.getLocalPoint(worldCoordAnchorPoints));
            jdef.localAnchorB.set(jdef.bodyB.getLocalPoint(worldCoordAnchorPoints));
            jdef.referenceAngle = jdef.bodyB.getAngle() - jdef.bodyA.getAngle();
            WeldJoint weldJoint = (WeldJoint) world.createJoint(jdef);

            ParticleEffect particleEffect = new ParticleEffect();
            particleEffect.load(Gdx.files.internal("particles/particles.p"),Gdx.files.internal("particles"));
            particleEffect.start();

            particleEffect.setPosition(worldCoordAnchorPoints.x*Constants.PPM,worldCoordAnchorPoints.y*Constants.PPM);
            si.setEffect(particleEffect,worldCoordAnchorPoints.x*Constants.PPM,worldCoordAnchorPoints.y*Constants.PPM);
            stickyInfos.add(si);

        }
        stickyPairs.clear();

        clearDeadBodies();
    }

    public void update(float delta){

        for(Ork ork:orkArray){
            ork.stateTimer = ork.previousState == ork.currentState ? ork.stateTimer +delta :0;
            if(ork.previousState != ork.currentState)
                ork.previousState = ork.currentState;

            if(ork.currentState == States.STANDING){
                TextureRegion region = standAnime.getKeyFrame(ork.stateTimer,false);
                ork.setRegion(region);
            }
            else if(ork.currentState == States.RUNNING){
                TextureRegion region = runAnime.getKeyFrame(ork.stateTimer,true);
                ork.setRegion(region);
            }
            else if(ork.currentState == States.ATTACK){
                TextureRegion region = attackAnime.getKeyFrame(ork.stateTimer,true);
                ork.setRegion(region);
            }
            else if(ork.currentState == States.HURT){
                TextureRegion region = hurtAnime.getKeyFrame(ork.stateTimer,false);
                ork.setRegion(region);
            }
            else if(ork.currentState == States.DIE){
                System.out.println(ork.name+"           TR DIE");
                TextureRegion region = dieAnime.getKeyFrame(ork.stateTimer,true);
                ork.setRegion(region);
            }
            else
                continue;

            ork.update(delta);

            if(ork.currentState == States.HURT){
                if(hurtAnime.isAnimationFinished(ork.stateTimer)){
                    ork.currentState = States.RUNNING;
                }
            }
        }

        updateCatapultTextures(delta);

        if(moveOrk){
            for(SteeringEntity entity:steeringEntities){
                entity.update(delta);
            }
        }

        for(Arrow arrow:arrowArray){
            arrow.update(delta);
        }

        if(ork1.DEAD){
            ork1.update(delta);
        }

    }


    private void attachCatapultTextures(){

        frame = new Sprite();
        frame.setRegion(atlas.findRegion("chassis"));

        leftWheel = new Sprite();
        leftWheel.setRegion(atlas.findRegion("left-wheel"));

        rightWheel = new Sprite();
        rightWheel.setRegion(atlas.findRegion("right-wheel"));

        bow = new Sprite();
        bow.setRegion(atlas.findRegion("bow"));

        spear = new Sprite();
        spear.setRegion(atlas.findRegion("spear"));

    }

    private void updateCatapultTextures(float delta){
        anchor.set(catapult.getFrameBody().getPosition().x*Constants.PPM - 3* bow.getRegionWidth()/8-8+113,catapult.getFrameBody().getPosition().y*Constants.PPM + bow.getRegionHeight()/2+20);

        frame.setOriginCenter();
        frame.setRotation(catapult.getFrameBody().getAngle()*MathUtils.radiansToDegrees);
        frame.setBounds(catapult.getFrameBody().getPosition().x*Constants.PPM - 3*frame.getRegionWidth()/8,catapult.getFrameBody().getPosition().y*Constants.PPM - Constants.CATAPULT_FRAME_HEIGHT/2,3*frame.getRegionWidth()/4,3*frame.getRegionHeight()/4);

        leftWheel.setOriginCenter();
        leftWheel.setRotation(catapult.getLeftWheelBody().getAngle()*MathUtils.radiansToDegrees);
        leftWheel.setBounds(catapult.getLeftWheelBody().getPosition().x*Constants.PPM - Constants.LEFT_WHEEL_RADIUS,catapult.getLeftWheelBody().getPosition().y*Constants.PPM - Constants.LEFT_WHEEL_RADIUS,2*Constants.LEFT_WHEEL_RADIUS,2*Constants.LEFT_WHEEL_RADIUS);

        rightWheel.setOriginCenter();
        rightWheel.setRotation(catapult.getRightWheelBody().getAngle()*MathUtils.radiansToDegrees);
        rightWheel.setBounds(catapult.getRightWheelBody().getPosition().x*Constants.PPM - Constants.RIGHT_WHEEL_RADIUS,catapult.getRightWheelBody().getPosition().y*Constants.PPM - Constants.RIGHT_WHEEL_RADIUS,2*Constants.RIGHT_WHEEL_RADIUS,2*Constants.RIGHT_WHEEL_RADIUS);

        bow.setOrigin(113,10);
        if(readyToShoot){
            currentAngle = angle*MathUtils.radiansToDegrees;
            previousAngle = currentAngle;
        }
        else
        {
            currentAngle = previousAngle;
        }
        bow.setRotation(currentAngle);
        bow.setBounds(catapult.getFrameBody().getPosition().x*Constants.PPM - 3* bow.getRegionWidth()/8-8,catapult.getFrameBody().getPosition().y*Constants.PPM + bow.getRegionHeight()/2,3* bow.getRegionWidth()/4,3* bow.getRegionHeight()/4);

        loadArrow.setOriginCenter();
        loadArrow.setRotation(currentAngle);
        loadArrow.setBounds(anchor.x - loadArrow.getRegionWidth()/2,anchor.y- loadArrow.getRegionHeight()/2, loadArrow.getRegionWidth(), loadArrow.getRegionHeight());

    }

    private void drawCatapult(Batch batch){
        frame.draw(batch);
        bow.draw(batch);
        leftWheel.draw(batch);
        rightWheel.draw(batch);
    }

    private void moveCatapult(){
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            catapult.getLeftWheelJoint().setMotorSpeed(catapult.getLeftWheelJoint().getMotorSpeed()-0.1f);
            catapult.getRightWheelJoint().setMotorSpeed(catapult.getRightWheelJoint().getMotorSpeed()-0.1f);
        }

        else if(Gdx.input.isKeyPressed(Input.Keys.A)){
            catapult.getLeftWheelJoint().setMotorSpeed(catapult.getLeftWheelJoint().getMotorSpeed()+0.1f);
            catapult.getRightWheelJoint().setMotorSpeed(catapult.getRightWheelJoint().getMotorSpeed()+0.1f);
        }

    }

    private void moveCamera(float delta){

        Vector2 pos = catapult.getFrameBody().getPosition();

        camera.position.set(pos.x*Constants.PPM + 170, Constants.V_HEIGHT/2,0);

      /* if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
           camera.translate(5,0);
       }

       else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
           camera.translate(-5,0);
       }*/

       if(camera.position.x < Constants.V_WIDTH/2){
           camera.position.set(new Vector2(Constants.V_WIDTH/2,Constants.V_HEIGHT/2),0);
       }

        camera.update();
    }


    private float angleBetweenTwoPoints(){
        float angle = MathUtils.atan2(anchor.y - firingPosition.y,anchor.x - firingPosition.x);
         angle %= MathUtils.PI2;
         if(angle < 0){
             angle += MathUtils.PI2;
         }

         return angle;
    }

    private float distanceBetweenTwoPoints(){
        return (float)Math.sqrt((anchor.x-firingPosition.x)*(anchor.x-firingPosition.x) + (anchor.y-firingPosition.y)*(anchor.y-firingPosition.y));
    }

    private void calculateAngleAndDistanceForArrow(int screenX, int screenY){
        anchor.set(catapult.getFrameBody().getPosition().x*Constants.PPM - 3* bow.getRegionWidth()/8-8+113,catapult.getFrameBody().getPosition().y*Constants.PPM + bow.getRegionHeight()/2+20);
        firingPosition.set(screenX,screenY);
        viewport.unproject(firingPosition);

        distance = distanceBetweenTwoPoints();
        angle = angleBetweenTwoPoints();

        if(angle > Constants.LOWER_ANGLE){
            if(angle > Constants.UPPER_ANGLE){
                angle = 0;
            }

            else {
                angle = Constants.LOWER_ANGLE;
            }
        }


        if(distance > Constants.MAX_DISTANCE){
            distance = Constants.MAX_DISTANCE;
        }

        firingPosition.set(anchor.x + distance*-MathUtils.cos(angle),(anchor.y + distance*-MathUtils.sin(angle)));

    }



    private void applyDragForce(Body body){
        Vector2 pointingDirection = body.getWorldVector(new Vector2(1,0));
        Vector2 flightDirection = body.getLinearVelocity();
        float flightSpeed = flightDirection.len();

        float dot = flightDirection.dot(pointingDirection);
        dot = dot > 0 ? dot :-dot ;
        float dragMagnitude = (dot)*flightSpeed*flightSpeed*Constants.DRAG_CONSTANT*body.getMass();

        Vector2 arrowTailPosition = body.getWorldPoint(new Vector2(-1.4f,0));

        flightDirection = flightDirection.nor();
        Vector2 dragForce = new Vector2();
        dragForce.x = -flightDirection.x*dragMagnitude;
        dragForce.y = -flightDirection.y*dragMagnitude;
        body.applyForce(dragForce,arrowTailPosition,true);

    }

    private void clearDeadBodies(){
        for(Ork ork:orkArray){
            if(ork.health < 0) {
                ork.currentState = States.DIE;
                if(ork.name.equals("ork1")){
                    steeringEntities.removeValue(ork1Steer,true);
                }

                else if(ork.name.equals("ork2")){
                    steeringEntities.removeValue(ork2Steer,true);
                }

                else if(ork.name.equals("ork3")){
                    steeringEntities.removeValue(ork3Steer,true);
                }

                if(dieAnime.isAnimationFinished(ork.stateTimer)) {
                    System.out.println(ork.name+"    DEAD ");
                    orkArray.removeValue(ork, true);
                    ork.DEAD = true;
                    orkParticleEffect.setPosition(ork.getBody().getPosition().x*Constants.PPM,ork.getBody().getPosition().y*Constants.PPM);
                    ork.setEffect(orkParticleEffect);
                    world.destroyBody(ork.getBody());
                }
            }
        }

        for(Arrow arrow:arrowArray){
            if(arrow.currentState == States.DIE){
                world.destroyBody(arrow.getBody());
                arrowArray.removeValue(arrow,true);
            }
        }

        for (StickyInfo si:stickyInfos){
            if(si.effect.isComplete()){
                si.effect.dispose();
                stickyInfos.removeValue(si,true);
                ((Arrow)si.arrowBody.getUserData()).currentState = States.DIE;
            }
        }
    }

    public World getWorld() {
        return world;
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }


    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        readyToShoot = true;
        calculateAngleAndDistanceForArrow(screenX,screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        calculateAngleAndDistanceForArrow(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        readyToShoot = false;
        arrowArray.add(new Arrow(this,atlas.findRegion("spear")));
        firingPosition.set(anchor.cpy());
        return true;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
