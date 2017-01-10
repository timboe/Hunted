package timboe.hunted.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import timboe.hunted.Param;
import timboe.hunted.entity.BigBad;
import timboe.hunted.entity.Tile;
import timboe.hunted.entity.Torch;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sprites;

/**
 * Created by Tim on 02/01/2017.
 */
public class CollisionHandle implements ContactListener {

  private Body playerCollidesWith(Contact contact) {
    if(contact.getFixtureA().getBody() == Sprites.getInstance().getPlayer().getBody()) {
      return contact.getFixtureB().getBody();
    } else if(contact.getFixtureB().getBody() == Sprites.getInstance().getPlayer().getBody()) {
      return contact.getFixtureA().getBody();
    } else {
      return null;
    }
  }

  @Override
  public void beginContact(Contact contact) {
    Body playerHit = playerCollidesWith(contact);
    if (playerHit != null) {
      Object myEntity = playerHit.getUserData();

      // Colision between player and off-torch
      if (myEntity instanceof Torch && !(myEntity instanceof BigBad)) {
        Torch t = (Torch) myEntity;
        t.doCollision();
      }

      if (myEntity instanceof Tile) {
        Tile t = (Tile) myEntity;
        if (t.switchID >= 0) {
          GameState.getInstance().switchStatus[t.switchID] = true;
        } else if (t.getIsWeb() && GameState.getInstance().aiCooldown == 0) {
          GameState.getInstance().aiCooldown = Param.BIGBAD_AI_COOLDOWN;
          GameState.getInstance().aiDestination = t;
          Sprites.getInstance().resetWeb();
          GameState.getInstance().webEffect = true;
          Sprites.getInstance().getBigBad().aiState = BigBad.AIState.DOASTAR;
          t.startWebEffect();
          Sprites.getInstance().tintWeb();
        }
      }
    }

  }
  @Override
  public void endContact(Contact contact) {
    Body playerHit = playerCollidesWith(contact);
    if (playerHit != null) {
      Object myEntity = playerHit.getUserData();

      if (myEntity instanceof Tile) {
        Tile t = (Tile) myEntity;
        if (t.switchID >= 0) {
          GameState.getInstance().switchStatus[t.switchID] = false;
        }
      }
    }
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
  }
}