/*
Copyright (C) 2016 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
Activity informing the user that a checkpoint has been reached.
*/
public class CheckpointReached extends Activity
{
  private int current_level = 0;

  /**
  Called when the activity is first created.
  */
  @Override
  public void onCreate(Bundle saved_instance_state)
  {
    super.onCreate(saved_instance_state);
    setContentView(R.layout.checkpoint_reached);

    //Store the current level locally.
    Bundle extras = getIntent().getExtras();
    if (extras != null)
      current_level = extras.getInt("current_level");
  }

  /**
  Go to the next level.
  */
  public void goToNextLevel(View view)
  {
      Intent intent = new Intent(this, Thugaim.class);
      intent.putExtra("current_level", current_level);
      startActivity(intent);
      finish();
  }

  /**
  Determine if a level is a checkpoint.
  @param level The level to test.
  @return True if it is a checkpoint, otherwise false.
  */
  public static boolean isLevelCheckpoint(int level)
  {
    return (level + 1) % ThugaimRuntime.CHECKPOINT_INTERVAL == 0;
  }
}
