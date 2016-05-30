/*
Copyright (C) 2013 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim.npc;

import android.graphics.Point;

import info.meoblast001.thugaim.Projectile;
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
  private static final float FIRE_ANGLE_RADIANS = (float) (30.0f *
                                                           Math.PI / 180.0f);
  private static final float FIRING_RANGE = 200.0f;

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
    Point station_size = target_station.getSize();
    float station_avg_size = (station_size.x + station_size.y) / 2;
    if (distance(target_station) < station_avg_size * 1.75f)
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

    //Is this fighter facing the player and at the appropriate distance?
    boolean will_fire = willFireAt(player, FIRE_ANGLE_RADIANS, FIRING_RANGE);

    //If the NPC plans on firing but another friendly actor may be at risk, stop
    //firing.
    if (will_fire)
    {
      for (Actor actor : getWorld().getActors())
      {
        //If another NPC is between this NPC and the player character, don't
        //fire.
        if (actor instanceof NPCVehicle && actor != this)
        {
          float rotation_to_actor = crossProduct(getRotationUnitVector(),
            getUnitVectorToTarget(getPosition(), actor.getPosition()));
          if (rotation_to_actor > -FIRE_ANGLE_RADIANS &&
              rotation_to_actor < FIRE_ANGLE_RADIANS &&
              distance(actor) < distance(player))
          {
            will_fire = false;
            break;
          }
        }
        //If a station is close within firing range and in the current
        //direction, don't fire.
        else if (actor instanceof Station)
        {
          float rotation_to_actor = crossProduct(getRotationUnitVector(),
            getUnitVectorToTarget(getPosition(), actor.getPosition()));
          if (rotation_to_actor > -FIRE_ANGLE_RADIANS &&
              rotation_to_actor < FIRE_ANGLE_RADIANS &&
              distance(actor) < Projectile.MAX_LENGTH  * 0.8f)
          {
            will_fire = false;
            break;
          }
        }
      }
    }

    //If an NPC player gets too close, avoid it.
    NPCVehicle avoiding_npc = getAvoidingNPC();
    if (avoiding_npc != null)
    {
      evade(avoiding_npc.getPosition(), avoiding_npc.getRotation(),
            millisecond_delta);
      will_fire = false;
    }

    if (will_fire)
      fire();

    //If the player character is close, go directly toward the player, else go
    //toward the target station.
    if (distance(player) < 225.0f)
      pursue(player.getPosition(), player.getRotation(), millisecond_delta);
    else
      seek(target_station.getPosition(), millisecond_delta);

    super.update(millisecond_delta, rotation, tapped);
  }
}
