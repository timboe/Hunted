package timboe.hunted.world;

import com.badlogic.gdx.physics.box2d.*;
import timboe.hunted.entity.Torch;
import timboe.hunted.render.Sprites;

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
      if (myEntity instanceof Torch) {
        Torch t = (Torch) myEntity;
        t.doCollision();
      }

    }

  }
  @Override
  public void endContact(Contact contact) {
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
  }
}