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

import java.util.concurrent.CountDownLatch;

/**
Controls the game's runtime.
*/
public class Engine extends Thread
{
  /**
  Specifies whether the game is running, not, or in a transitional or temporary
  state.
  */
  private enum RunState
  {
    /**
    Currently running with no plans to stop.
    */
    RUNNING,
    /**
    Transitioning to PAUSED state, but not yet achieved.
    */
    PAUSING,
    /**
    Paused but can be resumed.
    */
    PAUSED,
    /**
    Transitioning to SHUTDOWN state, but not yet achieved.
    */
    PERFORMING_SHUTDOWN,
    /**
    Finished and cannot be resumed.
    */
    SHUTDOWN,
  }

  private IGameRuntime runtime = null;
  private Graphics graphics = null;
  private float rotation = 0.0f; //In degrees.
  private boolean tapped = false;

  private RunState run_state;

  private CountDownLatch pause_countdown;
  private CountDownLatch shutdown_countdown;

  /**
  Construct engine but do not start.
  @param graphics Graphics instance to which the game will be drawn.
  @param runtime Instance of IGameRuntime which will be initialised and contains
    game-specific code.
  */
  public Engine(Graphics graphics, IGameRuntime runtime)
  {
    super();
    this.graphics = graphics;
    this.runtime = runtime;
    runtime.init(this);
  }

  /**
  Run by calling the start() method on an Engine instance. Contains the entire
  game loop from start to shutdown.
  */
  @Override
  public void run()
  {
    run_state = RunState.RUNNING;

    long previous_milliseconds = System.currentTimeMillis();
    //Game loop. Continue until the engine begins to shutdown.
    while (run_state != RunState.PERFORMING_SHUTDOWN)
    {
      //If pausing, pause and wait until unpaused. Will be unpaused on other
      //thread.
      if (run_state == RunState.PAUSING)
      {
        run_state = RunState.PAUSED;
        pause_countdown.countDown(); //Free other thread waiting at pause().
        while (run_state == RunState.PAUSED)
          waitOrNot();
      }

      long current_milliseconds = System.currentTimeMillis();
      //Update runtime with difference between last frame and this frame as the
      //delta.
      runtime.update(current_milliseconds - previous_milliseconds, rotation,
                     tapped);
      //Draw frame.
      graphics.finishDraw();

      previous_milliseconds = current_milliseconds;

      try
      {
        Thread.sleep(10);
      }
      catch (InterruptedException e)
      {
        //Can't do much.
      }
    }

    run_state = RunState.SHUTDOWN;
    shutdown_countdown.countDown(); //Free other thread waiting at shutdown().
  }

  /**
  Pause the game state.
  */
  public void pause()
  {
    run_state = RunState.PAUSING;
    //Wait until other thread finishes pausing at run().
    pause_countdown = new CountDownLatch(1);
    try
    {
      pause_countdown.await();
    }
    catch (InterruptedException e)
    {
      //Do nothing if fails.
    }
  }

  /**
  Shutdown the game engine, terminating the game.
  */
  public void shutdown()
  {
    run_state = RunState.PERFORMING_SHUTDOWN;
    //Wait until other thread finishes shutting down at run().
    shutdown_countdown = new CountDownLatch(1);
    try
    {
      shutdown_countdown.await();
    }
    catch (InterruptedException e)
    {
      //Do nothing if fails.
    }
  }

  /**
  Called if screen is tapped or untapped.
  @param tapped True if tapped, false if untapped.
  */
  public void setTapped(boolean tapped)
  {
    this.tapped = tapped;
  }

  /**
  Called if the device's rotation is changed.
  @param rotation Rotation in degrees on the axis through the screen.
  */
  public void setRotation(float rotation)
  {
    this.rotation = rotation;
  }

  /**
  Get a reference to the Graphics instance this Engine is using.
  @return Graphics reference.
  */
  public Graphics getGraphics()
  {
    return graphics;
  }

  /**
  Should wait, but if an exception occurs, ignore it.
  */
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
