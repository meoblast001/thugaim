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
