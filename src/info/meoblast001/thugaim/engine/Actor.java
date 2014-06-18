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

/**
Object in the game world. Abstract and must be extended to build classes of game
objects to be instantiated.
*/
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

  /**
  Get position of actor in world.
  @return X and Y position as PointF.
  */
  public PointF getPosition()
  {
    return new PointF(x, y);
  }

  /**
  Get rotation, where positive is clockwise and negative is counter-clockwise.
  @return Rotation in radians as float.
  */
  public float getRotation()
  {
    return rotation;
  }

  /**
  Get width and height.
  @return Point where X is width and Y is height.
  */
  public Point getSize()
  {
    return new Point(bitmap.getWidth(), bitmap.getHeight());
  }

  /**
  Rotates.
  @param rotation Radians of rotation, where positive is clockwise and negative
    is counter-clockwise.
  */
  public void rotate(float rotation)
  {
    this.rotation += rotation;
  }

  /**
  Translates in the world coordinate space.
  @param x X-axis translation.
  @param y Y-axis translation.
  */
  public void move(float x, float y)
  {
    this.x += x;
    this.y += y;

    if (isCollisionDetectionOn())
      updateCollisions();
  }

  /**
  Translates in the local coordinate space of the actor.
  @param x X-axis translation.
  @param y Y-axis translation.
  */
  public void moveLocal(float x, float y)
  {
    this.x += Math.sin(rotation) * y;
    this.y += -Math.cos(rotation) * y;

    this.x += -Math.cos(rotation) * x;
    this.y += Math.sin(rotation) * x;

    if (isCollisionDetectionOn())
      updateCollisions();
  }

  /**
  Implements logic executed per frame for each class of actors.
  @param millisecond_delta Amount of milliseconds that elapsed between the last
    call to this method and the current call.
  @param rotation The current rotation of the device. NOT necessarily the amount
    the actor should be rotated.
  @param tapped True if screen tapped, false if not.
  */
  public abstract void update(long millisecond_delta, float rotation,
                              boolean tapped);

  /**
  To increase performance, called instead of update() for instances which are
  outside of the relevant play area. The actual update() call occurs less
  frequently but with a millisecond_delta accumulating all missed frames.
  Parameters match those of {@link #update(long, float, boolean) update()}.
  */
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

  /**
  Draws the actor the Graphics instance.
  */
  public void draw()
  {
    engine.getGraphics().draw(bitmap, Math.round(x), Math.round(y), rotation);
  }

  /**
  Determines the distance between this actor and another actor.
  @return Distance in world units.
  */
  public float distance(Actor other)
  {
    PointF this_pos = getPosition();
    PointF other_pos = other.getPosition();
    return (float) Math.sqrt(Math.pow(other_pos.y - this_pos.y, 2) +
                             Math.pow(other_pos.x - this_pos.x, 2));
  }

  /**
  Should this actor detect collisions? By default, yes. This does not affect the
  ability of other actors to detect collisions with this actor.
  @return True if detects collisions, else false.
  */
  protected boolean isCollisionDetectionOn()
  {
    return true;
  }

  /**
  Get all actors with which this actor collided.
  @return Set of Actors.
  */
  public Set<Actor> getCollisions()
  {
    return collisions;
  }

  /**
  Update the list of actors with which this actor is colliding. Call after
  movements.
  */
  protected void updateCollisions()
  {
    if (world == null)
      return;

    //Periorically update the list of actors with which this actor may collide.
    if (frames_since_pca_recalculate >= FRAMES_UNTIL_PCA_RECALCULATE)
      recalculatePossibleCollisionActors();
    ++frames_since_pca_recalculate;

    //Iterate through actors this actor may collide with and check for
    //radial collisions.
    collisions = new HashSet<Actor>();
    for (Actor actor : possible_collision_actors)
    {
      //This actor doesn't collide with itself.
      if (actor == this)
        continue;

      //Use the average size of each actor using X and Y sizes.
      Point this_size = getSize();
      float this_avg_size = ((float) this_size.x + (float) this_size.y) / 2.0f;

      Point other_size = actor.getSize();
      float other_avg_size = ((float) other_size.x + (float) other_size.y) /
                             2.0f;

      //Sum the average size of both actors and halve the sum. If closer, a
      //radial collision occurred.
      float max_collide_distance = (this_avg_size + other_avg_size) / 2;

      if (distance(actor) < max_collide_distance)
        collisions.add(actor);
    }
  }

  /**
  To simplify the calculation of collisions, periodically do a full scan of
  which actors this actor may collide with.
  #{@link #updateCollisions() updateCollisions()} will only check those actors.
  */
  private void recalculatePossibleCollisionActors()
  {
    possible_collision_actors = new HashSet<Actor>();

    for (Actor actor : world.getActors())
      if (distance(actor) < PCA_DISTANCE)
        possible_collision_actors.add(actor);

    frames_since_pca_recalculate = 0;
  }

  /**
  Get a unit vector representing the rotation of this actor.
  @return Unit vector as PointF from (0, 0) to the point.
  */
  public PointF getRotationUnitVector()
  {
    float rotation = getRotation();
    return new PointF((float) Math.sin(rotation),
                      (float) -Math.cos(rotation));
  }

  /**
  Produced a unit vector from one point to another.
  @param from Point from which the unit vector will start.
  @param to Point to which the unit vector points.
  @return Unit vector as PointF from (0, 0) to the point.
  */
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

  /**
  Determines the cross product of two 2D points.
  */
  protected float crossProduct(PointF lhs, PointF rhs)
  {
    return lhs.x * rhs.y - lhs.y * rhs.x;
  }
}
