package timboe.hunted;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Tim on 31/12/2016.
 */
public class Param {
  public static final int TILE_SIZE = 32;
  public static final int TILE_X = 128;
  public static final int TILE_Y = 128;

  public static final int DISPLAY_X = 1280;
  public static final int DISPLAY_Y = 720;

  public static final int WORLDGEN_TRIES = 100;

  public static final int CAMERA_LEAD = TILE_SIZE*2;

  public static final int RAYS = 256;
  public static final Color EVIL_FLAME = new Color(1f, 0f, 0f, 1f);
  public static final Color PLAYER_FLAME = new Color(.5f, .5f, .2f, .7f);
  public static final Color WALL_FLAME_CAST_N = new Color(.5f, .5f, .2f, .7f);
  public static final Color WALL_FLAME_CAST_ESW = new Color(.5f, .5f, .2f, .4f);
  public static final Color WALL_FLAME_CAST_C = new Color(.5f, .5f, .2f, .4f);
  public static final Color WALL_FLAME_SPOT = new Color(1f, 0f, 0f, .8f);

  public static final int PLAYER_TORCH_STRENGTH = 15;
  public static final int WALL_TORCH_STRENGTH = PLAYER_TORCH_STRENGTH/2;
  public static final float SMALL_TORCH_STRENGTH = 1.5f;
  public static final Color AMBIENT_LIGHT = new Color(0f, 0f, 0f, .03f);
  public static final float TORCH_CHANCE = .9f; // Chance to place torch on valid surface
  public static final int TORCH_SPACING = 5;
  public static final float TORCH_FLICKER = 2f;
  public static final int MAX_MINI_LIGHT = 20; // Mini lights per key room

  public static final float CHEST_INERTIA_MOD = 0.07f; // Have some travel
  public static final float PLAYER_INERTIA_MOD = 0.1f; // Still quite responsive (smaller = more sluggish)

  public static final float PLAYER_SPEED = 5f;
  public static final float BIGBAD_RUSH = 3f * PLAYER_SPEED;
  public static final float BIGBAD_SPEED = .8f * PLAYER_SPEED;
  public static final float BIGBAD_SPEED_BOOST = .05f * PLAYER_SPEED;
  public static final float BIGBAD_ANGULAR_SPEED = (float)Math.PI/90f;
  public static final int BIGBAD_AI_COOLDOWN = 100;
  public static final int BIGBAD_POUNCE_DISTANCE = 2;

  public static final float BIGBAD_SENSE_DISTANCE = 20f; // Within what line of sight can we see the player

  public static final float PLAYER_SMELL = 0.001f;
  public static final float SMELL_DISSAPATE = PLAYER_SMELL/10f; // How much of the moving smell is lost

  public static final int MIN_ROOM_SIZE = 5;
  public static final int KEY_ROOMS = 3;
  public static final int CORRIDOR_SIZE = MIN_ROOM_SIZE;

  // Physics
  public static final short PLAYER_ENTITY = 0x1;    // 0001
  public static final short BIGBAD_ENTITY = 0x1 << 1; // 0010
  public static final short WORLD_ENTITY = 0x1 << 2; // 0100
  public static final short SENSOR_ENTITY = 0x1 << 3; // 1000
  public static final short TORCH_ENTITY = 0x1 << 4; // 10000
  public static final short CLUTTER_ENTITY = 0x1 << 5;

  public static final short PLAYER_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|SENSOR_ENTITY|CLUTTER_ENTITY;
  public static final short TORCH_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|SENSOR_ENTITY|PLAYER_ENTITY|CLUTTER_ENTITY;
  public static final short BIGBAD_COLLIDES = TORCH_ENTITY;
  public static final short BIGBAD_CAN_SEE_THROUGH = CLUTTER_ENTITY|SENSOR_ENTITY|TORCH_ENTITY;
  public static final short WORLD_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|PLAYER_ENTITY|CLUTTER_ENTITY;
  public static final short CLUTTER_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|PLAYER_ENTITY|CLUTTER_ENTITY;
  public static final short SENSOR_COLLIDES = PLAYER_ENTITY;

  public static final int SWITCH_TIME = 250;

  public static final int ANIM_SPEED = 10; // Frames per anim update
  public static final int MAX_FRAMES = 9;

  public static final int MAX_CRINKLE = 3;

  public static final Color LIGHT_RED = new Color(1f,0f,0f,1f);
  public static final Color LIGHT_GREEN = new Color(0f,1f,0f,1f);
  public static final Color LIGHT_BLUE = new Color(0f,0f,1f,1f);

}
