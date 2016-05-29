/*
Copyright (C) 2013 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

/**
Activity giving the user instructions before the game begins.
*/
public class Instructions extends Activity
{
  boolean music_enabled = true;

  /**
  Called when the activity is first created.
  */
  @Override
  public void onCreate(Bundle saved_instance_state)
  {
    super.onCreate(saved_instance_state);
    setContentView(R.layout.instructions);
  }

  @Override
  public void onBackPressed()
  {
    moveTaskToBack(true);
  }

  public void goToGame(View view)
  {
    Intent intent = new Intent(this, Thugaim.class);
    intent.putExtra("current_level", 0);
    intent.putExtra("music_enabled", music_enabled);
    startActivity(intent);
    finish();
  }

  public void toggleMusic(View view)
  {
    CheckBox checkbox = (CheckBox) view;
    music_enabled = checkbox.isChecked();
  }
}
