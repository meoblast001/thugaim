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

import android.graphics.PointF;

public abstract class Actor
{
  private float x = 0.0f, y = 0.0f;
  private float rotation = 0.0f;

  public PointF getPosition()
  {
    return new PointF(x, y);
  }

  public float getRotation()
  {
    return rotation;
  }

  public void rotate(float rotation)
  {
    this.rotation += rotation;
  }

  public void move(float x, float y)
  {
    this.x += x;
    this.y += y;
  }

  public void moveLocal(float x, float y)
  {
    this.x += Math.sin(rotation * (Math.PI / 180)) * y;
    this.y += -Math.cos(rotation * (Math.PI / 180)) * y;

    this.x += -Math.cos(rotation * (Math.PI / 180)) * x;
    this.y += Math.sin(rotation * (Math.PI / 180)) * x;
  }

  public abstract void update(long millisecond_delta, float rotation,
                              boolean tapped);
}
