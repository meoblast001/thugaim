/*
Copyright (C) 2014 Braden Walters

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

import android.app.Activity;

/**
Handles engine shutdown.
*/
public abstract class ShutdownHandlingActivity extends Activity
{
  /**
  Called by the engine after it shuts down internally (i.e. calls to
  Engine.shutdown do not result in this method being called).
  @param winner If true, player won, if false, player lost.
  */
  public abstract void onShutdown(boolean winner);
}
