package timboe.hunted.world;

import com.badlogic.gdx.physics.box2d.*;
import timboe.hunted.Param;
import timboe.hunted.entity.*;
import timboe.hunted.manager.GameState;
import timboe.hunted.manager.Sounds;
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
      if (myEntity instanceof Torch && !(myEntity instanceof BigBad) && !(myEntity instanceof KeyLight)) {
        Torch t = (Torch) myEntity;
        t.doCollision(true);
      }

      if (myEntity instanceof WinMask) {
        // This means we won
        GameState.getInstance().userControl = false;
        GameState.getInstance().gameIsWon = true;
        Sprites.getInstance().exitDoor.sound = false;
        Sprites.getInstance().getBigBad().aiState = BigBad.AIState.RETURN_TO_WAYPOINT;
      }

      if (myEntity instanceof Tile) {
        Tile t = (Tile) myEntity;
        if (t.getIsWeb() && GameState.getInstance().aiCooldown <= 0) {
          GameState.getInstance().aiCooldown = Param.BIGBAD_AI_COOLDOWN;
          GameState.getInstance().webEffect = true;
          Sprites.getInstance().resetWeb();
          t.startWebEffect(t);
          Sounds.getInstance().twang();
          Sprites.getInstance().tintWeb();
        } else if (!t.getIsWeb()) {
          Sounds.getInstance().thud();
        }
      }

      if (myEntity instanceof KeyLight || myEntity instanceof Clutter) {
        Sounds.getInstance().thud();
      }

      if (myEntity instanceof Switch) {
        Switch s = (Switch) myEntity;
        if (s.switchID >= 0) {
          GameState.getInstance().switchStatus[s.switchID] = true;
        }
      }

      if (myEntity instanceof Chest) {
        Chest c = (Chest) myEntity;
        c.chestOpened = true;
      }
    }

  }
  @Override
  public void endContact(Contact contact) {
    Body playerHit = playerCollidesWith(contact);
    if (playerHit != null) {
      Object myEntity = playerHit.getUserData();

      if (myEntity instanceof Switch) {
        Switch s = (Switch) myEntity;
        if (s.switchID >= 0) {
          GameState.getInstance().switchStatus[s.switchID] = false;
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