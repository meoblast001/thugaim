/*
Copyright (C) 2014 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
Activity which appears when the game ends either by the player winning or
losing.
*/
public class GameOver extends Activity
{
  /**
  Called when the activity is first created.
  */
  @Override
  public void onCreate(Bundle saved_instance_state)
  {
    super.onCreate(saved_instance_state);
    setContentView(R.layout.game_over);

    //Change text to show whether player won or lost.
    Bundle extras = getIntent().getExtras();
    if (extras != null)
    {
      TextView notice = (TextView) findViewById(R.id.game_over_notice);

      if (extras.getBoolean("player_won"))
        notice.setText(R.string.game_over_win);
      else
        notice.setText(R.string.game_over_lose);
    }
  }

  @Override
  public void onBackPressed()
  {
    moveTaskToBack(true);
  }

  public void restart(View view)
  {
    Intent intent = new Intent(view.getContext(), Thugaim.class);
    intent.putExtra("current_level", ThugaimRuntime.getCheckpointLevel());
    startActivity(intent);
    finish();
  }

  public void quit(View view)
  {
    System.exit(0);
  }
}
