/*
Copyright (C) 2015 Braden Walters

This software may be modified and distributed under the terms of the MIT
license. See the LICENSE file for details.
*/

package info.meoblast001.thugaim;

/**
An actor which can take damage from projectiles.
*/
public interface IDamageable
{
  /**
  Reduces health by 1. If health reaches zero, actor is removed from the world.
  */
  public void reduceHealth();
}
