/*
Copyright (C) 2014 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
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
