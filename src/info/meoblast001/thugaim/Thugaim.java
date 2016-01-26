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
import android.app.AlertDialog;
import android.content.DialogInterface;
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
  private Audio audio = null;
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

    audio = new Audio(this);

    try
    {
      runtime = new ThugaimRuntime(getResources());
      Bundle extras = getIntent().getExtras();
      if (extras != null)
      {
        current_level = extras.getInt("current_level");
        runtime.setLevel(current_level);
      }

      engine = new Engine(graphics, audio, runtime, this);
      engine.start();
    }
    catch (ThugaimRuntime.LoadLevelsException e)
    {
      fail(getString(R.string.load_levels_exception));
    }
  }

  @Override
  public void onPause()
  {
    super.onPause();
    if (engine != null)
      engine.pause();
    if (audio != null)
      audio.pauseMusic();
  }

  @Override
  public void onStop()
  {
    super.onStop();
    if (engine != null)
      engine.pause();
    if (audio != null)
      audio.pauseMusic();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    if (engine != null)
      engine.unpause();
    if (audio != null)
      audio.unpauseMusic();
  }

  @Override
  public void onRestart()
  {
    super.onRestart();
    if (engine != null)
      engine.unpause();
    if (audio != null)
      audio.unpauseMusic();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    if (engine != null)
      engine.shutdown();
    if (audio != null)
      audio.stopMusic();
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
    if (engine != null)
      engine.setRotation(event.values[1]);
  }

  public boolean onTouch(View view, MotionEvent event)
  {
    if (engine == null)
      return false;

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
      Intent intent = null;
      if (CheckpointReached.isLevelCheckpoint(current_level + 1))
        //A checkpoint has been reached. Go to the checkpoint page.
        intent = new Intent(this, CheckpointReached.class);
      else
        //Go to next level.
        intent = new Intent(this, Thugaim.class);

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

  /**
  Creates a fatal error dialog and ends the game.
  @param message The message to display to the user.
  */
  private void fail(String message)
  {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle(R.string.fatal_error);
    alert.setMessage(message);
    alert.setPositiveButton(android.R.string.ok,
      new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int which)
        {
          Thugaim.this.finish();
        }
      });
    alert.setIcon(android.R.drawable.ic_dialog_alert);
    alert.show();
  }
}
