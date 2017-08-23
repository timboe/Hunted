package timboe.hunted.manager;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import timboe.hunted.HuntedGame;
import timboe.hunted.Utility;

/**
 * Created by Tim on 15/01/2017.
 */
public class Sounds {

  private boolean soundsOn = true;

  private Music ambiance = Gdx.audio.newMusic(Gdx.files.internal("Echoes_of_Time_v2.mp3"));
  private Music machineNoise = Gdx.audio.newMusic(Gdx.files.internal("260815__iccleste__industrial-machine-cycle.ogg"));
  private Music died = Gdx.audio.newMusic(Gdx.files.internal("Inner_Sanctum.ogg"));

  private final int nMonsterCall = 4;
  private Sound monsterCall[] = new Sound[nMonsterCall];
  private final int nFootsteps = 4;
  private Sound footstepSound[] = new Sound[nFootsteps];
  private final int nTwang = 4;
  private Sound twangSound[] = new Sound[nTwang];
  private final int nIgnite = 3;
  private Sound ignitionSound[] = new Sound[nIgnite];
  private Sound unlockSound = Gdx.audio.newSound(Gdx.files.internal("336562__anthousai__keys-rustling-02.mp3"));
  private Sound treasureSound = Gdx.audio.newSound(Gdx.files.internal("202092__spookymodem__chest-opening.ogg"));
  private Sound thudSound = Gdx.audio.newSound(Gdx.files.internal("215162__otisjames__thud.ogg"));
  private Sound doorOpenSound = Gdx.audio.newSound(Gdx.files.internal("97790__cgeffex__dungeon-gates.ogg"));

  private final int nChaseStarts = 4;
  private int currentChase = 0;
  private Music chaseMusic[] = new Music[nChaseStarts];

  private static Sounds ourInstance;
  public static Sounds getInstance() {
    return ourInstance;
  }
  public static void create() { ourInstance = new Sounds(); }

  private Sounds() {
    monsterCall[0] = Gdx.audio.newSound(Gdx.files.internal("276481__xdimebagx__monster-scream-1-wet.ogg"));
    monsterCall[1] = Gdx.audio.newSound(Gdx.files.internal("276479__xdimebagx__monster-scream-2-wet.ogg"));
    monsterCall[2] = Gdx.audio.newSound(Gdx.files.internal("276485__xdimebagx__monster-scream-3-wet.ogg"));
    monsterCall[3] = Gdx.audio.newSound(Gdx.files.internal("276483__xdimebagx__monster-scream-4-wet.ogg"));

    footstepSound[0] = Gdx.audio.newSound(Gdx.files.internal("197778__samulis__footstep-on-stone-1.ogg"));
    footstepSound[1] = Gdx.audio.newSound(Gdx.files.internal("197779__samulis__footstep-on-stone-2.ogg"));
    footstepSound[2] = Gdx.audio.newSound(Gdx.files.internal("197780__samulis__footstep-on-stone-3.ogg"));
    footstepSound[3] = Gdx.audio.newSound(Gdx.files.internal("197781__samulis__footstep-on-stone-4.ogg"));

    chaseMusic[0] = Gdx.audio.newMusic(Gdx.files.internal("chase0.ogg"));
    chaseMusic[1] = Gdx.audio.newMusic(Gdx.files.internal("chase1.ogg"));
    chaseMusic[2] = Gdx.audio.newMusic(Gdx.files.internal("chase2.ogg"));
    chaseMusic[3] = Gdx.audio.newMusic(Gdx.files.internal("chase3.ogg"));

    twangSound[0] = Gdx.audio.newSound(Gdx.files.internal("twang0.ogg"));
    twangSound[1] = Gdx.audio.newSound(Gdx.files.internal("twang1.ogg"));
    twangSound[2] = Gdx.audio.newSound(Gdx.files.internal("twang2.ogg"));
    twangSound[3] = Gdx.audio.newSound(Gdx.files.internal("twang3.ogg"));

    ignitionSound[0] = Gdx.audio.newSound(Gdx.files.internal("331621__hykenfreak__flame-ignition0.ogg"));
    ignitionSound[1] = Gdx.audio.newSound(Gdx.files.internal("331621__hykenfreak__flame-ignition1.ogg"));
    ignitionSound[2] = Gdx.audio.newSound(Gdx.files.internal("331621__hykenfreak__flame-ignition2.ogg"));

    if (!HuntedGame.sounds) {
      soundsOn = false;
    }
  }

  public boolean getSoundsOn() { return soundsOn; }

  public void toggleSounds() {
    soundsOn = !soundsOn;
    if (soundsOn) startAmbiance();
    else {
      stopAmbiance();
      stopDied();
    }
  }

  public void step() {
    if (!soundsOn) return;
    footstepSound[ Utility.r.nextInt(nFootsteps) ].play();
  }

  public void startDied() {
    if (!soundsOn) {
      stopAmbiance();
      died.stop();
      return;
    }
    stopAmbiance();
    chaseVolume(0);
    died.play();
    died.setLooping(true);
  }

  public void scream(float volume) {
    if (!soundsOn) return;
    int n = Utility.r.nextInt(nMonsterCall);
    long id =  monsterCall[ n ].play();
    monsterCall[ n ].setVolume(id, volume);
  }

  public void doorOpen() {
    if (!soundsOn) return;
    doorOpenSound.play();
  }

  public void thud() {
    if (!soundsOn) return;
    thudSound.play();
  }

  public void twang() {
    if (!soundsOn) return;
    twangSound[Utility.r.nextInt(nTwang)].play();
  }

  public void treasure() {
    if (!soundsOn) return;
    treasureSound.play();
  }

  public void ignite() {
    if (!soundsOn) return;
    ignitionSound[Utility.r.nextInt(nIgnite)].play();
  }

  public void unlock() {
    if (!soundsOn) return;
    unlockSound.play();
  }

  public void unlockStop() {
    if (!soundsOn) return;
    unlockSound.stop();
  }

  public void startAmbiance() {
    if (!soundsOn) return;
    died.stop();
    ambiance.play();
    ambiance.setLooping(true);
  }

  public void stopAmbiance() {
    ambiance.stop();
  }

  public void stopDied() {
    died.stop();
  }

  public void startChaseMusic() {
    if (!soundsOn) return;
    currentChase = Utility.r.nextInt(nChaseStarts);
    chaseMusic[currentChase].play();
    chaseMusic[currentChase].setVolume(1f);
    ambiance.setVolume(0f);
  }

  public void chaseVolume(float v) {
    if (!soundsOn) return;
    chaseMusic[currentChase].setVolume(v);
    if (Math.abs(v) < 1e-6) chaseMusic[currentChase].stop();
    ambiance.setVolume(1f - v);
  }

  public void machineNoise(float v) {
    if (!soundsOn) return;
    if (v == 0 && machineNoise.isPlaying()) {
      machineNoise.stop();
      return;
    }
    else if (v == 0) return;
    else if (!machineNoise.isPlaying()) {
      machineNoise.play();
      machineNoise.setLooping(true);
    }
    machineNoise.setVolume(v);
  }

  public void endChase() {
    chaseMusic[currentChase].stop();
  }

  public void dispose() {
    chaseMusic[currentChase].stop();
    ambiance.stop();
    ambiance.dispose();
    unlockSound.dispose();
    died.stop();
    died.dispose();
    for (Music m : chaseMusic) m.dispose();
    for (Sound s : monsterCall) s.dispose();
    for (Sound s : twangSound) s.dispose();
    for (Sound s : footstepSound) s.dispose();
    for (Sound s : ignitionSound) s.dispose();
    ourInstance = null;
  }

}
