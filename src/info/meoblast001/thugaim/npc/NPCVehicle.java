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

package info.meoblast001.thugaim.npc;

import android.graphics.PointF;

import info.meoblast001.thugaim.Vehicle;
import info.meoblast001.thugaim.engine.Engine;

public abstract class NPCVehicle extends Vehicle
{
  private static short current_fighter_id = 0;

  public NPCVehicle(Engine engine, int bitmap_resource, float x, float y,
                    float rotation)
  {
    super(engine, "npc_" + current_fighter_id++, bitmap_resource, x, y,
          rotation);
  }

  protected void seek(PointF target, long millisecond_delta)
  {
    float radian_rotation = (float) (getRotation() * (Math.PI / 180.0f));
    PointF current = new PointF((float) Math.sin(radian_rotation),
                                (float) -Math.cos(radian_rotation));

    PointF position = getPosition();
    target = new PointF(target.x - position.x, target.y - position.y);
    float target_magn = (float) Math.sqrt(Math.pow(target.x, 2.0f) +
                                          Math.pow(target.y, 2.0f));
    target = new PointF(target.x / target_magn, target.y / target_magn);

    float cross_product = current.x * target.y - current.y * target.x;

    rotate(cross_product * 8.0f, millisecond_delta);
  }

  protected void pursue(PointF target, long millisecond_delta)
  {
  }

  protected void flee(PointF target, long millisecond_delta)
  {
  }

  protected void evade(PointF target, long millisecond_delta)
  {
  }

  protected void wander(PointF target, long millisecond_delta)
  {
  }
}
