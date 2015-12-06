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

import android.graphics.Point;

import info.meoblast001.thugaim.Station;
import info.meoblast001.thugaim.StationGraph;
import info.meoblast001.thugaim.engine.Actor;
import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.engine.World;
import info.meoblast001.thugaim.R;

/**
Simple NPC which travels randomly through the StationGraph with the following
behaviour.
1. Travels slightly slower than the player.
2. If the player is nearby, it follows the player directly.
3. Very cautious not to collide with or attack a station.
*/
public class HydrogenFighter extends NPCVehicle
{
  private static final int MAX_HEALTH = 5;
  private static final float FREE_SURROUNDING_SPACE_AT_INIT = 20.0f;

  private StationGraph station_graph = null;
  private Station target_station = null;

  public HydrogenFighter(Engine engine, float x, float y, float rotation,
                         StationGraph station_graph)
  {
    super(engine, R.drawable.hydrogen, x, y, rotation, MAX_HEALTH,
          station_graph);
    this.station_graph = station_graph;
    setSpeed(0.8f);
  }

  /**
  Generates all of the Hydrogen fighters in a level at randon positions.
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
        HydrogenFighter fighter = new HydrogenFighter(engine,
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

    Actor player = getWorld().getActor("player");
    if (player == null)
      return;

    //Set initial target station.
    if (target_station == null)
      target_station = getClosestStation();

    //If close to target station, change target to a random adjacent station.
    if (distance(target_station) < 150.0f)
    {
      Station[] adjacent_stations = station_graph.getAdjacentStations(
        target_station);
      if (adjacent_stations != null && adjacent_stations.length > 0)
        target_station = adjacent_stations[(int) Math.floor(Math.random() *
                                           adjacent_stations.length)];
      else
        //Target station removed from graph.
        target_station = station_graph.getClosestStation(this);
    }

    boolean will_fire = false;

    //The amount the fighter would need to rotate to face the player.
    float rotation_to_player = crossProduct(getRotationUnitVector(),
      getUnitVectorToTarget(getPosition(), player.getPosition()));
    final float FIRE_ANGLE_RADIANS = (float) (30.0f * Math.PI / 180.0f);
    //If target is within firing angle and is near, fire.
    if (rotation_to_player > -FIRE_ANGLE_RADIANS &&
        rotation_to_player < FIRE_ANGLE_RADIANS &&
        distance(player) < 200.0f)
      will_fire = true;

    //If another NPC is within firing angle and is nearer than the player,
    //cancel fire.
    //TODO: Make this more efficient. Only iterate over a subset of nearby
    //  actors.
    if (will_fire)
    {
      for (Actor npc : getWorld().getActors())
      {
        if (!(npc instanceof NPCVehicle) || npc == this)
          continue;

        float rotation_to_npc = crossProduct(getRotationUnitVector(),
          getUnitVectorToTarget(getPosition(), npc.getPosition()));
        if (rotation_to_npc > -FIRE_ANGLE_RADIANS &&
            rotation_to_npc < FIRE_ANGLE_RADIANS &&
            distance(npc) < distance(player))
          will_fire = false;
      }
    }

    if (will_fire)
      fire();

    if (distance(player) < 225.0f)
      pursue(player.getPosition(), player.getRotation(), millisecond_delta);
    else
      seek(target_station.getPosition(), millisecond_delta);

    super.update(millisecond_delta, rotation, tapped);
  }
}
