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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.concurrent.LinkedBlockingQueue;

public class Graphics extends SurfaceView implements SurfaceHolder.Callback
{
  private class BitmapRenderOperation
  {
    public Bitmap bitmap;
    public int x, y;
    public float angle;
  }

  private Context context = null;
  private int focus_x = 0, focus_y = 0;
  private float focus_angle = 0.0f;
  private LinkedBlockingQueue<BitmapRenderOperation> render_operations =
    new LinkedBlockingQueue<BitmapRenderOperation>();

  public Graphics(Context context, AttributeSet attr)
  {
    super(context, attr);
    this.context = context;

    SurfaceHolder holder = getHolder();
    holder.addCallback(this);
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width,
                             int height)
  {
  }

  public void surfaceCreated(SurfaceHolder holder)
  {
  }

  public void surfaceDestroyed(SurfaceHolder holder)
  {
  }

  public void draw(Bitmap bitmap, int x, int y, float angle)
  {
    BitmapRenderOperation operation = new BitmapRenderOperation();
    operation.bitmap = bitmap;
    operation.x = x;
    operation.y = y;
    operation.angle = angle;
    render_operations.add(operation);
  }

  public void focusOn(int x, int y, float angle)
  {
    focus_x = x;
    focus_y = y;
    focus_angle = angle;
  }

  public void finishDraw()
  {
    SurfaceHolder holder = getHolder();

    if (!holder.getSurface().isValid())
      return;

    Canvas canvas = holder.lockCanvas();

    canvas.translate((float) -focus_x, (float) -focus_y);
    canvas.rotate(-focus_angle);
    canvas.save();
    while (render_operations.size() > 0)
    {
      BitmapRenderOperation operation = render_operations.poll();

      canvas.save();
      canvas.translate((float) operation.x, (float) operation.y);
      canvas.rotate(operation.angle);
      canvas.drawBitmap(operation.bitmap, 0, 0, null);
      canvas.restore();
    }
    canvas.restore();

    holder.unlockCanvasAndPost(canvas);
  }
}
