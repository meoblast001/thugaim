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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.concurrent.LinkedBlockingQueue;

/**
SurfaceView extension which handles graphics on the play screen.
*/
public class Graphics extends SurfaceView implements SurfaceHolder.Callback
{
  /**
  Most graphics operations are NOT handled until #{@link #finishDraw()
  finishDraw()} is called. Objects of this class represent one operation and are
  queued for completion when finishDraw() is called.
  */
  private class BitmapRenderOperation
  {
    public Bitmap bitmap;
    public int x, y;
    public float angle;
  }

  public enum Shape
  {
    RECTANGLE,
    OVAL
  }

  public enum PaintType
  {
    STROKE,
    FILL
  }

  private class ShapeRenderOperation
  {
    public Shape shape;
    public Paint colour;
    public int x, y;
    public int width, height;
  }

  private class TextRenderOperation
  {
    public String text;
    public Paint fill_colour, stroke_colour;
    public int x, y;
  }

  private Context context = null;
  private int focus_x = 0, focus_y = 0;
  private RectF clip_area = null;
  //Bitmaps.
  private LinkedBlockingQueue<BitmapRenderOperation> bm_render_operations =
    new LinkedBlockingQueue<BitmapRenderOperation>();
  private LinkedBlockingQueue<BitmapRenderOperation> hud_bm_render_operations =
    new LinkedBlockingQueue<BitmapRenderOperation>();
  //Shapes.
  private LinkedBlockingQueue<ShapeRenderOperation> shape_render_operations =
    new LinkedBlockingQueue<ShapeRenderOperation>();
  private LinkedBlockingQueue<ShapeRenderOperation>
    hud_shape_render_operations =
    new LinkedBlockingQueue<ShapeRenderOperation>();
  //Texts.
  private LinkedBlockingQueue<TextRenderOperation> hud_text_render_operations =
    new LinkedBlockingQueue<TextRenderOperation>();

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

  /**
  Draw a rotated bitmap to the canvas in world space.
  @param bitmap The bitmap to be drawn.
  @param x The X position in the world at which to draw the bitmap's centre.
  @param y The Y position in the world at which to draw the bitmap's centre.
  @param angle Radians of rotation (0.0f for no rotation).
  */
  public void draw(Bitmap bitmap, int x, int y, float angle)
  {
    draw(bitmap, x, y, angle, false);
  }

  /**
  Draw a rotated bitmap to the canvas in screen space. Parameters same as
  #{@link #draw(bitmap, x, y, angle) draw()}.
  */
  public void drawHud(Bitmap bitmap, int x, int y, float angle)
  {
    draw(bitmap, x, y, angle, true);
  }

  /**
  Generalised backend method for draw and drawHud.
  */
  private void draw(Bitmap bitmap, int x, int y, float angle, boolean to_hud)
  {
    BitmapRenderOperation operation = new BitmapRenderOperation();
    operation.bitmap = bitmap;
    operation.x = x;
    operation.y = y;
    operation.angle = angle;
    if (to_hud)
      hud_bm_render_operations.add(operation);
    else
      bm_render_operations.add(operation);
  }

  /**
  Draw a shape to the canvas in world space.
  @param shape The type of shape to draw.
  @param paint_type How to paint the shape.
  @param x The X position in the world at which to draw the top-left corner of
    the shape.
  @param y The Y position in the world at which to draw the top-left corner of
    the shape.
  @param width The width of the shape at its widest point.
  @param height The height of the shape at its highest point.
  @param colour The colour with which to draw the shape.
  */
  public void drawShape(Shape shape, PaintType paint_type, int x, int y,
                        int width, int height, Paint colour)
  {
    drawShape(shape, paint_type, x, y, width, height, colour, false);
  }

  /**
  Draw a shape to the canvas in screen space. Parameters same as
  #{@link #drawShape(Shape, PaintType, x, y, width, height, colour)
  drawShape()}.
  */
  public void drawShapeHud(Shape shape, PaintType paint_type, int x, int y,
                           int width, int height, Paint colour)
  {
    drawShape(shape, paint_type, x, y, width, height, colour, true);
  }

  /**
  Generalised backend method for drawShape and drawShapeHud.
  */
  private void drawShape(Shape shape, PaintType paint_type, int x, int y,
                         int width, int height, Paint colour, boolean to_hud)
  {
    switch (paint_type)
    {
      case STROKE:
        colour.setStyle(Paint.Style.STROKE);
        break;
      case FILL:
        colour.setStyle(Paint.Style.FILL);
        break;
    }

    ShapeRenderOperation operation = new ShapeRenderOperation();
    operation.shape = Shape.RECTANGLE;
    operation.colour = colour;
    operation.x = x;
    operation.y = y;
    operation.width = width;
    operation.height = height;
    if (to_hud)
      hud_shape_render_operations.add(operation);
    else
      shape_render_operations.add(operation);
  }

