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

import info.meoblast001.thugaim.engine.Actor;
import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.R;

/**
Station actor. Primary goal of the player is to destroy these actors. They are
protected by NPCs and belong to a graph of other stations.
*/
public class Station extends Actor implements IDamageable
{
  private static final int MAX_HEALTH = 15;

  private static int cur_station_id = 0;

  private StationGraph station_graph;
  private int health = MAX_HEALTH;

  public Station(Engine engine, StationGraph station_graph, float x, float y)
  {
    super("station_" + (cur_station_id++), engine, R.drawable.station);
    this.station_graph = station_graph;
    move(x, y);
  }

  /**
  Reduces health by 1. If health reaches zero, actor is removed from the world.
  */
  @Override
  public void reduceHealth()
  {
    --health;
    if (health == 0)
      station_graph.remove(this);
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    //No movement, therefore collision detection is not performed automatically.
    clearCollisions();
    detectCollisions();
    draw();
  }
}
