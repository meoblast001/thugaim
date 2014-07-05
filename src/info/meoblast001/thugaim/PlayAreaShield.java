/*
Copyright (C) 2014 Braden Walters

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
import info.meoblast001.thugaim.R;

import java.util.LinkedList;
import java.util.List;

/**
Indicator which moves across the edges of the world to discourage the player
from passing.
*/
public class PlayAreaShield extends Actor
{
  public enum Side
  {
    LEFT, TOP, RIGHT, BOTTOM
  }

  private static final int SHIELD_DISTANCE = 240;
  private static final float SHIELD_PART_DISTANCE = 8.0f;
  private static final int INDICATOR_DISTANCE_FROM_EDGE = 2;

  private static int current_play_area_shield_id = 0;

  private Side side = null;
  private Point starting_position = null;
  private int milliseconds_since_last_step = 0;

  public PlayAreaShield(Engine engine, int play_size, Side side,
                        int start_offset)
  {
    super("play_area_shield" + current_play_area_shield_id++, engine,
          R.drawable.play_area_shield);
    this.side = side;
    switch (side)
    {
      case LEFT:
        starting_position = new Point(
          -(play_size / 2) + INDICATOR_DISTANCE_FROM_EDGE, play_size / 2);
        setPosition(starting_position.x, starting_position.y + start_offset);
        break;
      case TOP:
        starting_position = new Point(
          -(play_size / 2), -(play_size / 2) + INDICATOR_DISTANCE_FROM_EDGE);
        setPosition(starting_position.x + start_offset, starting_position.y);
        rotate(90.0f * (float) Math.PI / 180.0f);
        break;
      case RIGHT:
        starting_position = new Point(
          play_size / 2 - INDICATOR_DISTANCE_FROM_EDGE, -(play_size / 2));
        setPosition(starting_position.x, starting_position.y - start_offset);
        break;
      case BOTTOM:
        starting_position = new Point(
          play_size / 2, play_size / 2 - INDICATOR_DISTANCE_FROM_EDGE);
        setPosition(starting_position.x - start_offset, starting_position.y);
        rotate(90.0f * (float) Math.PI / 180.0f);
        break;
    }
  }

  @Override
  protected boolean isCollisionDetectionOn()
  {
    return false;
  }

  /**
  Generates all of the indicators around the play area which are needed for a
  given play size and inserts them into the world.
  @param engine Game engine.
  @param world Current world.
  @param play_size Width of play area.
  */
  public static void generateAll(Engine engine, World world,
                                                 int play_size)
  {
    for (int i = 0; i < play_size; i += SHIELD_DISTANCE)
    {
      PlayAreaShield[] cur_shields = {
          new PlayAreaShield(engine, play_size, Side.LEFT, i),
          new PlayAreaShield(engine, play_size, Side.TOP, i),
          new PlayAreaShield(engine, play_size, Side.RIGHT, i),
          new PlayAreaShield(engine, play_size, Side.BOTTOM, i),
        };
      for (PlayAreaShield cur_shield : cur_shields)
        world.insertActor(cur_shield);
    }
  }

  @Override
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    milliseconds_since_last_step += millisecond_delta;
    if (milliseconds_since_last_step / 200 > 0)
    {
      int steps = milliseconds_since_last_step / 200;
      milliseconds_since_last_step %= 200;

      switch (side)
      {
        case LEFT:
          move(0.0f, -SHIELD_PART_DISTANCE * steps);
          break;
        case TOP:
          move(SHIELD_PART_DISTANCE * steps, 0.0f);
          break;
        case RIGHT:
          move(0.0f, SHIELD_PART_DISTANCE * steps);
          break;
        case BOTTOM:
          move(-SHIELD_PART_DISTANCE * steps, 0.0f);
          break;
      }
    }

    if (!getWorld().isInsidePlayArea(this, getSize().y))
      setPosition(starting_position.x, starting_position.y);
    draw();
  }
}
