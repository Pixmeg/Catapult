package com.pixmeg;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Constants {

    public static final float V_WIDTH = 800f;
    public static final float V_HEIGHT = 480f;
    public static final float PPM = 32f;

    public static final float MAX_STRENGTH = 30;
    public static final float MAX_DISTANCE = 100;

    public static final float ORK_WIDTH = 180;
    public static final float ORK_HEIGHT  = 300;

    public static final Vector2 CHEST_LOCATION = new Vector2(2150,90);

    public static final float CATAPULT_FRAME_WIDTH = 150;
    public static final float CATAPULT_FRAME_HEIGHT = 60;
    public static final float LEFT_WHEEL_RADIUS = 15;
    public static final float RIGHT_WHEEL_RADIUS = 20;
    public static final float LOWER_ANGLE = MathUtils.PI/6;
    public static final float UPPER_ANGLE = 3*MathUtils.PI/2;
    public static final float DRAG_CONSTANT = 0.001f;
    public static final float ORK_HARDNESS = 4;
    public static final float ORK_HEALTH = 20;




    public static final short ARROW_BIT = 2;
    public static final short CATAPULT_BIT = 4;
    public static final short GROUND_BIT = 8;
    public static final short BRIDGE_BIT = 16;
    public static final short ORK_BIT = 32;

}
