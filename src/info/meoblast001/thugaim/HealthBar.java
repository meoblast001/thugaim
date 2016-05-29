/*
Copyright (C) 2014 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

import info.meoblast001.thugaim.engine.Graphics;
import info.meoblast001.thugaim.R;

/**
Thin bar spanning the top of the screen signifying the player's health.
*/
public class HealthBar
{
  private Graphics graphics;
  private Bitmap health_icon = null;
  private Player player;

  private final int MAX_HEALTH_BAR_WIDTH = 150;
  private final int HEALTH_BAR_COLOUR = Color.rgb(204, 0, 0);

  public HealthBar(Graphics graphics, Player player)
  {
    this.graphics = graphics;
    health_icon = BitmapFactory.decodeResource(
      graphics.getContext().getResources(), R.drawable.healthicon);
    this.player = player;
  }

  public void update()
  {
    //Health icon.
    graphics.drawHud(health_icon, health_icon.getWidth() / 2,
                     health_icon.getHeight() / 2, 0.0f);

    //Bar background.
    Paint container_fill = new Paint();
    container_fill.setColor(Color.BLACK);
    graphics.drawShapeHud(Graphics.Shape.RECTANGLE, Graphics.PaintType.FILL,
                          health_icon.getWidth() + 2, 0, MAX_HEALTH_BAR_WIDTH,
                          health_icon.getHeight(), container_fill);

    //Bar.
    Paint bar_fill = new Paint();
    bar_fill.setColor(HEALTH_BAR_COLOUR);
    int health_bar_width = (int) (MAX_HEALTH_BAR_WIDTH *
      ((float) player.getHealth() / (float) Player.MAX_HEALTH));
    graphics.drawShapeHud(Graphics.Shape.RECTANGLE, Graphics.PaintType.FILL,
                          health_icon.getWidth() + 2, 0, health_bar_width,
                          health_icon.getHeight(), bar_fill);

    //Bar frame.
    Paint container_line = new Paint();
    container_line.setColor(Color.WHITE);
    container_line.setStrokeWidth(1);
    graphics.drawShapeHud(Graphics.Shape.RECTANGLE, Graphics.PaintType.STROKE,
                          health_icon.getWidth() + 2, 0, MAX_HEALTH_BAR_WIDTH,
                          health_icon.getHeight(), container_line);
  }
}
