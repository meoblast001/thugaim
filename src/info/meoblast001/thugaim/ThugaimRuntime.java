package info.meoblast001.thugaim;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import info.meoblast001.thugaim.engine.*;

public class ThugaimRuntime implements IGameRuntime
{
  private Engine engine;
  private Context context;

  private Bitmap player;
  private float rotate = 0.0f;

  public void init(Engine engine)
  {
    this.engine = engine;
    context = engine.getGraphics().getContext();

    Resources resources = context.getResources();
    player = BitmapFactory.decodeResource(resources, R.drawable.player);
  }

  public void update(long millisecond_delta)
  {
    rotate += (0.1f * (float) millisecond_delta);
    engine.getGraphics().draw(player, 100, 100, rotate);
  }
}
