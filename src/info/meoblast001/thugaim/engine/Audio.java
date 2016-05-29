/*
Copyright (C) 2015 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim.engine;

import android.content.Context;
import android.media.MediaPlayer;

/**
Audio manager for playing music and sound.
*/
public class Audio
{
  private Context context = null;
  private MediaPlayer music = null;

  public Audio(Context context)
  {
    this.context = context;
  }

  /**
  Start a song from a resource. Stops previously song if playing.
  @param res_id Resource ID containing the music.
  */
  public void startMusic(int res_id)
  {
    if (music != null)
      stopMusic();

    music = MediaPlayer.create(context, res_id);
    music.setLooping(true);
    music.start();
  }

  /**
  Stop any music that is currently playing if it is playing.
  */
  public void stopMusic()
  {
    if (music != null)
    {
      music.stop();
      music.release();
      music = null;
    }
  }

  /**
  Pauses music if playing.
  */
  public void pauseMusic()
  {
    if (music != null)
      music.pause();
  }

  /**
  Resumes music if paused.
  */
  public void unpauseMusic()
  {
    if (music != null)
      music.start();
  }
}
