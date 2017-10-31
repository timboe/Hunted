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

  public static final int WORLDGEN_TRIES = 500;

  public static final int CAMERA_LEAD = TILE_SIZE*2;

  public static final int RAYS = 16;
  public static final int RAYS_SMALL = 8;
  public static final int RAYS_PLAYER = 128;
  public static final int RAYS_BIGBAD = 64;

  public static final Color EVIL_FLAME = new Color(1f, 0f, 0f, 1f);
//  public static final Color PLAYER_FLAME = new Color(.5f, .5f, .2f, .9f);
  public static final Color PLAYER_FLAME = new Color(.15f, .15f, 0.0f, .8f);
//  public static final Color WALL_FLAME_CAST_N = new Color(.5f, .5f, .2f, .7f);
  public static final Color WALL_FLAME_CAST = new Color(.15f, .15f, .0f, .8f);
  public static final Color WALL_FLAME_SPOT = new Color(1f, 0f, 0f, .8f);

  public static final float PLAYER_TORCH_STRENGTH = 10f;
  public static final float WALL_TORCH_STRENGTH = 7f;
  public static final float SMALL_TORCH_STRENGTH = 2f;
  public static final Color AMBIENT_LIGHT = new Color(0f, 0f, 0f, .04f);
  public static final Color AMBIENT_FLOODLIGHT = new Color(0f, 0f, 0f, .6f);
  public static final float TORCH_CHANCE = .9f; // Chance to place torch on valid surface
  public static final int TORCH_SPACING = 7;
//  public static final float TORCH_FLICKER = 2f;
  public static final int MAX_MINI_LIGHT = 8; // Mini lights per key room

  public static final float CHEST_INERTIA_MOD = 0.07f; // Have some travel
  public static final float PLAYER_INERTIA_MOD = 0.1f; // Still quite responsive (smaller = more sluggish)

  public static final float PLAYER_SPEED = 5f;
  public static final float PLAYER_SPEED_BOOST = PLAYER_SPEED * 1.5f;
  public static final float PLAYER_SPEED_LOSS = 0.01f;
  public static final float BIGBAD_RUSH = 3f * PLAYER_SPEED;
  public static final float BIGBAD_SPEED = .7f * PLAYER_SPEED;
  public static final float BIGBAD_SPEED_BOOST = .05f * PLAYER_SPEED;
  public static final float BIGBAD_ANGULAR_SPEED = (float)Math.PI/90f;
  public static final int BIGBAD_POUNCE_DISTANCE = 2;
  public static final int BIGBAD_SIXTH_SENSE = 20; //% chance to guess correct direction
  public static final int BIGBAD_SIXTH_SENSE_BOOST = 20; //% chance boost with every key shrine
  public static final float BIGBAD_SENSE_DISTANCE = 20f; // Within what line of sight can we see the player
  public static final float BIGBAD_FARAWAY_BOOST = 1.5f;

  public static final float PLAYER_SMELL = 0.1f;
  public static final float SMELL_DISSAPATE = PLAYER_SMELL/10f; // How much of the moving smell is lost

  public static final int MIN_ROOM_SIZE = 5;
  public static final int MAX_ROOM_SIZE = 25;
  public static final int KEY_ROOMS = 3;
  public static final int CORRIDOR_SIZE = MIN_ROOM_SIZE;

  // Physics
  public static final short PLAYER_ENTITY = 0x1;    // 0001
  public static final short BIGBAD_ENTITY = 0x1 << 1; // 0010
  public static final short WORLD_ENTITY = 0x1 << 2; // 0100
  public static final short SENSOR_ENTITY = 0x1 << 3; // 1000
  public static final short TORCH_ENTITY = 0x1 << 4; // 10000
  public static final short CLUTTER_ENTITY = 0x1 << 5;
  public static final short PIT_ENTITY = 0x1 << 6;

  public static final short PLAYER_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|SENSOR_ENTITY|CLUTTER_ENTITY|PIT_ENTITY;
  public static final short TORCH_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|SENSOR_ENTITY|PLAYER_ENTITY|CLUTTER_ENTITY;
  public static final short BIGBAD_COLLIDES = TORCH_ENTITY;
  public static final short BIGBAD_CAN_SEE_THROUGH = CLUTTER_ENTITY|SENSOR_ENTITY|TORCH_ENTITY|PIT_ENTITY;
  public static final short WORLD_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|PLAYER_ENTITY|CLUTTER_ENTITY|PIT_ENTITY;
  public static final short CLUTTER_COLLIDES = TORCH_ENTITY|WORLD_ENTITY|PLAYER_ENTITY|CLUTTER_ENTITY|PIT_ENTITY;
  public static final short SENSOR_COLLIDES = PLAYER_ENTITY;
  public static final short PIT_COLLIDES = WORLD_ENTITY|PLAYER_ENTITY|CLUTTER_ENTITY|PIT_ENTITY;


  public static final int ANIM_SPEED = 12; // Frames per anim update
  public static final float DESIRED_FPS = 60; // FPS ANIM_SPEED is tuned for
  public static final float FRAME_TIME = (1f/DESIRED_FPS);
  public static final float ANIM_TIME = FRAME_TIME * ANIM_SPEED;
  public static final float SWITCH_TIME = FRAME_TIME * 250f;
  public static final float BIGBAD_AI_COOLDOWN = FRAME_TIME * 100f;
  public static final float CHASE_VOL_MAX = FRAME_TIME * 100f;

  public static final int MAX_FRAMES = 12;
  public static final int MAX_CRINKLE = 3;

  public static final int N_TREASURE = 5;

  public static final float COMPASS_SPEED = 4f;
  public static final float ARROW_SPEED = .7f * COMPASS_SPEED;

  public static final Color LIGHT_RED = new Color(1f,0f,0f,1f);
  public static final Color LIGHT_GREEN = new Color(0f,1f,0f,1f);
  public static final Color LIGHT_BLUE = new Color(0f,0f,1f,1f);

}
