/*
Copyright (C) 2015 Braden Walters

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
