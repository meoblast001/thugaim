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
import info.meoblast001.thugaim.R;

/**
NPC which always follows the player in the StationGraph. It follows the player
whenever it is close. It is also slightly faster than the player.
*/
public class HeliumFighter extends NPCVehicle
{
  private static final int MAX_HEALTH = 5;
  private static final float FREE_SURROUNDING_SPACE_AT_INIT = 20.0f;

  private StationGraph station_graph = null;
  private Station target_station = null;

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
    super.update(millisecond_delta, rotation, tapped);
  }
}
