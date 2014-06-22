/*
Copyright (C) 2013 Braden Walters

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

/**
Extend this class to define game functionality.
*/
public interface IGameRuntime
{
  /**
  Called after engine initialisation. Initialises the game.
  @param engine An instance of the Engine.
  */
  public void init(Engine engine);
  /**
  Called once per frame. Allows the game to respond to user input, perform game
  logic, and draw actors.
  @param millisecond_delta Milliseconds elapsed since last call.
  @param rotation Rotation of the device, where positive values are clockwise
    and negative values are counter-clockwise.
  @param tapped Signifies whether the screen is being tapped.
  */
  public void update(long millisecond_delta, float rotation, boolean tapped);
  /**
  Is the game still running? Called every frame. If false the engine shuts down.
  @return True if running, false if not.
  */
  public boolean isRunning();
  /**
  Did the player win? Only defined after isRunning() returns false.
  @return True if player won, false if player lost.
  */
  public boolean didPlayerWin();
}
