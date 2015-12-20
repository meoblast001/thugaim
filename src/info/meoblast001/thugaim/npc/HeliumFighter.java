/*
Copyright (C) 2015 Braden Walters

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

import android.graphics.Point;

import info.meoblast001.thugaim.Station;
import info.meoblast001.thugaim.StationGraph;
import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.engine.World;
import info.meoblast001.thugaim.Vehicle;
import info.meoblast001.thugaim.R;

import java.util.Queue;

/**
Complex NPC which follows the player in the StationGraph with the following
behaviour.
1. Travels slightly faster than the player.
2. If the player is nearby, it follows the player directly.
3. Uses a graph search algorithm to always move toward the player's closest
   station.
4. Not very cautious about not colliding with or attacking a station.
*/
public class HeliumFighter extends NPCVehicle
{
  private static final int MAX_HEALTH = 5;
  private static final float FREE_SURROUNDING_SPACE_AT_INIT = 20.0f;

  private StationGraph station_graph = null;
  private Queue<Station> remaining_path_to_player = null;
  private Station player_station = null;

  public HeliumFighter(Engine engine, float x, float y, float rotation,
                       StationGraph station_graph)
  {
    super(engine, R.drawable.helium, x, y, rotation, MAX_HEALTH, station_graph);
    this.station_graph = station_graph;
    setSpeed(1.05f);
  }

  /**
  Generates all of the Helium fighters in a level at randon positions.
  @param engine The game engine.
  @param world The current world.
  @param play_size Size of play area.
  @param station_graph The current station graph.
  @param num_fighters The amount of fighters to create.
  */
  public static void generateAll(Engine engine, World world, int play_size,
                                 StationGraph station_graph, int num_fighters)
  {
    for (int i = 0; i < num_fighters; ++i)
    {
      inner_loop: while (true)
      {
        HeliumFighter fighter = new HeliumFighter(engine,
          (float) Math.random() * play_size - (play_size / 2),
          (float) Math.random() * play_size - (play_size / 2),
          (float) (Math.random() * Math.PI / 180.0), station_graph);
        Point fighter_size = fighter.getSize();
        float avg_fighter_size = (fighter_size.x + fighter_size.y) / 2.0f;
        if (!world.hasActorAt(fighter.getPosition(), avg_fighter_size / 2.0f +
                              FREE_SURROUNDING_SPACE_AT_INIT))
        {
          world.insertActor(fighter);
          break inner_loop;
        }
        else; //Continue trying.
      }
    }
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    if (getWorld() == null || getClosestStation() == null)
      return;

    Vehicle player = (Vehicle) getWorld().getActor("player");
    if (player == null)
      return;

    //If the closest station to the player changes or the path to the player has
    //not yet been determined, calculate it.
    Station player_station = player.getClosestStation();
    if (remaining_path_to_player == null ||
        player_station != this.player_station)
    {
      remaining_path_to_player = station_graph.approxShortestPath(
        getClosestStation(), player_station);
      this.player_station = player_station;
    }

    //If the fighter gets too close to another station during its path, it
    //should prioritise not colliding with this station.
    if (distance(getClosestStation()) < 125.0f)
      flee(getClosestStation().getPosition(), millisecond_delta);

    //If an NPC player gets too close, avoid it.
    NPCVehicle avoiding_npc = getAvoidingNPC();
    if (avoiding_npc != null)
    {
      evade(avoiding_npc.getPosition(), avoiding_npc.getRotation(),
            millisecond_delta);
    }

    //If there are stations to follow on the way to the player, follow them.
    if (remaining_path_to_player != null && remaining_path_to_player.size() > 0)
    {
      //If close to target station, pop the current station off the path and
      //seek the next.
      if (distance(remaining_path_to_player.peek()) < 150.0f)
        remaining_path_to_player.remove();
      //Else seek the current station.
      else
        seek(remaining_path_to_player.peek().getPosition(), millisecond_delta);
    }
    //Else if no stations left to follow or a path could not be found, directly
    //pursue the player.
    else
      pursue(player.getPosition(), player.getRotation(), millisecond_delta);

    //TODO: Fire.

    super.update(millisecond_delta, rotation, tapped);
  }
}
