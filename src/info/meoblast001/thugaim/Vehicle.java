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

public abstract class Vehicle
{
  private Engine engine = null;
  private float x, y;
  private float rotation;
  private Bitmap bitmap = null;
  private float speed = 1.0f;

  public Vehicle(Engine engine, int bitmap_resource, float x, float y,
                 float rotation)
  {
    this.engine = engine;
    this.x = x;
    this.y = y;
    this.rotation = rotation;
    bitmap = BitmapFactory.decodeResource(
      engine.getGraphics().getContext().getResources(), bitmap_resource);
  }

  public void setSpeed(float speed)
  {
    this.speed = speed;
  }

  public PointF getPosition()
  {
    return new PointF(x, y);
  }

  public void update(long millisecond_delta, float rotation_delta)
  {
    rotation += rotation_delta * 0.015f * (float) millisecond_delta;

    x += Math.sin(rotation * (Math.PI / 180)) * speed * 0.12f *
         (float) millisecond_delta;
    y += -Math.cos(rotation * (Math.PI / 180)) * speed * 0.12f *
         (float) millisecond_delta;

    engine.getGraphics().draw(bitmap, Math.round(x), Math.round(y), rotation);
  }
}
