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
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import info.meoblast001.thugaim.engine.*;

/**
Play activity. During initialisation, starts the Engine, which runs on its own
thread. After initialisation, forwards events to Engine.
*/
public class Thugaim extends ShutdownHandlingActivity
  implements View.OnTouchListener, SensorEventListener
{
  private ThugaimRuntime runtime = null;
  private Sensor accelerometer = null;
  private Engine engine = null;
  private Graphics graphics = null;
  private int current_level = 0;

  /**
  Called when the activity is first created.
  */
  @Override
  public void onCreate(Bundle saved_instance_state)
  {
    super.onCreate(saved_instance_state);

    SensorManager sensor_manager =
      (SensorManager) getSystemService(SENSOR_SERVICE);
    accelerometer = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensor_manager.registerListener(this, accelerometer,
                                    SensorManager.SENSOR_DELAY_NORMAL);

    setContentView(R.layout.main);
    graphics = (Graphics) findViewById(R.id.graphics);
    graphics.setOnTouchListener(this);

    runtime = new ThugaimRuntime(getResources());
    Bundle extras = getIntent().getExtras();
    if (extras != null)
    {
      current_level = extras.getInt("current_level");
      runtime.setLevel(current_level);
    }

    engine = new Engine(graphics, runtime, this);
    engine.start();
  }

  @Override
  public void onPause()
  {
    super.onPause();
    engine.pause();
  }

  @Override
  public void onStop()
  {
    super.onStop();
    engine.pause();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    engine.unpause();
  }

  @Override
  public void onRestart()
  {
    super.onRestart();
    engine.unpause();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    engine.shutdown();
  }

  @Override
  public void onBackPressed()
  {
    moveTaskToBack(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.menu_quit:
        engine.shutdown();
        System.exit(0);
        return false;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy)
  {
    //Do nothing.
  }

  public void onSensorChanged(SensorEvent event)
  {
    engine.setRotation(event.values[1]);
  }

  public boolean onTouch(View view, MotionEvent event)
  {
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
    {
      engine.setTapped(true);
      return true;
    }
    else if (event.getActionMasked() == MotionEvent.ACTION_UP)
    {
      engine.setTapped(false);
      return true;
    }
    return false;
  }

  public void onShutdown(boolean winner)
  {
    if (winner == true && runtime.hasNextLevel())
    {
      //Go to next level.
      Intent intent = new Intent(this, Thugaim.class);
      intent.putExtra("current_level", current_level + 1);
      startActivity(intent);
      finish();
    }
    else
    {
      //Game finished.
      Intent intent = new Intent(this, GameOver.class);
      intent.putExtra("player_won", winner);
      startActivity(intent);
      finish();
    }
  }
}
