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

import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.engine.Actor;

/**
Abstract actor class for vehicles. Each class of vehicles must define specific
behaviour.
*/
public abstract class Vehicle extends Actor
{
  private Engine engine = null;
  private float speed = 1.0f;
  private int health = 1;
  private long last_fired_millis = 0;
  private StationGraph station_graph = null;
  private Station closest_station = null;

  public Vehicle(Engine engine, String id, int bitmap_resource, float x,
                 float y, float rotation, int health,
                 StationGraph station_graph)
  {
    super(id, engine, bitmap_resource);
    this.engine = engine;
    this.health = health;
    this.station_graph = station_graph;
    move(x, y);
    rotate(rotation);
  }

  /**
  Changes speed of vehicle. Default is 1.0f.
  @param speed New speed with no particular units. Higher values are faster.
    If negative, speed will be set to 0.0f.
  */
  public void setSpeed(float speed)
  {
    if (speed < 0.0f)
      speed = 0.0f;
    this.speed = speed;
  }

  /**
  Rotates the vehicle.
  @param rotation Amount to rotate in no particular units. Positive values are
    clockwise and negative values are counter-clockwise. Values clipped to
    (-8.0f, 8.0f).
  @param millisecond_delta Amount of milliseconds that elapsed between the
    previous frame and this frame.
  */
  protected void rotate(float rotation, long millisecond_delta)
  {
    if (rotation >= 8.0f)
      rotation = 8.0f;
    if (rotation <= -8.0f)
      rotation = -8.0f;
    rotate(rotation * 0.00026f * (float) millisecond_delta);
  }

  /**
  Fires a projectile from this vehicle.
  */
  protected void fire()
  {
    long cur_millis = System.currentTimeMillis();
    if (cur_millis > last_fired_millis + 100)
    {
      getWorld().insertActor(new Projectile(engine, this));
      last_fired_millis = cur_millis;
    }
  }

  /**
  Reduces health by 1. If health reaches zero, actor is removed from the world.
  */
  protected void reduceHealth()
  {
    --health;
    if (health == 0)
      getWorld().removeActor(getId());
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    moveLocal(0, speed * 0.12f * (float) millisecond_delta);

    //If collides with a foreign projectile, take damage.
    for (Actor actor : getCollisions().toArray(new Actor[0]))
    {
      if (actor instanceof Projectile && ((Projectile) actor).getOrigin() !=
          this)
      {
        getWorld().removeActor(actor.getId());
        reduceHealth(); //May be removed from world (getWorld() == null).
      }
    }

    draw();
  }

  /**
  Gets the closest station to this vehicle.
  @return Reference to Station.
  */
  public Station getClosestStation()
  {
    return closest_station;
  }

  /**
  Sets the closest station to this vehicle. Should only be called by
  StationGraph.
  @param station New closest station.
  */
  public void setClosestStation(Station station)
  {
    closest_station = station;
  }
}
