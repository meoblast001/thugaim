/*
Copyright (C) 2016 Braden Walters

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

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

    //Render advertisement if one exists in the activity.
    AdView ad_view = (AdView) findViewById(R.id.checkpoint_ad);
    if (ad_view != null)
    {
      //First disable the continue button until the ad is displayed.
      final View continue_button = findViewById(R.id.continue_button);
      continue_button.setEnabled(false);
      //Then load and render ad. When loaded, enable the continue button again.
      AdRequest ad_request = new AdRequest.Builder().build();
      ad_view.setAdListener(new AdListener()
        {
          public void onAdFailedToLoad(int error_code)
          {
            super.onAdFailedToLoad(error_code);
            continue_button.setEnabled(true);
          }

          public void onAdLoaded()
          {
            super.onAdLoaded();
            continue_button.setEnabled(true);
          }
        });
      ad_view.loadAd(ad_request);
    }
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
