/*
Copyright (C) 2013 - 2014 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim;

import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.R;

/**
Player character which responds to player input. Only one exists per world.
*/
public class Player extends Vehicle
{
  public static final int MAX_HEALTH = 50;

  public Player(Engine engine, StationGraph station_graph)
  {
    super(engine, "player", R.drawable.player, 0.0f, 0.0f, 0.0f, MAX_HEALTH,
          station_graph);
    setSpeed(1.0f);
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    if (getWorld() == null)
      return;

    //Destroy player if it goes outside of the play area.
    if (!getWorld().isInsidePlayArea(this))
    {
      reduceHealth(MAX_HEALTH); //Destroy player.
      return;
    }

    if (tapped)
      fire();

    rotate(rotation, millisecond_delta);
    super.update(millisecond_delta, rotation, tapped);
  }
}
