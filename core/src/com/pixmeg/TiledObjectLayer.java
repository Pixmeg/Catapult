package com.pixmeg;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class TiledObjectLayer {
    public static void objectLayerParser(World world, MapObjects objects){
        for(MapObject object:objects){
            if(object instanceof PolylineMapObject){
                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                Body body = world.createBody(bdef);

                Shape shape = createPolyline((PolylineMapObject) object);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                fdef.density = 1;
                fdef.filter.categoryBits = Constants.GROUND_BIT;
                fdef.filter.maskBits = Constants.ARROW_BIT | Constants.ORK_BIT | Constants.CATAPULT_BIT;

                body.createFixture(fdef);
                shape.dispose();
            }
            else if(object instanceof RectangleMapObject){
                Vector2 position = new Vector2();
                ((RectangleMapObject)object).getRectangle().getPosition(position);

                BodyDef bdef = new BodyDef();
                bdef.position.set((position.x + ((RectangleMapObject) object).getRectangle().getWidth()/2) /Constants.PPM,(position.y+((RectangleMapObject) object).getRectangle().getHeight()/2)/Constants.PPM);
                bdef.type = BodyDef.BodyType.StaticBody;
                Body body = world.createBody(bdef);

                Shape shape = createRectangle((RectangleMapObject) object);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                fdef.density = 1;
                fdef.filter.categoryBits = Constants.BRIDGE_BIT;
                fdef.filter.maskBits = Constants.ARROW_BIT | Constants.CATAPULT_BIT | Constants.ORK_BIT;

                body.createFixture(fdef);
                shape.dispose();
            }
        }
    }

    private static ChainShape createPolyline(PolylineMapObject object){
        float[] vertices = object.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length/2];

        for(int i = 0;i<worldVertices.length;i++){
            worldVertices[i] = new Vector2(vertices[i*2]/Constants.PPM,vertices[i*2+1]/Constants.PPM);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);

        return chainShape;
    }

    private static PolygonShape createRectangle(RectangleMapObject object){

        float width = object.getRectangle().getWidth();
        float height = object.getRectangle().getHeight();

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width/(2*Constants.PPM),height/(2*Constants.PPM));

        return polygonShape;
    }
}
