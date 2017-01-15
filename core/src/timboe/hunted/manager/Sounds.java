package timboe.hunted.manager;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import timboe.hunted.Utility;

/**
 * Created by Tim on 15/01/2017.
 */
public class Sounds {

  boolean musicOn = false;
  boolean sfxOn = false;

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
    if (!musicOn) return;
    ambiance.setPosition(0f);
    ambiance.setLooping(true);
    ambiance.play();
  }

  public void stopAmbiance() {
    ambiance.stop();
  }

  public void startChase() {
    if (!musicOn) return;
    chaseMusic.play();
    chaseMusic.setPosition( chaseStarts[Utility.r.nextInt(nChaseStarts) ]);
    chaseMusic.setVolume(1f);
    ambiance.setVolume(0f);
  }

  public void chaseVolume(float v) {
    chaseMusic.setVolume(v);
    ambiance.setVolume(1f - v);
  }

  public void endChase() {
    chaseMusic.stop();
  }

  public void dispose() {
    chaseMusic.stop();
    ambiance.stop();
    chaseMusic.dispose();
    ambiance.dispose();
  }

}
