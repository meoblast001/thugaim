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

import info.meoblast001.thugaim.Station;
import info.meoblast001.thugaim.StationGraph;
import info.meoblast001.thugaim.engine.Actor;
import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.R;

/**
Simple NPC which travels randomly through the StationGraph unless near the
player, in which case it follows the player, or at an adjacent station to the
player's station, in which case it moves to that station.
*/
public class HydrogenFighter extends NPCVehicle
{
  private static final int MAX_HEALTH = 5;

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
    if (distance(target_station) < 50.0f)
    {
      Station[] adjacent_stations = station_graph.getAdjacentStations(
        target_station);
      if (adjacent_stations != null)
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
