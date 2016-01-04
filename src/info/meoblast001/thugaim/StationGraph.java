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

import info.meoblast001.thugaim.engine.Actor;
import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.engine.World;

import java.util.Queue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

/**
Invisible entity linking stations as a directed graph. Stations are placed at
random locations. Edges between stations are built randomly.
*/
public class StationGraph
{
  /**
  Extra data needed for each station in StationGraph.approxShortestPath.
  */
  private class ApproxSearchExtra
  {
    public float estimated_cost = Float.POSITIVE_INFINITY;
    public float cost = Float.POSITIVE_INFINITY;
    public Station came_from = null;
  }

  private final int UPDATE_AFTER_FRAMES = 5;
  //Amount of free space around stations at initialisation.
  private final float FREE_SURROUNDING_SPACE_AT_INIT = 100.0f;
  //Maximum amount of normal station placement attempts before it is simply
  //placed.
  private final int MAX_PLACEMENT_ATTEMPTS = 5;

  private Engine engine;
  private World world;

  private Station[] stations;
  private boolean[][] edges;

  private int frames_since_update = UPDATE_AFTER_FRAMES;

  public StationGraph(Engine engine, World world, int num_stations,
                      int play_size)
  {
    this.engine = engine;
    this.world = world;

    stations = new Station[num_stations];
    edges = new boolean[num_stations][num_stations];

    //Create a station object so that the size of one is known.
    Station unused_station = new Station(engine, this, 0, 0);
    Point station_size = unused_station.getSize();
    float avg_station_size = (station_size.x + station_size.y) / 2.0f;

    //If world is too small to place stations, fail construction.
    float init_min_distance = avg_station_size / 2.0f +
                              FREE_SURROUNDING_SPACE_AT_INIT;
    if (play_size / 2 < init_min_distance)
    {
      throw new RuntimeException("World is too small to construct a " +
        "station graph.");
    }

    //Place num_stations amount of stations at random locations in the play
    //area.
    for (int i = 0; i < stations.length; ++i)
    {
      int placement_attempts = MAX_PLACEMENT_ATTEMPTS;
      do
      {
        //Generate random X and Y values relative to the area allowed for
        //placing stations.
        float rand_x = (float) ((Math.random() - 0.5) * 2.0) *
                       (play_size / 2 - init_min_distance);
        float rand_y = (float) ((Math.random() - 0.5) * 2.0) *
                       (play_size / 2 - init_min_distance);

        //Convert these relative values to absolute values.
        if (rand_x >= 0)
          rand_x += init_min_distance;
        else
          rand_x -= init_min_distance;
        if (rand_y >= 0)
          rand_y += init_min_distance;
        else
          rand_y -= init_min_distance;

        //Create a station at this position.
        stations[i] = new Station(engine, this, rand_x, rand_y);

        //If the station overlays another actor in the world, try placing again.
        //If too many attempts occur, stop attempting and simply place it.
        --placement_attempts;
      } while (world.hasActorAt(stations[i].getPosition(),
        avg_station_size / 2.0f) && placement_attempts > 0);

      //Place the station.
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

  /**
  Find an approximated shortest path between two stations. Uses A* algorithm to
  find the result.
  @param start Beginning station in path.
  @param end Station to search.
  @return Path of stations from start to end. Null if a best path could not be
    found.
  */
  public Queue<Station> approxShortestPath(Station start, Station end)
  {
    //Create a hash from station IDs to extra data needed for this algorithm.
    HashMap<String, ApproxSearchExtra> station_extras =
      new HashMap<String, ApproxSearchExtra>();
    for (Station station : getStations())
      station_extras.put(station.getId(), new ApproxSearchExtra());

    HashSet<Station> closed = new HashSet<Station>();
    HashSet<Station> open = new HashSet<Station>();
    open.add(start);

    while (open.size() > 0)
    {
      //Current is the item in the open set with the lowest estimated cost.
      Station current = null;
      ApproxSearchExtra current_extra = null;
      for (Station element : open)
      {
        if (current == null && current_extra == null)
        {
          current = element;
          current_extra = station_extras.get(element.getId());
        }
        else
        {
          ApproxSearchExtra extra = station_extras.get(element.getId());
          if (extra.estimated_cost < current_extra.estimated_cost)
          {
            current = element;
            current_extra = extra;
          }
        }
      }

      //If the current station is the end station, then we're done.
      if (current == end)
        return buildApproxShortestPathResult(end, station_extras);

      //Station is no longer in the open set and is now in the closed set
      //because it was traversed.
      open.remove(current);
      closed.add(current);

      for (Station neighbour : getAdjacentStations(current))
      {
        //Do nothing if neighbour is already in the closed set.
        if (closed.contains(neighbour))
          continue;

        ApproxSearchExtra neighbour_extra =
          station_extras.get(neighbour.getId());

        //Cost of movement to this neighbour.
        float attempt_cost = current_extra.cost + current.distance(neighbour);

        //If not in the open set, add the neighbour to the open set so that it
        //will be traversed later.
        if (!open.contains(neighbour))
          open.add(neighbour);
        //If this path is more costly than another path to this station, then
        //this path cannot be optimal.
        else if (attempt_cost >= neighbour_extra.cost)
          continue;

        //This is now the best path to this neighbour. Store this information.
        neighbour_extra.came_from = current;
        neighbour_extra.cost = attempt_cost;
        neighbour_extra.estimated_cost = attempt_cost +
                                         neighbour.distance(end);
      }
    }

    return null;
  }

  /**
  Build the result of the approximate shortest path algorithm once the shortest
  path has been found.
  @param current The goal station.
  @param current_extra The extra algorithm-specific data of the goal station.
  @return Path of stations from start to end.
  */
  private Queue<Station> buildApproxShortestPathResult(Station current,
    HashMap<String, ApproxSearchExtra> station_extras)
  {
    LinkedList<Station> path = new LinkedList<Station>();

    ApproxSearchExtra extra = null;
    do {
      extra = station_extras.get(current.getId());
      path.addFirst(current);
      current = extra.came_from;
    } while (current != null);

    return path;
  }
}
