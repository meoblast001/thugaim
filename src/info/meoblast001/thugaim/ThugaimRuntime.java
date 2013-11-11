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
import info.meoblast001.thugaim.engine.*;

public class ThugaimRuntime implements IGameRuntime
{
  private Engine engine;
  private Context context;

  private Player player;

  public void init(Engine engine)
  {
    this.engine = engine;
    context = engine.getGraphics().getContext();

    player = new Player(engine);
  }

  public void update(long millisecond_delta, float rotation, boolean tapped)
  {
    player.update(millisecond_delta, rotation);
  }
}
