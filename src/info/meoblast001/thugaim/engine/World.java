/*
Copyright (C) 2013 - 2014 Braden Walters

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

package info.meoblast001.thugaim.engine;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.HashMap;

/**
Represents the game world and manages its contents.
*/
public class World
{
  private Engine engine;
  private int play_size;
  private HashMap<String, Actor> actors = new HashMap<String, Actor>();
  private Actor actor_focus = null;

  public World(Engine engine, int play_size)
  {
    this.engine = engine;
    this.play_size = play_size;
  }

  /**
  Inserts an actor into the world if it is not already in a world. Also signals
  the actor of the world change.
  @param actor Actor to be inserted.
  @return True if inserted, false if the actor is already in a world.
  */
  public boolean insertActor(Actor actor)
  {
    if (actor.getWorld() != null)
      return false;
    actor.setWorld(this);
    actors.put(actor.getId(), actor);
    return true;
  }

  /**
  Removes an actor from the world if it is already in the world. Signals the
  actor that it has been removed from the world.
  @param actor_id ID string of the actor to remove.
  */
  public void removeActor(String actor_id)
  {
    Actor removed = actors.remove(actor_id);
    if (removed != null)
      removed.setWorld(null);
  }

  /**
  Get an actor by its ID string.
  @param actor_id ID string of actor.
  @return Actor if found or null if not found.
  */
  public Actor getActor(String actor_id)
  {
    return actors.get(actor_id);
  }

  /**
  Get an array of all actors in world in no particular order.
  @return Array of actors.
  */
  public Actor[] getActors()
  {
    return actors.values().toArray(new Actor[0]);
  }

  /**
  Specify the actor which the world (and therefore the play screen) will focus
  on.
  @param actor_id The unique ID string of the actor.
  */
  public void focusOnActor(String actor_id)
  {
    actor_focus = actors.get(actor_id);
  }

  /**
  Updates all actors. If an actor is outside the relevant play area, an idle
  update occurs, which performs actual updates less frequently to improve
  performance.
  @param millisecond_delta Milliseconds elapsed since last call.
  @param rotation Rotation of the device, where positive values are clockwise
    and negative values are counter-clockwise.
  @param tapped Signifies whether the screen is being tapped.
  */
  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    Graphics graphics = engine.getGraphics();

    PointF focus_position = actor_focus.getPosition();
    graphics.focusOn((int) focus_position.x, (int) focus_position.y);

    ArrayList<String> actor_ids = new ArrayList<String>(actors.keySet());
    for (String actor_id : actor_ids)
    {
      Actor actor = actors.get(actor_id);
      if (actor == null)
        continue;

      if (actor_focus.distance(actor) < Math.max(graphics.getWidth(),
                                                 graphics.getHeight()))
        actor.update(millisecond_delta, rotation, tapped);
      else
        actor.idleUpdate(millisecond_delta, rotation, tapped);
    }
  }

  /**
  Is an actor within the play area?
  @param actor Actor to check.
  @return True if in the play area, false if not.
  */
  public boolean isInsidePlayArea(Actor actor)
  {
    PointF position = actor.getPosition();
    int halved_play_size = play_size / 2;
    return Math.abs(position.x) < halved_play_size &&
           Math.abs(position.y) < halved_play_size;
  }
}
