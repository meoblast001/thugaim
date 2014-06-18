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
  private Actor origin;
  private static short current_projectile_id = 0;
  //Static list of all projectiles in the world.
  private static LinkedList<Projectile> cur_projectiles =
    new LinkedList<Projectile>();

  public Projectile(Engine engine, Actor origin)
  {
    super("projectile_" + (current_projectile_id++), engine,
          R.drawable.projectile);
    this.origin = origin;

    PointF original_position = origin.getPosition();
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

  @Override
  protected boolean isCollisionDetectionOn()
  {
    return false;
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
    moveLocal(0.0f, millisecond_delta * 0.5f);
    draw();
  }
}
