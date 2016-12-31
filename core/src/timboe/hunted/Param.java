package timboe.hunted;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Tim on 31/12/2016.
 */
public class Param {
  public static final int TILE_SIZE = 32;
  public static final int TILE_X = 128;
  public static final int TILE_Y = 128;

  public static final int RAYS = 500;
  public static final Color FLAME = new Color(.986f, .345f, .1331f, .4f);
  public static final int PLAYER_TORCH_STRENGTH = 20;

  public static final float PLAYER_SPEED = 10f;
  public static final float BIGBAD_RUSH = 3f * PLAYER_SPEED;
  public static final float BIGBAD_SPEED = .8f * PLAYER_SPEED;

  public static final float PLAYER_SMELL = 0.005f;

  public static final int MIN_ROOM_SIZE = 5;
  public static final int CORRIDOR_SIZE = MIN_ROOM_SIZE;
}