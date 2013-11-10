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
      runtime.update(current_milliseconds - previous_milliseconds);
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
