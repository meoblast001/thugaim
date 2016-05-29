/*
Copyright (C) 2014 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
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

  private static final int SHIELD_DISTANCE = 80;
  private static final float SHIELD_PART_DISTANCE = 8.0f;
  private static final int MIN_MILLIS_FOR_STEP = 200;
  private static final int INDICATOR_DISTANCE_FROM_EDGE = 2;

  private static int current_play_area_shield_id = 0;

  private Side side = null;
  private int play_size;
  private int offset; //Movement from starting position.
  private int milliseconds_since_last_step = MIN_MILLIS_FOR_STEP;

  public PlayAreaShield(Engine engine, int play_size, Side side,
                        int start_offset)
  {
    super("play_area_shield" + current_play_area_shield_id++, engine,
          R.drawable.play_area_shield);
    this.play_size = play_size;
    this.side = side;
    this.offset = start_offset;

    if (side == Side.LEFT || side == Side.RIGHT)
      rotate((float) (Math.PI / 2.0));
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
    if (milliseconds_since_last_step / MIN_MILLIS_FOR_STEP > 0)
    {
      int steps = milliseconds_since_last_step / MIN_MILLIS_FOR_STEP;
      milliseconds_since_last_step %= MIN_MILLIS_FOR_STEP;
      offset += SHIELD_PART_DISTANCE * steps;
      offset %= play_size;

      switch (side)
      {
        case LEFT:
          setPosition(-(play_size / 2) + INDICATOR_DISTANCE_FROM_EDGE,
                      (play_size / 2) - offset);
          break;
        case TOP:
          setPosition(-(play_size / 2) + offset,
                      -(play_size / 2) + INDICATOR_DISTANCE_FROM_EDGE);
          break;
        case RIGHT:
          setPosition(play_size / 2 - INDICATOR_DISTANCE_FROM_EDGE,
                      -(play_size / 2) + offset);
          break;
        case BOTTOM:
          setPosition((play_size / 2) - offset,
                      play_size / 2 - INDICATOR_DISTANCE_FROM_EDGE);
          break;
      }
    }

    draw();
  }
}
