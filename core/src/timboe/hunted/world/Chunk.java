package timboe.hunted.world;

import com.badlogic.gdx.math.*;
import timboe.hunted.HuntedGame;
import timboe.hunted.render.Sprites;

import java.util.Random;
import java.util.Vector;

/**
 * Created by Tim on 28/12/2016.
 */
public class Chunk {

  private int chunkX;
  private int chunkY;
  private WorldGen.Split split = WorldGen.Split.NONE;
  private Random r;
  private final int splitChance = 10; //%, per split type
  private final int noRoomChance = 25; //%
  private Vector<Rectangle> rooms;


  Chunk(int x, int y, int id) {
    chunkX = x;
    chunkY = y;
    r = new Random(id);
    rooms = new Vector<Rectangle>();
  }

  private void reset() {
    split = WorldGen.Split.NONE;
    rooms.clear();
  }

  public void generate() {
    reset();
    int rnd = r.nextInt(100);
    if (rnd < splitChance) split = WorldGen.Split.BOTH;
    else if (rnd < 2*splitChance) split = WorldGen.Split.HORIZONTAL;
    else if (rnd < 3*splitChance) split = WorldGen.Split.VERTICAL;

    newRandomRoom(1, 1, HuntedGame.CHUNK_SIZE - 2, HuntedGame.CHUNK_SIZE - 2);

  }

  private void newRandomRoom(int xOff, int yOff, int maxX, int maxY) {
    final int rnd = r.nextInt(100);
    if (rnd < noRoomChance) return;

    final int width = HuntedGame.MIN_SIZE + r.nextInt(maxX - HuntedGame.MIN_SIZE);
    final int height = HuntedGame.MIN_SIZE + r.nextInt(maxY - HuntedGame.MIN_SIZE);

    final int x = xOff + r.nextInt( maxX - width);
    final int y = yOff + r.nextInt( maxY - height);

    rooms.add( new Rectangle(x,y,width,height) );
  }

  public void addRoomsToTileMap() {
    for (Rectangle room : rooms) {
      final int xStart = (chunkX * HuntedGame.CHUNK_SIZE) + (int)room.x;
      final int xEnd = xStart + (int)room.width;
      final int yStart = (chunkY * HuntedGame.CHUNK_SIZE) + (int)room.y;
      final int yEnd = yStart + (int)room.height;
      for (int x = xStart; x < xEnd; ++x) {
        for (int y = yStart; y < yEnd; ++y) {
          Sprites.getInstance().getTile(x,y).setIsFloor();
        }
      }
    }
  }
}
