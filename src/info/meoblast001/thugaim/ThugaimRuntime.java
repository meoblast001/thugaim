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

package info.meoblast001.thugaim;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import info.meoblast001.thugaim.engine.*;
import info.meoblast001.thugaim.npc.*;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

/**
Top level of game code. Manages all gameplay elements either directly or
indirectly.
*/
public class ThugaimRuntime implements IGameRuntime
{
  private Engine engine;
  private Context context;

  private World world;
  private StationGraph station_graph;
  private Player player;
  private HealthBar health_bar;
  private boolean player_won = false, player_lost = false;
  private long started_level_complete_millis = Long.MAX_VALUE;
  private static final long SHOW_LEVEL_COMPLETE_BEFORE_END_MILIS = 3000;

  //Level information.
  private int current_level = 0;
  private boolean has_next_level = false;
  private int stations;
  private int hydrogen_fighters;
  private int play_size;

  public void init(Engine engine)
  {
    this.engine = engine;
    context = engine.getGraphics().getContext();

    //Only show what's in the play area.
    int half_play_size = play_size / 2;
    engine.getGraphics().enableClip(-half_play_size, half_play_size,
                                    half_play_size, -half_play_size);

    world = new World(engine, play_size);
    station_graph = new StationGraph(engine, world, stations, play_size);

    player = new Player(engine, station_graph);
    world.insertActor(player);
    world.focusOnActor("player");

    HydrogenFighter.generateAll(engine, world, play_size, station_graph,
                                hydrogen_fighters);

    health_bar = new HealthBar(engine.getGraphics(), player);
    PlayAreaShield.generateAll(engine, world, play_size);
  }

  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    station_graph.update();
    world.update(millisecond_delta, rotation, tapped);
    health_bar.update();
    displayLevelNumber();

    //Player won if there are no stations remaining and the player didn't
    //already lose.
    if (station_graph.getStations().length == 0 && !player_lost)
      player_won = true;
    //Player lost if it's no longer in the world and has not already won.
    if (player.getWorld() == null && !player_won)
      player_lost = true;

    if (player_won)
      displayLevelComplete();
  }

  public boolean isRunning()
  {
    return !((player_won && System.currentTimeMillis() -
      started_level_complete_millis > SHOW_LEVEL_COMPLETE_BEFORE_END_MILIS) ||
      player_lost);
  }

  public boolean didPlayerWin()
  {
    return player_won;
  }

  /**
  Load and use information about a level. Must be called before init().
  @param current_level 0-based level number.
  @param resources Activity's resources.
  */
  public void setLevel(int current_level, Resources resources)
  {
    try
    {
      XmlResourceParser xml_parser = resources.getXml(R.xml.levels);
      int processing_level = -1;
      boolean successfully_loaded_cur_level = false;
      int event_type = xml_parser.getEventType();
      while (event_type != XmlResourceParser.END_DOCUMENT)
      {
        if (event_type == XmlResourceParser.START_TAG &&
            xml_parser.getName().equals("level"))
        {
          ++processing_level;
          if (processing_level > current_level)
            has_next_level = true;

          if (processing_level == current_level)
          {
            for (int i = 0; i < xml_parser.getAttributeCount(); ++i)
            {
              String attr_name = xml_parser.getAttributeName(i);
              String attr_value = xml_parser.getAttributeValue(i);

              if (attr_name.equals("stations"))
                stations = Integer.parseInt(attr_value);
              else if (attr_name.equals("hydrogen_fighters"))
                hydrogen_fighters = Integer.parseInt(attr_value);
              else if (attr_name.equals("play_size"))
                play_size = Integer.parseInt(attr_value);
              //Else ignore this attribute.

              this.current_level = current_level;
              successfully_loaded_cur_level = true;
            }
          }
        }
        event_type = xml_parser.next();
      }
    }
    //Currently there's no way to recover from exceptions here.
    //TODO: Raise something that could be handled to display a nice error screen
    //  and cleanly shut down.
    catch (XmlPullParserException e)
    {
      throw new RuntimeException(e.getMessage());
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage());
    }
    catch (NumberFormatException e)
    {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
  Does a level proceed the current level?
  @return True if yes, false if no.
  */
  public boolean hasNextLevel()
  {
    return has_next_level;
  }

  /**
  Displays the current level at the bottom-left of the screen.
  */
  private void displayLevelNumber()
  {
    Paint fill = new Paint();
    fill.setColor(Color.BLACK);
    Paint stroke = new Paint();
    stroke.setColor(Color.WHITE);
    Graphics graphics = engine.getGraphics();
    graphics.drawTextHud(context.getString(R.string.level_number_indicator,
                                           current_level + 1),
                         10, graphics.getHeight() - 10, 30.0f,
                         Paint.Align.LEFT, fill, stroke);
  }

  /**
  Displays the level complete screen. If called first time, sets the timer to
  end the game.
  */
  private void displayLevelComplete()
  {
    if (started_level_complete_millis == Long.MAX_VALUE)
      started_level_complete_millis = System.currentTimeMillis();

    Paint fill = new Paint();
    fill.setARGB(255, 0, 100, 0);
    Paint stroke = new Paint();
    stroke.setColor(Color.WHITE);
    Graphics graphics = engine.getGraphics();
    graphics.drawTextHud(context.getString(R.string.level_complete),
                         graphics.getWidth() / 2, graphics.getHeight() / 2,
                         30.0f, Paint.Align.CENTER, fill, stroke);
  }
}
