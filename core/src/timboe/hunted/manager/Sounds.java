package timboe.hunted.manager;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import timboe.hunted.Utility;

/**
 * Created by Tim on 15/01/2017.
 */
public class Sounds {

  boolean musicOn = true;
  boolean sfxOn = true;

  Music ambiance = Gdx.audio.newMusic(Gdx.files.internal("EoT.mp3"));

  private final int nMonsterCall = 4;
  Sound monsterCall[] = new Sound[nMonsterCall];

  private final int nChaseStarts = 4;
  private int currentChase = 0;
  Music chaseMusic[] = new Music[nChaseStarts];

  private static Sounds ourInstance = new Sounds();

  public static Sounds getInstance() {
    return ourInstance;
  }

  private Sounds() {
    monsterCall[0] = Gdx.audio.newSound(Gdx.files.internal("276481__xdimebagx__monster-scream-1-wet.ogg"));
    monsterCall[1] = Gdx.audio.newSound(Gdx.files.internal("276479__xdimebagx__monster-scream-2-wet.ogg"));
    monsterCall[2] = Gdx.audio.newSound(Gdx.files.internal("276485__xdimebagx__monster-scream-3-wet.ogg"));
    monsterCall[3] = Gdx.audio.newSound(Gdx.files.internal("276483__xdimebagx__monster-scream-4-wet.ogg"));

    chaseMusic[0] = Gdx.audio.newMusic(Gdx.files.internal("chase0.ogg"));
    chaseMusic[1] = Gdx.audio.newMusic(Gdx.files.internal("chase1.ogg"));
    chaseMusic[2] = Gdx.audio.newMusic(Gdx.files.internal("chase2.ogg"));
    chaseMusic[3] = Gdx.audio.newMusic(Gdx.files.internal("chase3.ogg"));
  }

  public void scream() {
    if (!sfxOn) return;
    monsterCall[ Utility.r.nextInt(nMonsterCall) ].play();
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
    currentChase = Utility.r.nextInt(nChaseStarts);
    chaseMusic[currentChase].play();
    chaseMusic[currentChase].setVolume(1f);
    scream();
    ambiance.setVolume(0f);
  }

  public void chaseVolume(float v) {
    chaseMusic[currentChase].setVolume(v);
    ambiance.setVolume(1f - v);
  }

  public void endChase() {
    chaseMusic[currentChase].stop();
  }

  public void dispose() {
    chaseMusic[currentChase].stop();
    ambiance.stop();
    ambiance.dispose();
    for (Music m : chaseMusic) m.dispose();
    for (Sound s : monsterCall) s.dispose();
  }

}
