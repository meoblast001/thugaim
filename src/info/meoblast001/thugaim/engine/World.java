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

package info.meoblast001.thugaim.engine;

import java.util.HashMap;

public class World
{
  private Engine engine;
  private HashMap<String, Actor> actors = new HashMap<String, Actor>();
  private String actor_focus_id = null;

  public World(Engine engine)
  {
    this.engine = engine;
  }

  public boolean insertActor(Actor actor)
  {
    if (actor.getWorld() != null)
      return false;
    actor.setWorld(this);
    actors.put(actor.getId(), actor);
    return true;
  }

  public void insertActor(String actor_id)
  {
    actors.remove(actor_id);
  }

  public Actor getActor(String actor_id)
  {
    return actors.get(actor_id);
  }

  public void focusOnActor(String actor_id)
  {
    actor_focus_id = actor_id;
  }

  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    for (String actor_id : actors.keySet())
    {
      Actor actor = actors.get(actor_id);
      actor.update(millisecond_delta, rotation, tapped);
    }
  }
}
