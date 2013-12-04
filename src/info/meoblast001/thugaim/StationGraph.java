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
import info.meoblast001.thugaim.engine.World;

import java.util.Vector;

public class StationGraph
{
  private Engine engine;
  private World world;

  private final int NUM_STATIONS = 8;
  private Station[] stations = new Station[NUM_STATIONS];
  private boolean[][] edges = new boolean[NUM_STATIONS][NUM_STATIONS];

  private final int UPDATE_AFTER_FRAMES = 5;
  private int frames_since_update = UPDATE_AFTER_FRAMES;

  public StationGraph(Engine engine, World world)
  {
    this.engine = engine;
    this.world = world;

    final float play_size = ThugaimRuntime.PLAY_SIZE;
    for (int i = 0; i < stations.length; ++i)
    {
      stations[i] = new Station(engine,
        (float) Math.random() * play_size - (play_size / 2),
        (float) Math.random() * play_size - (play_size / 2));
      world.insertActor(stations[i]);
    }

    for (int i = 0; i < edges.length; ++i)
      for (int j = 0; j < edges[i].length; ++j)
        edges[i][j] = i == j ? false : Math.random() > 0.2;
  }

  public Station[] getStations()
  {
    return stations;
  }

  public void update()
  {
    if (frames_since_update++ < UPDATE_AFTER_FRAMES)
      return;
    frames_since_update = 0;

    Actor[] actors = world.getActors();
    for (Actor actor: actors)
    {
      if (!(actor instanceof Vehicle))
        continue;
      Vehicle vehicle = (Vehicle) actor;

      Station closest_station = null;
      float closest_station_distance = Float.MAX_VALUE;
      for (Station station : stations)
      {
        float new_distance = vehicle.distance(station);
        if (new_distance < closest_station_distance)
        {
          closest_station = station;
          closest_station_distance = new_distance;
        }
      }

      vehicle.setClosestStation(closest_station);
    }
  }
}
