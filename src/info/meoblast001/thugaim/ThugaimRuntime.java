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
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.PointF;

import info.meoblast001.thugaim.engine.*;
import info.meoblast001.thugaim.npc.*;

public class ThugaimRuntime implements IGameRuntime
{
  public static final float PLAY_SIZE = 2000.0f;
  private final int NUM_HYDROGEN_FIGHTERS = 10;

  private Engine engine;
  private Context context;

  private World world;
  private StationGraph station_graph;
  private Player player;

  public void init(Engine engine)
  {
    this.engine = engine;
    context = engine.getGraphics().getContext();

    world = new World(engine);
    station_graph = new StationGraph(engine, world);

    player = new Player(engine, station_graph);
    world.insertActor(player);
    world.focusOnActor("player");

    for (int i = 0; i < NUM_HYDROGEN_FIGHTERS; ++i)
    {
      Station[] stations = station_graph.getStations();
      Station use_station = stations[
        (int) Math.floor(Math.random() * stations.length)];
      PointF position = use_station.getPosition();
      world.insertActor(new HydrogenFighter(engine, position.x, position.y,
                                            0.0f, station_graph));
    }
  }

  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    station_graph.update();
    world.update(millisecond_delta, rotation, tapped);
  }
}