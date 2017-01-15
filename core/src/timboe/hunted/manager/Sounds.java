package timboe.hunted.manager;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import timboe.hunted.Param;
import timboe.hunted.Utility;

/**
 * Created by Tim on 15/01/2017.
 */
public class Sounds {

  Music chaseMusic = Gdx.audio.newMusic(Gdx.files.internal("stormfront.ogg"));
  Music ambiance = Gdx.audio.newMusic(Gdx.files.internal("EoT.mp3"));

  private final int nChaseStarts = 4;
  private final float chaseStarts[] = {0f, 44.7f, 141f, 238f};

  private static Sounds ourInstance = new Sounds();

  public static Sounds getInstance() {
    return ourInstance;
  }

  private Sounds() {
  }

  public void startAmbiance() {
    ambiance.setPosition(0f);
    ambiance.setLooping(true);
    ambiance.play();
  }

  public void stopAmbiance() {
    ambiance.stop();
  }

  public void startChase() {
    chaseMusic.setPosition(chaseStarts[Utility.r.nextInt(nChaseStarts) ]);
    chaseMusic.setVolume(1f);
    chaseMusic.play();
  }

  public void chaseVolume(float v) {
    chaseMusic.setVolume(v);
  }

  public void endChase() {
    chaseMusic.stop();
  }

}
