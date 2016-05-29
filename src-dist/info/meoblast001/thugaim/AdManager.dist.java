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
import android.widget.Button;
//{{{COMMENT_START_WHEN_ADS_OFF}}}
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
//{{{COMMENT_END_WHEN_ADS_OFF}}}

/**
Activity which displays and manages ads if they are enabled. Otherwise it does
nothing.
*/
public class AdManager
{
  // Represents the ad unit on the checkpoint activity.
  public static final int UNIT_CHECKPOINT = 1;

  /**
  Load an advertisement.
  @param unit One of the UNIT_ constants pertaining to the specific ad being
    rendered. This will be mapped to a view ID in the activity which will only
    exist if ads are enabled.
  @param activity_exit_buttons All buttons which exit the activity. These will
    be disabled while the advertisement loads.
  */
  public static void load(int unit, Activity activity,
                          final Button[] activity_exit_buttons)
  {
    //{{{COMMENT_START_WHEN_ADS_OFF}}}
    //First map the internal unit ID to the resource for the AdView.
    int ad_resource = 0;
    switch (unit)
    {
      case UNIT_CHECKPOINT:
        ad_resource = R.id.checkpoint_ad;
        break;
      default:
        return;
    }
    //Get the AdView and return if not found.
    AdView ad_view = (AdView) activity.findViewById(ad_resource);
    if (ad_view == null)
      return;
    AdRequest ad_request = new AdRequest.Builder().build();
    //Disable all buttons which exit the activity until the ad is loaded.
    setAllEnabled(activity_exit_buttons, false);
    //Create an ad listener which enabled buttons exiting the activity again.
    ad_view.setAdListener(new AdListener()
      {
        public void onAdFailedToLoad(int error_code)
        {
          super.onAdFailedToLoad(error_code);
          setAllEnabled(activity_exit_buttons, true);
        }

        public void onAdLoaded()
        {
          super.onAdLoaded();
          setAllEnabled(activity_exit_buttons, true);
        }
      });
    //Load the ad.
    ad_view.loadAd(ad_request);
    //{{{COMMENT_END_WHEN_ADS_OFF}}}
  }

  /**
  Enable or disable an array of buttons.
  @param buttons Array of buttons to enable or disable.
  @param enabled True if the buttons should be enabled, false if disabled.
  */
  private static void setAllEnabled(final Button[] buttons, boolean enabled)
  {
    for (Button button : buttons)
      button.setEnabled(enabled);
  }
}
