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

public class Engine extends Thread
{
  private enum RunState
  {
    RUNNING,
    PAUSING,
    PAUSED,
    PERFORMING_SHUTDOWN,
    SHUTDOWN
  }

  private IGameRuntime runtime = null;
  private Graphics graphics = null;
  private float rotation = 0.0f;
  private boolean tapped = false;

  private RunState run_state;

  public Engine(Graphics graphics, IGameRuntime runtime)
  {
    super();
    this.graphics = graphics;
    this.runtime = runtime;
    runtime.init(this);
  }

  @Override
  public void run()
  {
    run_state = RunState.RUNNING;

    long previous_milliseconds = 0;
    while (run_state != RunState.PERFORMING_SHUTDOWN)
    {
      if (run_state == RunState.PAUSING)
      {
        run_state = RunState.PAUSED;
        while (run_state == RunState.PAUSED)
          waitOrNot();
      }

      long current_milliseconds = System.currentTimeMillis();
      runtime.update(current_milliseconds - previous_milliseconds, rotation,
                     tapped);
      graphics.finishDraw();

      previous_milliseconds = current_milliseconds;
    }

    run_state = RunState.SHUTDOWN;
  }

  public void pause()
  {
    run_state = RunState.PAUSING;

    while (run_state != RunState.PAUSED)
      waitOrNot();
  }

  public void shutdown()
  {
    run_state = RunState.PERFORMING_SHUTDOWN;

    while (run_state != RunState.SHUTDOWN)
      waitOrNot();
  }

  public void setTapped(boolean tapped)
  {
    this.tapped = tapped;
  }

  public void setRotation(float rotation)
  {
    this.rotation = rotation;
  }

  public Graphics getGraphics()
  {
    return graphics;
  }

  private void waitOrNot()
  {
    try
    {
      wait();
    }
    catch (InterruptedException e)
    {
      //Do nothing if fails.
    }
  }
}
