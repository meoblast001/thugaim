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

import android.app.Activity;
import android.os.Bundle;

import info.meoblast001.thugaim.engine.*;

public class Thugaim extends Activity
{
  private Engine engine = null;
  private Graphics graphics = null;

  /**
  Called when the activity is first created.
  */
  @Override
  public void onCreate(Bundle saved_instance_state)
  {
    super.onCreate(saved_instance_state);

    setContentView(R.layout.main);
    graphics = (Graphics) findViewById(R.id.graphics);
    engine = new Engine(graphics, new ThugaimRuntime());
    engine.start();
  }

  @Override
  public void onPause()
  {
    engine.pause();
  }

  @Override
  public void onStop()
  {
    engine.pause();
  }

  @Override
  public void onDestroy()
  {
    engine.shutdown();
  }
}
