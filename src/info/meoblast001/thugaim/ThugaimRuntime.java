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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
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

  //Level information.
  public boolean has_next_level = false;
  public int stations;
  public int hydrogen_fighters;
  public int play_size;

  public void init(Engine engine)
  {
    this.engine = engine;
    context = engine.getGraphics().getContext();

    world = new World(engine);
    station_graph = new StationGraph(engine, world, stations, play_size);

    player = new Player(engine, station_graph);
    world.insertActor(player);
    world.focusOnActor("player");

    //Instantiate HydrogenFighters at random stations.
    for (int i = 0; i < hydrogen_fighters; ++i)
    {
      Station[] stations = station_graph.getStations();
      Station use_station = stations[
        (int) Math.floor(Math.random() * stations.length)];
      PointF position = use_station.getPosition();
      world.insertActor(new HydrogenFighter(engine, position.x, position.y,
                                            0.0f, station_graph));
    }

    health_bar = new HealthBar(engine.getGraphics(), player);
  }

  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    station_graph.update();
    world.update(millisecond_delta, rotation, tapped);
    health_bar.update();
  }

  public boolean isRunning()
  {
    return player.getWorld() != null && station_graph.getStations().length > 0;
  }

  public boolean didPlayerWin()
  {
    //Player won if there are no stations remaining.
    return station_graph.getStations().length == 0;
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
          if (processing_level != current_level)
            continue;

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

            successfully_loaded_cur_level = true;
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
}
