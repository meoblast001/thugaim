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

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.PointF;

import info.meoblast001.thugaim.engine.Engine;
import info.meoblast001.thugaim.engine.Actor;

public abstract class Vehicle extends Actor
{
  private Engine engine = null;
  private Bitmap bitmap = null;
  private float speed = 1.0f;

  public Vehicle(Engine engine, String id, int bitmap_resource, float x,
                 float y, float rotation)
  {
    super(id);
    this.engine = engine;
    move(x, y);
    rotate(rotation);
    bitmap = BitmapFactory.decodeResource(
      engine.getGraphics().getContext().getResources(), bitmap_resource);
  }

  public void setSpeed(float speed)
  {
    this.speed = speed;
  }

  @Override
  public void update(long millisecond_delta, float rotation_delta,
                     boolean tapped)
  {
    rotate(rotation_delta * 0.015f * (float) millisecond_delta);
    moveLocal(0, speed * 0.12f * (float) millisecond_delta);

    PointF position = getPosition();
    engine.getGraphics().draw(bitmap, Math.round(position.x),
                              Math.round(position.y), getRotation());
  }
}
