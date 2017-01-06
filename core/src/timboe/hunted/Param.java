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

  public static final int CAMERA_LEAD = TILE_SIZE*2;

  public static final int RAYS = 256;
  public static final Color EVIL_FLAME = new Color(1f, 0f, 0f, .7f);
  public static final Color PLAYER_FLAME = new Color(.1f, .1f, .1f, .7f);
  public static final Color WALL_FLAME = new Color(.1f, .1f, .1f, .5f);
  public static final int PLAYER_TORCH_STRENGTH = 15;
  public static final int WALL_TORCH_STRENGTH = PLAYER_TORCH_STRENGTH/2;
  public static final Color AMBIENT_LIGHT = new Color(.1f, .1f, .1f, .02f);
  public static final float TORCH_CHANCE = .9f; // Chance to place torch on valid surface
  public static final int TORCH_SPACING = 5;
  public static final float TORCH_FLICKER = 2f;

  public static final float PLAYER_SPEED = 10f;
  public static final float BIGBAD_RUSH = 3f * PLAYER_SPEED;
  public static final float BIGBAD_SPEED = .75f * PLAYER_SPEED;
  public static final float BIGBAD_ANGULAR_SPEED = (float)Math.PI/90f;

  public static final float BIGBAD_SENSE_DISTANCE = 20f; // Within what line of sight can we see the player

  public static final float PLAYER_SMELL = 0.001f;
  public static final float SMELL_DISSAPATE = PLAYER_SMELL/10f; // How much of the moving smell is lost

  public static final int MIN_ROOM_SIZE = 5;
  public static final int CORRIDOR_SIZE = MIN_ROOM_SIZE;

  // Physics
  public static final short PLAYER_ENTITY = 0x1;    // 0001
  public static final short BIGBAD_ENTITY = 0x1 << 1; // 0010
  public static final short WORLD_ENTITY = 0x1 << 2; // 0100
  public static final short TORCH_SENSOR_ENTITY = 0x1 << 3; // 1000
  public static final short SENSOR_ENTITY = 0x1 << 4; // 10000


}
