package timboe.hunted.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Sprites;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Tim on 28/12/2016.
 */
public class WorldGen {
  private static WorldGen ourInstance = new WorldGen();

  public static WorldGen getInstance() {
    return ourInstance;
  }

  private Vector<Room> rooms;
  private Random r;

  private final int ROOM_PLACE_TRIES = 1000;
  public final int ROOM_MEAN_SIZE = 15;
  private final int ROOM_STD_D = 5;
  private final int ROOM_BORDER = 1; // minimum spaceing between rooms

  private WorldGen() {
    r = new Random();
    rooms = new Vector<Room>();
  }

  public void generateWorld() {
    placeRooms();
    shrinkRooms();
    addRoomsToTileMap();
  }


  private void placeRooms() {
    int t = 0;
    final int minX = 1;
    final int minY = 1;
    final int maxX = HuntedGame.TILE_X - HuntedGame.MIN_ROOM_SIZE;
    final int maxY = HuntedGame.TILE_Y - HuntedGame.MIN_ROOM_SIZE;
    while (t < ROOM_PLACE_TRIES) {
      boolean pass = true;
      final long w = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      final long h = Math.round(ROOM_MEAN_SIZE + (r.nextGaussian() * ROOM_STD_D));
      final long x = minX + r.nextInt(maxX - minX);
      final long y = minY + r.nextInt(maxY - minY);
      Room room = new Room(x, y, w, h);
      if (w < HuntedGame.MIN_ROOM_SIZE + (2 * ROOM_BORDER)) pass = false;
      else if (h < HuntedGame.MIN_ROOM_SIZE + (2 * ROOM_BORDER)) pass = false;
      else if (x + w >= HuntedGame.TILE_X) pass = false;
      else if (y + h >= HuntedGame.TILE_Y) pass = false;
      for (final Rectangle testRoom : rooms) {
        if (!pass) break;
        if (testRoom.overlaps(room)) {
          pass = false;
        }
      }
      if (pass) rooms.add(room);
      else ++t;
    }
  }

  private void shrinkRooms() {
    for (Rectangle room : rooms) {
      room.set(room.getX(), room.getY(), room.getWidth() - ROOM_BORDER, room.getHeight() - ROOM_BORDER);
    }
  }

  private void addRoomsToTileMap() {
    for (Rectangle room : rooms) {
      for (int x = (int)room.x; x < (int)room.x + (int)room.width; ++x) {
        for (int y = (int)room.y; y < (int)room.y + (int)room.height; ++y) {
          Sprites.getInstance().getTile(x,y).setIsFloor();
        }
      }
    }
  }



}
