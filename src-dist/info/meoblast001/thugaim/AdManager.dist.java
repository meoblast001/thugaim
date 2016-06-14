/*
Copyright (C) 2016 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
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
  //Represents the ad unit on the instructions activity.
  public static final int UNIT_INSTRUCTIONS = 1;
  //Represents the ad unit on the checkpoint activity.
  public static final int UNIT_CHECKPOINT = 2;
  //Represents the ad unit on the gameover activity.
  public static final int UNIT_GAMEOVER = 3;

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
      case UNIT_INSTRUCTIONS:
        ad_resource = R.id.instructions_ad;
        break;
      case UNIT_CHECKPOINT:
        ad_resource = R.id.checkpoint_ad;
        break;
      case UNIT_GAMEOVER:
        ad_resource = R.id.gameover_ad;
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
