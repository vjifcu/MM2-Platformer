import java.awt.Graphics2D;

public class BossDoorTile extends Tile
{

	static int counter = 40;
	
	public BossDoorTile(int x, int y, char image)
	{
		super(x, y, image);
	}

	@Override
	public void draw(Graphics2D b, int offset)
	{
		super.draw(b, offset);

		// If the player runs into the boss door tile, change the room
		if ((DrawPanel.player.getX() + DrawPanel.player.displayX + DrawPanel.player.getWidth() + 2) / 32 >= x && x == Level.getCurrentRoom().maxX - 1)
			Level.changeRoom(1, 0, true, DrawPanel.b);
	}

}
