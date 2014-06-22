/*
Copyright (C) 2014 Braden Walters

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
import android.os.Bundle;
import android.view.View;
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
    startActivity(intent);
    finish();
  }

  public void quit(View view)
  {
    System.exit(0);
  }
}
