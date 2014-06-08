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

import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.R;

/**
Player character which responds to player input. Only one exists per world.
*/
public class Player extends Vehicle
{
  public static final int MAX_HEALTH = 50;

  public Player(Engine engine, StationGraph station_graph)
  {
    super(engine, "player", R.drawable.player, 0.0f, 0.0f, 0.0f, MAX_HEALTH,
          station_graph);
    setSpeed(1.0f);
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    if (tapped)
      fire();

    rotate(rotation, millisecond_delta);
    super.update(millisecond_delta, rotation, tapped);
  }
}
