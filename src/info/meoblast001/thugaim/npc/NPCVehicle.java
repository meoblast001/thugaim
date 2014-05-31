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

import info.meoblast001.thugaim.StationGraph;
import info.meoblast001.thugaim.Vehicle;
import info.meoblast001.thugaim.engine.Engine;

/**
Base vehicle class for all NPCs. Includes common functionality all NPCs have and
provides helper methods.
*/
public abstract class NPCVehicle extends Vehicle
{
  private static short current_fighter_id = 0;

  public NPCVehicle(Engine engine, int bitmap_resource, float x, float y,
                    float rotation, int health, StationGraph station_graph)
  {
    super(engine, "npc_" + current_fighter_id++, bitmap_resource, x, y,
          rotation, health, station_graph);
  }

  @Override
  protected void rotate(float rotation, long millisecond_delta)
  {
    if (rotation >= 4.0f)
      rotation = 4.0f;
    super.rotate(rotation, millisecond_delta);
  }

  /**
  Rotate to reach a particular target.
  @param target The location of the target.
  @param millisecond_delta Milliseconds elapsed since last frame.
  */
  protected void seek(PointF target, long millisecond_delta)
  {
    rotate(
      crossProduct(getRotationUnitVector(),
                   getUnitVectorToTarget(getPosition(), target)) * 8.0f,
      millisecond_delta);
  }

  /**
  Rotate to reach the location to which a particular target is going.
  @param target The location of the target (current location).
  @param rotation The rotation of the target.
  @param millisecond_delta Milliseconds elapsed since last frame.
  */
  protected void pursue(PointF target, float rotation, long millisecond_delta)
  {
    float radian_rotation = (float) (rotation * (Math.PI / 180.0f));
    target = new PointF(target.x + (float) Math.sin(rotation),
                        target.y + (float) -Math.cos(rotation));
    seek(target, millisecond_delta);
  }

  /**
  Rotate to go in the opposite direction of the target. Flees from the target.
  @param target The location of the target.
  @param millisecond_delta Milliseconds elapsed since last frame.
  */
  protected void flee(PointF target, long millisecond_delta)
  {
    rotate(
      crossProduct(getRotationUnitVector(),
                   getUnitVectorToTarget(target, getPosition())) * 8.0f,
      millisecond_delta);
  }

  /**
  Rotate in the opposite direction of that which would reach the location to
  which the target is going.
  @param target The location of the target (current location).
  @param rotation The rotation of the target.
  @param millisecond_delta Milliseconds elapsed since last frame.
  */
  protected void evade(PointF target, float rotation, long millisecond_delta)
  {
    float radian_rotation = (float) (rotation * (Math.PI / 180.0f));
    target = new PointF(target.x + (float) Math.sin(rotation),
                        target.y + (float) -Math.cos(rotation));
    flee(target, millisecond_delta);
  }
}
