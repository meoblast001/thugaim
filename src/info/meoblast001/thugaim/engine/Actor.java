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

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.HashSet;
import java.util.Set;

public abstract class Actor
{
  private String id;
  private Engine engine;
  private Bitmap bitmap = null;
  private World world = null;
  private float x = 0.0f, y = 0.0f;
  private float rotation = 0.0f;
  private HashSet<Actor> collisions = new HashSet<Actor>();

  private HashSet<Actor> possible_collision_actors = new HashSet<Actor>();
  private final int FRAMES_UNTIL_PCA_RECALCULATE = 20;
  private final float PCA_DISTANCE = 50;
  private int frames_since_pca_recalculate = FRAMES_UNTIL_PCA_RECALCULATE;

  private long idle_milliseconds = 0;
  private int idle_frames = (int) (Math.random() * 20);

  public Actor(String id, Engine engine, int bitmap_resource)
  {
    this.id = id;
    this.engine = engine;
    bitmap = BitmapFactory.decodeResource(
      engine.getGraphics().getContext().getResources(), bitmap_resource);
  }

  public String getId()
  {
    return id;
  }

  public World getWorld()
  {
    return world;
  }

  public void setWorld(World world)
  {
    this.world = world;
  }

  public PointF getPosition()
  {
    return new PointF(x, y);
  }

  public float getRotation()
  {
    return rotation;
  }

  public Point getSize()
  {
    return new Point(bitmap.getWidth(), bitmap.getHeight());
  }

  public void rotate(float rotation)
  {
    this.rotation += rotation;
  }

  public void move(float x, float y)
  {
    this.x += x;
    this.y += y;

    updateCollisions();
  }

  public void moveLocal(float x, float y)
  {
    this.x += Math.sin(rotation * (Math.PI / 180)) * y;
    this.y += -Math.cos(rotation * (Math.PI / 180)) * y;

    this.x += -Math.cos(rotation * (Math.PI / 180)) * x;
    this.y += Math.sin(rotation * (Math.PI / 180)) * x;

    updateCollisions();
  }

  public abstract void update(long millisecond_delta, float rotation,
                              boolean tapped);

  public void idleUpdate(long millisecond_delta, float rotation, boolean tapped)
  {
    idle_milliseconds += millisecond_delta;
    ++idle_frames;
    if (idle_frames == 20)
    {
      update(idle_milliseconds, rotation, tapped);
      idle_milliseconds = 0;
      idle_frames = 0;
    }
  }

  public void draw()
  {
    engine.getGraphics().draw(bitmap, Math.round(x), Math.round(y), rotation);
  }

  public float distance(Actor other)
  {
    PointF this_pos = getPosition();
    PointF other_pos = other.getPosition();
    return (float) Math.sqrt(Math.pow(other_pos.y - this_pos.y, 2) +
                             Math.pow(other_pos.x - this_pos.x, 2));
  }

  public Set<Actor> getCollisions()
  {
    return collisions;
  }

  private void updateCollisions()
  {
    if (world == null)
      return;

    if (frames_since_pca_recalculate >= FRAMES_UNTIL_PCA_RECALCULATE)
      recalculatePossibleCollisionActors();
    ++frames_since_pca_recalculate;

    collisions = new HashSet<Actor>();
    for (Actor actor : possible_collision_actors)
    {
      if (actor == this)
        continue;

      Point this_size = getSize();
      float this_avg_size = ((float) this_size.x + (float) this_size.y) / 2.0f;

      Point other_size = actor.getSize();
      float other_avg_size = ((float) other_size.x + (float) other_size.y) /
                             2.0f;

      float max_collide_distance = (this_avg_size + other_avg_size) / 2;

      if (distance(actor) < max_collide_distance)
        collisions.add(actor);
    }
  }

  private void recalculatePossibleCollisionActors()
  {
    possible_collision_actors = new HashSet<Actor>();

    for (Actor actor : world.getActors())
      if (distance(actor) < PCA_DISTANCE)
        possible_collision_actors.add(actor);

    frames_since_pca_recalculate = 0;
  }

  public PointF getRotationUnitVector()
  {
    float radian_rotation = (float) (getRotation() * (Math.PI / 180.0f));
    return new PointF((float) Math.sin(radian_rotation),
                      (float) -Math.cos(radian_rotation));
  }

  protected PointF getUnitVectorToTarget(PointF from, PointF to)
  {
    to = new PointF(to.x - from.x, to.y - from.y);
    float to_magn = (float) Math.sqrt(Math.pow(to.x, 2.0f) +
                                      Math.pow(to.y, 2.0f));
    if (to_magn < Float.MIN_VALUE && to_magn > -Float.MIN_VALUE)
      return new PointF(0.0f, 0.0f);
    else
      return new PointF(to.x / to_magn, to.y / to_magn);
  }

  protected float crossProduct(PointF lhs, PointF rhs)
  {
    return lhs.x * rhs.y - lhs.y * rhs.x;
  }
}
