/*
Copyright (C) 2013 - 2014 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim;

import android.graphics.PointF;

import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.engine.Actor;

/**
Abstract actor class for vehicles. Each class of vehicles must define specific
behaviour.
*/
public abstract class Vehicle extends Actor implements IDamageable
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
  Gets health.
  @return Health.
  */
  public int getHealth()
  {
    return health;
  }

  /**
  Reduces health by 1 via {@link #reduceHealth(int) reduceHealth()}.
  */
  @Override
  public void reduceHealth()
  {
    reduceHealth(1);
  }

  /**
  Reduces health. If health reaches zero, actor is removed from the world.
  @param amount Amount by which to reduce health.
  */
  protected void reduceHealth(int amount)
  {
    health -= amount;
    if (health <= 0)
      getWorld().removeActor(getId());
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    clearCollisions();
    moveLocal(0, speed * 0.12f * (float) millisecond_delta);

    //If collides with a foreign projectile or station, take damage.
    for (Actor actor : getCollisions().toArray(new Actor[0]))
    {
      //Don't continue processing collisions if getWorld() returns null. If it
      //does, the vehicle has already been removed from the world.
      if (getWorld() == null)
        break;

      //Collision with a station destroys vehicle.
      if (actor instanceof Station)
        reduceHealth(Integer.MAX_VALUE);
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
