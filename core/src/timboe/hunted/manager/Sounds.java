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

  private Music ambiance = Gdx.audio.newMusic(Gdx.files.internal("Echoes_of_Time_v2.mp3"));
  private Music machineNoise = Gdx.audio.newMusic(Gdx.files.internal("260815__iccleste__industrial-machine-cycle.ogg"));

  private final int nMonsterCall = 4;
  private Sound monsterCall[] = new Sound[nMonsterCall];
  private final int nFootsteps = 4;
  private Sound footstepSound[] = new Sound[nFootsteps];
  private Sound unlockSound = Gdx.audio.newSound(Gdx.files.internal("336562__anthousai__keys-rustling-02.ogg"));
  private Sound treasureSound = Gdx.audio.newSound(Gdx.files.internal("202092__spookymodem__chest-opening.ogg"));
  private Sound ignitionSound = Gdx.audio.newSound(Gdx.files.internal("331621__hykenfreak__flame-ignition.ogg"));
  private Sound thudSound = Gdx.audio.newSound(Gdx.files.internal("215162__otisjames__thud.ogg"));
  private Sound doorOpenSound = Gdx.audio.newSound(Gdx.files.internal("97790__cgeffex__dungeon-gates.ogg"));

  private final int nChaseStarts = 4;
  private int currentChase = 0;
  private Music chaseMusic[] = new Music[nChaseStarts];

  private static Sounds ourInstance = new Sounds();

  public static Sounds getInstance() {
    return ourInstance;
  }

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
  }

  public void step() {
    if (!sfxOn) return;
    footstepSound[ Utility.r.nextInt(nFootsteps) ].play();
  }

  public void scream(float volume) {
    if (!sfxOn) return;
    int n = Utility.r.nextInt(nMonsterCall);
    long id =  monsterCall[ n ].play();
    monsterCall[ n ].setVolume(id, volume);

  }

  public void doorOpen() {
    if (!sfxOn) return;
    doorOpenSound.play();
  }

  public void thud() {
    if (!sfxOn) return;
    thudSound.play();
  }

  public void treasure() {
    if (!sfxOn) return;
    treasureSound.play();
  }

  public void ignite() {
    if (!sfxOn) return;
    ignitionSound.play();
  }

  public void unlock() {
    if (!sfxOn) return;
    unlockSound.play();
  }

  public void unlockStop() {
    if (!sfxOn) return;
    unlockSound.stop();
  }

  public void startAmbiance() {
    if (!musicOn) return;
    ambiance.play();
    ambiance.setLooping(true);
  }

  public void stopAmbiance() {
    ambiance.stop();
  }

  public void startChase() {
    if (!musicOn) return;
    currentChase = Utility.r.nextInt(nChaseStarts);
    chaseMusic[currentChase].play();
    chaseMusic[currentChase].setVolume(1f);
    ambiance.setVolume(0f);
  }

  public void chaseVolume(float v) {
    if (!musicOn) return;
    chaseMusic[currentChase].setVolume(v);
    ambiance.setVolume(1f - v);
  }

  public void machineNoise(float v) {
    if (!musicOn) return;
    if (v == 0 && machineNoise.isPlaying()) {
      machineNoise.stop();
      return;
    }
    if (!machineNoise.isPlaying()) {
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
    for (Music m : chaseMusic) m.dispose();
    for (Sound s : monsterCall) s.dispose();
  }

}
