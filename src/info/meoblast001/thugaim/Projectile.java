/*
Copyright (C) 2013 Braden Walters

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package info.meoblast001.thugaim;

import android.graphics.Point;
import android.graphics.PointF;

import info.meoblast001.thugaim.engine.Actor;
import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.engine.World;
import info.meoblast001.thugaim.R;

import java.util.LinkedList;

/**
Ammunition fired by the player and NPCs. Moves continuously in the direction its
origin was traveling when fired.
*/
public class Projectile extends Actor
{
  public final static float MAX_LENGTH = 300;

  private Actor origin;
  private PointF original_position = null;
  private static short current_projectile_id = 0;
  //Static list of all projectiles in the world.
  private static LinkedList<Projectile> cur_projectiles =
    new LinkedList<Projectile>();

  public Projectile(Engine engine, Actor origin)
  {
    super("projectile_" + (current_projectile_id++), engine,
          R.drawable.projectile);
    this.origin = origin;

    original_position = origin.getPosition();
    move(original_position.x, original_position.y);
    rotate(origin.getRotation());
  }

  @Override
  public void setWorld(World world)
  {
    super.setWorld(world);

    if (world == null)
      return;

    //Do not keep more than 50 projectiles in play at one time for performance.
    cur_projectiles.add(this);
    if (cur_projectiles.size() > 50)
    {
      Projectile to_remove = cur_projectiles.poll();
      getWorld().removeActor(to_remove.getId());
      cur_projectiles.remove(to_remove);
    }
  }

  /**
  Get the actor from which this projectile was fired.
  @return Origin Actor.
  */
  public Actor getOrigin()
  {
    return origin;
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    clearCollisions();

    //If the projectile has moved too far from its original location, delete it.
    if (distance(original_position) > MAX_LENGTH)
    {
      getWorld().removeActor(getId());
      return;
    }

    //Make multiple movements per frame. Each movement should be maximally the
    //size of a projectile. During each movement, collision detection will be
    //performed. This prevents the projectiles from "passing through" objects.
    float move_distance = millisecond_delta * 0.5f;
    Point size = getSize();
    float average_size = ((float) size.x + (float) size.y) / 2.0f;
    while (true) {
      if (move_distance >= average_size) {
        moveLocal(0.0f, average_size);
        move_distance -= average_size;
      } else {
        moveLocal(0.0f, move_distance);
        break;
      }
    }

    //Check for collisions. Cause damage to objects that take damage.
    for (Actor actor : getCollisions().toArray(new Actor[0]))
    {
      if (getWorld() == null || actor.getWorld() == null)
        break;

      //If collided with a damageable actor that did not produce the projectile,
      //reduce its health.
      if (actor instanceof IDamageable && getOrigin() != actor)
      {
        IDamageable damageable = (IDamageable) actor;
        damageable.reduceHealth();
        //Remove projectile from world.
        getWorld().removeActor(getId());
        break;
      }
    }

    draw();
  }
}
