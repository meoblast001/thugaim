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

import java.util.LinkedList;
import java.util.Vector;

/**
Invisible entity linking stations as a directed graph. Stations are placed at
random locations. Edges between stations are built randomly.
*/
public class StationGraph
{
  private Engine engine;
  private World world;

  private Station[] stations;
  private boolean[][] edges;

  private final int UPDATE_AFTER_FRAMES = 5;
  private int frames_since_update = UPDATE_AFTER_FRAMES;

  public StationGraph(Engine engine, World world, int num_stations,
                      int play_size)
  {
    this.engine = engine;
    this.world = world;

    stations = new Station[num_stations];
    edges = new boolean[num_stations][num_stations];

    //Place num_stations amount of stations at random locations in the play
    //area.
    for (int i = 0; i < stations.length; ++i)
    {
      stations[i] = new Station(engine, this,
        (float) Math.random() * play_size - (play_size / 2),
        (float) Math.random() * play_size - (play_size / 2));
      world.insertActor(stations[i]);
    }

    //Randomly form edges between stations (80% chance of edge). Do not built
    //edge to self.
    for (int i = 0; i < edges.length; ++i)
      for (int j = 0; j < edges[i].length; ++j)
        edges[i][j] = i == j ? false : Math.random() > 0.2;
  }

  /**
  Get all stations in graph.
  @return Array of stations.
  */
  public Station[] getStations()
  {
    Vector<Station> ret_stations = new Vector<Station>();
    for (Station station : stations)
      if (station != null)
        ret_stations.add(station);
    return ret_stations.toArray(new Station[0]);
  }

  /**
  Remove a station from the graph if it exists.
  @param station Station to remove.
  */
  public void remove(Station station)
  {
    world.removeActor(station.getId());

    for (int i = 0; i < stations.length; ++i)
    {
      if (station == stations[i])
      {
        stations[i] = null;
        break;
      }
    }
  }

  /**
  Get all stations to which a particular station in the graph has an edge
  (adjacent stations).
  @param station Station from which edges extend.
  @return Array of stations to which edges from the parameter station extend.
    Null if the parameter station is not in the graph.
  */
  public Station[] getAdjacentStations(Station station)
  {
    //Locate the station's index in the list of stations.
    int station_index = -1;
    for (int i = 0; i < stations.length; ++i)
    {
      if (station == stations[i])
      {
        station_index = i;
        break;
      }
    }
    //If nothing found, return null. This station does not exist in the graph.
    if (station_index == -1)
      return null;

    //Iterate over the row in the matrix containing edges from this station.
    //Extract all stations to which edges extend.
    LinkedList<Station> adjacent_stations = new LinkedList<Station>();
    for (int i = 0; i < edges[station_index].length; ++i)
    {
      if (edges[station_index][i] && stations[i] != null)
        adjacent_stations.add(stations[i]);
    }

    return adjacent_stations.toArray(new Station[0]);
  }

  /**
  Finds the closest station to an actor.
  @param actor Actor to which to search for the closest station.
  @return Closest station. Null if no stations remaining.
  */
  public Station getClosestStation(Actor actor)
  {
    Station with_lowest_distance = null;
    float lowest_distance = Float.MAX_VALUE;

    for (Station station : stations)
    {
      if (station == null)
        continue;

      float distance = station.distance(actor);
      if (distance < lowest_distance)
      {
        with_lowest_distance = station;
        lowest_distance = distance;
      }
    }

    return with_lowest_distance;
  }

  /**
  Updates graph: finds closest station to each actor in world. Does not occur at
  every frame.
  */
  public void update()
  {
    if (frames_since_update++ < UPDATE_AFTER_FRAMES)
      return;
    frames_since_update = 0;

    //For each vehicle in the world, determine which station is the closest.
    Actor[] actors = world.getActors();
    for (Actor actor: actors)
    {
      if (!(actor instanceof Vehicle))
        continue;
      Vehicle vehicle = (Vehicle) actor;

      //Iterate over each station until the closest one is found.
      Station closest_station = null;
      float closest_station_distance = Float.MAX_VALUE;
      for (Station station : stations)
      {
        if (station == null)
          continue;

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
