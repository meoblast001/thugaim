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

package info.meoblast001.thugaim;

/**
A description of a level loaded from the XML file.
*/
public class LevelDescriptor
{
  private String music = null;
  private int stations = 0;
  private int hydrogen_fighters = 0;
  private int play_size = 0;

  /**
  Gets the name of the music to play.
  @return The name of the raw resource.
  */
  public String getMusic()
  {
    return music;
  }

  /**
  Sets the name of the music to play.
  @param value The name of the raw resource.
  */
  public void setMusic(String value)
  {
    music = value;
  }

  /**
  Gets the amount of stations in the level.
  @return The amount of stations.
  */
  public int getStations()
  {
    return stations;
  }

  /**
  Sets the amount of stations in the level.
  @param value The amount of stations.
  */
  public void setStations(int value)
  {
    stations = value;
  }

  /**
  Gets the amount of hydrogen fighter NPCs in the level.
  @return The amount of hydrogen fighters.
  */
  public int getHydrogenFighters()
  {
    return hydrogen_fighters;
  }

  /**
  Sets the amount of hydrogen fighter NPCs in the level.
  @param value The amount of hydrogen fighters.
  */
  public void setHydrogenFighters(int value)
  {
    hydrogen_fighters = value;
  }

  /**
  Gets the play size. Only one dimension is required because the play area is
  square.
  @return The play size in one dimension.
  */
  public int getPlaySize()
  {
    return play_size;
  }

  /**
  Sets the play size. Only one dimension is required because the play area is
  square.
  @param value The play size in one dimension.
  */
  public void setPlaySize(int value)
  {
    play_size = value;
  }
}
