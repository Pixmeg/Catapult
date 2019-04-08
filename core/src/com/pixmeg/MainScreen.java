package com.pixmeg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainScreen extends ScreenAdapter implements InputProcessor {

    private GameClass gameClass;
    private Viewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;


    private Vector2 anchor;
    private Vector2 firingPosition;
    private float distance, angle;

    private Array<Body> deadBodies;


    public MainScreen(GameClass gameClass) {
        this.gameClass = gameClass;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Constants.V_WIDTH,Constants.V_HEIGHT,camera);
        renderer = new ShapeRenderer();

        world = new World(new Vector2(0,-10f),false);
        b2dr = new Box2DDebugRenderer();

        anchor = new Vector2(100,120);
        firingPosition = anchor.cpy();

        deadBodies = new Array<Body>();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        createGround();
        createBox(620,60,5,30);
        createBox(650,85,40,5);
        createBox(680,60,5,30);
    }




    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(camera.combined);
        renderer.setColor(Color.ORANGE);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.circle(anchor.x,anchor.y,5);
        renderer.setColor(Color.WHITE);
        renderer.circle(firingPosition.x,firingPosition.y,5);
        renderer.line(firingPosition,anchor);
        renderer.end();


        b2dr.render(world,camera.combined.scl(Constants.PPM));
        camera.update();

        world.step(1/60f,6,2);

        if(deadBodies != null) {
            for (Body ballBody : deadBodies) {
                if (ballBody.getPosition().y * Constants.PPM < 0) {
                    deadBodies.removeValue(ballBody, true);
                    world.destroyBody(ballBody);
                }
            }
        }
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

    private void calculateAngleAndDistanceForBall(int screenX, int screenY){
        firingPosition.set(screenX,screenY);
        viewport.unproject(firingPosition);

        distance = distanceBetweenTwoPoints();
        angle = angleBetweenTwoPoints();


        if(distance > Constants.MAX_DISTANCE){
            distance = Constants.MAX_DISTANCE;
        }

        firingPosition.set(anchor.x + distance*-MathUtils.cos(angle),(anchor.y + distance*-MathUtils.sin(angle)));

    }

    public void createBall(){
        Body body;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(anchor.x/ Constants.PPM,anchor.y/Constants.PPM));
        body = world.createBody(bdef);


        CircleShape shape = new CircleShape();
        shape.setRadius(10/Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;

        body.createFixture(fdef);

        shape.dispose();

        float velX = Constants.MAX_STRENGTH * MathUtils.cos(angle) * distance/Constants.MAX_DISTANCE;
        float velY = Constants.MAX_STRENGTH * MathUtils.sin(angle) * distance/Constants.MAX_DISTANCE;

        body.setLinearVelocity(velX,velY);

        deadBodies.add(body);
    }

    public void createGround(){
        Body body;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(new Vector2(400/ Constants.PPM,10/Constants.PPM));
        body = world.createBody(bdef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(500/Constants.PPM,10/Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;

        body.createFixture(fdef);
        shape.dispose();
    }

    public void createBox(float x, float y, float w, float h){
        Body body;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(x/ Constants.PPM,y/Constants.PPM));
        body = world.createBody(bdef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/Constants.PPM,h/Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;

        body.createFixture(fdef);
        shape.dispose();

        deadBodies.add(body);
    }



    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }


    @Override
    public void dispose() {
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
        calculateAngleAndDistanceForBall(screenX,screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        calculateAngleAndDistanceForBall(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        createBall();
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
