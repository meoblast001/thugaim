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

import info.meoblast001.thugaim.engine.Actor;
import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.R;

public class HydrogenFighter extends NPCVehicle
{
  public HydrogenFighter(Engine engine, float x, float y, float rotation)
  {
    super(engine, R.drawable.hydrogen, x, y, rotation);
    setSpeed(0.8f);
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    if (getWorld() == null)
      return;

    Actor player = getWorld().getActor("player");
    Actor station = getWorld().getActor("station_0");

    if (distance(station) < 175.0f)
      flee(station.getPosition(), millisecond_delta);
    else if (distance(player) < 400.0f)
      pursue(player.getPosition(), player.getRotation(), millisecond_delta);
    else
      seek(station.getPosition(), millisecond_delta);

    super.update(millisecond_delta, rotation, tapped);
  }
}
