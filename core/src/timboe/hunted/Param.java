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
  public static final Color FLAME = new Color(.1f, .1f, .1f, .4f);
  public static final int PLAYER_TORCH_STRENGTH = 25;
  public static final float AMBIENT_LIGHT = 1f;
  public static final float TORCH_CHANCE = .9f; // Chance to place torch on valid surface
  public static final int TORCH_SPACING = 5;

  public static final float PLAYER_SPEED = 10f;
  public static final float BIGBAD_RUSH = 3f * PLAYER_SPEED;
  public static final float BIGBAD_SPEED = .8f * PLAYER_SPEED;
  public static final float BIGBAD_ANGULAR_SPEED = (float)Math.PI/90f;

  public static final float PLAYER_SMELL = 0.001f;
  public static final float SMELL_SPREAD = 0.05f; // [Fraction] How much smell moves/is lost per tick
  public static final float SMELL_DISSAPATE = 0.5f; // How much of the moving smell is lost

  public static final int MIN_ROOM_SIZE = 5;
  public static final int CORRIDOR_SIZE = MIN_ROOM_SIZE;
}
