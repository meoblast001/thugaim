/*
Copyright (C) 2013 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
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
    Transitioning to RUNNING state from PAUSED state, but not yet achieved.
    */
    RESUMING,
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
  private Audio audio = null;
  private Graphics graphics = null;
  private ShutdownHandlingActivity shutdown_handler = null;
  private float rotation = 0.0f;
  private boolean tapped = false;

  private RunState run_state;

  private CountDownLatch resume_countdown;
  private CountDownLatch shutdown_countdown;

  /**
  Construct engine but do not start.
  @param graphics Graphics instance to which the game will be drawn.
  @param audio Audio manager instance.
  @param runtime Instance of IGameRuntime which will be initialised and contains
    game-specific code.
  */
  public Engine(Graphics graphics, Audio audio, IGameRuntime runtime,
                ShutdownHandlingActivity shutdown_handler)
  {
    super();
    this.graphics = graphics;
    this.audio = audio;
    this.runtime = runtime;
    this.shutdown_handler = shutdown_handler;
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

        //Wait until unpaused.
        resume_countdown = new CountDownLatch(1);
        forceAwait(resume_countdown);

        //Unpause game.
        run_state = RunState.RUNNING;
        resume_countdown = null;
        //If previous_milliseconds is not updated, the entire paused time period
        //will occur in one frame as if unpaused during that time.
        previous_milliseconds = System.currentTimeMillis();
      }

      long current_milliseconds = System.currentTimeMillis();
      //Update runtime with difference between last frame and this frame as the
      //delta.
      runtime.update(current_milliseconds - previous_milliseconds, rotation,
                     tapped);
      //Draw frame.
      graphics.finishDraw();

      //Shutdown game if the game runtime is over.
      if (!runtime.isRunning())
      {
        shutdown_handler.runOnUiThread(new Runnable()
          {
            public void run()
            {
              shutdown_handler.onShutdown(runtime.didPlayerWin());
            }
          });
        break;
      }

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
    if (shutdown_countdown != null)
      //Free other thread waiting at shutdown().
      shutdown_countdown.countDown();
  }

  /**
  Pause the game state.
  */
  public void pause()
  {
    run_state = RunState.PAUSING;
  }

  /**
  If paused, resume. Else do nothing.
  */
  public void unpause()
  {
    if (resume_countdown != null)
    {
      run_state = RunState.RESUMING;
      resume_countdown.countDown();
    }
  }

  /**
  Shutdown the game engine, terminating the game.
  */
  public void shutdown()
  {
    //Return if already shutdown or transitioning states.
    if (run_state != RunState.RUNNING && run_state != RunState.PAUSED)
      return;

    run_state = RunState.PERFORMING_SHUTDOWN;
    //Wait until other thread finishes shutting down at run().
    shutdown_countdown = new CountDownLatch(1);
    forceAwait(shutdown_countdown);
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
  @param rotation Rotation determined from accelerometer pull on Y-axis. Values
    between negative and positive SensorManager.STANDARD_GRAVITY.
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
  Get a reference to the Audio manager instance this Engine is using.
  @return Audio manager reference.
  */
  public Audio getAudio()
  {
    return audio;
  }

  /**
  Calls await on a CountDownLatch. If fails, tries again until successful.
  @param latch CountDownLatch on which to await.
  */
  private void forceAwait(CountDownLatch latch)
  {
    try
    {
      latch.await();
    }
    catch (InterruptedException e)
    {
      forceAwait(latch);
    }
  }
}
