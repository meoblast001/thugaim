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
  public HydrogenFighter(Engine engine, float x, float y, float rotation,
                         StationGraph station_graph)
  {
    super(engine, R.drawable.hydrogen, x, y, rotation, station_graph);
    setSpeed(0.8f);
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    if (getWorld() == null || getClosestStation() == null)
      return;

    Actor player = getWorld().getActor("player");
    Actor station = getWorld().getActor("station_0");

    if (distance(player) < 225.0f)
      pursue(player.getPosition(), player.getRotation(), millisecond_delta);
    else
      seek(getClosestStation().getPosition(), millisecond_delta);

    super.update(millisecond_delta, rotation, tapped);
  }
}