  /**
  Draw text to the canvas in screen space.
  @param text Text to draw.
  @param x The X position on the screen at which to draw the base of the text.
  @param y The Y position on the screen at which to draw the base of the text.
  @param text_size Text size to use with Paints.
  @param align Alignment of the base to the text.
  @param fill_colour Colour with which to fill the text.
  @param stroke_colour Colour with which to outline the text. If null, no
    outline.
  */
  public void drawTextHud(String text, int x, int y, float text_size,
                          Paint.Align align, Paint fill_colour,
                          Paint stroke_colour)
  {
    if (stroke_colour != null)
    {
      stroke_colour.setStyle(Paint.Style.STROKE);
      stroke_colour.setStrokeWidth(1);
      stroke_colour.setTextSize(text_size);
      stroke_colour.setTextAlign(align);
    }

    fill_colour.setTextSize(text_size);
    fill_colour.setTextAlign(align);

    TextRenderOperation operation = new TextRenderOperation();
    operation.text = text;
    operation.fill_colour = fill_colour;
    operation.stroke_colour = stroke_colour;
    operation.x = x;
    operation.y = y;
    hud_text_render_operations.add(operation);
  }

  /**
  Specify the world X and Y coordinates at which to focus the centre of the
  screen.
  @param x World X coordinate at which to focus.
  @param y World Y coordinate at which to focus.
  */
  public void focusOn(int x, int y)
  {
    focus_x = x;
    focus_y = y;
  }

  public void enableClip(float left, float top, float right, float bottom)
  {
    clip_area = new RectF(left, top, right, bottom);
  }

  public void disableClip()
  {
    clip_area = null;
  }

  /**
  Called by the engine to commit all graphical operations for the current frame.
  */
  public void finishDraw()
  {
    SurfaceHolder holder = getHolder();

    //Don't draw if the surface isn't valid.
    if (!holder.getSurface().isValid())
      return;
    Canvas canvas = holder.lockCanvas();

    //Background is black.
    canvas.drawColor(Color.BLACK);

    //Operations in world space.
    canvas.save();
    //Focus the centre of the screen to the coordinates set by focusOn().
    canvas.translate((float) (-focus_x + canvas.getWidth() / 2),
                     (float) (-focus_y + canvas.getHeight() / 2));

    //Clip if enabled.
    if (clip_area != null)
      canvas.clipRect(clip_area, Region.Op.REPLACE);

    canvas.save();
    //Perform each BitmapRenderOperation.
    while (bm_render_operations.size() > 0)
      doBitmapRenderOperation(canvas, bm_render_operations.poll());
    //Perform each ShapeRenderOperation.
    while (shape_render_operations.size() > 0)
      doShapeRenderOperation(canvas, shape_render_operations.poll());
    canvas.restore();

    //Leave world space. Draw in screen space.
    canvas.restore();
    //Perform each HUD BitmapRenderOperation.
    while (hud_bm_render_operations.size() > 0)
      doBitmapRenderOperation(canvas, hud_bm_render_operations.poll());
    //Perform each HUD ShapeRenderOperation.
    while (hud_shape_render_operations.size() > 0)
      doShapeRenderOperation(canvas, hud_shape_render_operations.poll());
    //Perform each HUD TextRenderOperation.
    while (hud_text_render_operations.size() > 0)
      doTextRenderOperation(canvas, hud_text_render_operations.poll());

    holder.unlockCanvasAndPost(canvas);
  }

  private void doBitmapRenderOperation(Canvas canvas,
                                       BitmapRenderOperation operation)
  {
    canvas.save();
    canvas.translate((float) operation.x, (float) operation.y);
    canvas.rotate((float) (operation.angle * (180.0f / Math.PI)));
    //Subtract half of the width and height from the draw position so that the
    //centre of the bitmap (instead of the top-left) is drawn at the specified
    //position.
    canvas.drawBitmap(operation.bitmap,
                      (int) (-operation.bitmap.getWidth() / 2),
                      (int) (-operation.bitmap.getHeight() / 2), null);
    canvas.restore();
  }

  private void doShapeRenderOperation(Canvas canvas,
                                      ShapeRenderOperation operation)
  {
    canvas.save();
    canvas.translate((float) operation.x, (float) operation.y);
    switch (operation.shape)
    {
      case RECTANGLE:
        canvas.drawRect(operation.x, operation.y,
          operation.x + operation.width, operation.y + operation.height,
          operation.colour);
        break;
      case OVAL:
        canvas.drawOval(new RectF(operation.x, operation.y,
          operation.x + operation.width, operation.y + operation.height),
          operation.colour);
        break;
    }
    canvas.restore();
  }

  private void doTextRenderOperation(Canvas canvas,
                                     TextRenderOperation operation)
  {
    canvas.drawText(operation.text, operation.x, operation.y,
                    operation.fill_colour);
    if (operation.stroke_colour != null)
    {
      canvas.drawText(operation.text, operation.x, operation.y,
                      operation.stroke_colour);
    }
  }
}
