package timboe.hunted.entity;

import timboe.hunted.render.Textures;

/**
 * Created by Tim on 31/12/2016.
 */
public class BigBad extends EntityBase {
  public BigBad() {
    super(0,0);
    texture = Textures.getInstance().dummyBigBad;
    angle = 0;
    setPlayerBody(0.5f, 0.25f);
  }

}
