/*
Copyright (C) 2013 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
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
