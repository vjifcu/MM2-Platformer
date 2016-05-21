import java.awt.AlphaComposite;
import java.awt.Graphics2D;

//This class handles animated tiles that are supposed to be offset depending on which column they are in.
//For example, the pistons in metal man's level have their animations alternate from one column to the next.
public class OffsetTile extends Tile
{

	public OffsetTile(int x, int y, char image)
	{
		super(x, y, image);
	}

	//Almost the exact same as Tile class's draw method
	public void draw(Graphics2D b, int x, int y, boolean mouse)
	{
		if (mouse)
		{
			x = x / 32 * 32 - offset % 32;
			y = y / 32 * 32;
			b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			if (animated == true)
			{
				b.drawImage(tileImageAnimated[counter / speed], x, y, null);
			} else
			{
				b.drawImage(tileImage, x, y, null);
			}
			b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		} else
		{
			if (animated == true)
			{
				// The difference is here. If the column is odd, animate it normally. (frame 1, frame 0, frame 1, frame 0)
				if (this.x % 2 == 1)
					b.drawImage(tileImageAnimated[Tile.counter / speed], this.x * TILESIZE + x, this.y * TILESIZE + y, null);
				// If the column is even, animate it at 1 - the frame it would be. Therefore the frames end up being:
				// frame (1-1 = 0), frame (1-0 = 1), frame (1-1 = 0) and so on... (frame 0, frame 1, frame 0, frame 1).
				else
					b.drawImage(tileImageAnimated[1 - (Tile.counter / speed)], this.x * TILESIZE + x, this.y * TILESIZE + y, null);

			} else
			{
				b.drawImage(tileImage, this.x * TILESIZE + x, this.y * TILESIZE + y, null);
			}
		}
	}

	//Almost the exact same as Tile class's draw method.
	@Override
	public void draw(Graphics2D b, int offset)
	{
		Tile.offset = offset;

		if (animated == true)
		{
			// The difference is here. If the column is odd, animate it normally. (frame 1, frame 0, frame 1, frame 0)
			if (x % 2 == 1)
				b.drawImage(tileImageAnimated[Tile.counter / speed], x * TILESIZE - offset, y * TILESIZE, null);
			// If the column is even, animate it at 1 - the frame it would be. Therefore the frames end up being:
			// frame (1-1 = 0), frame (1-0 = 1), frame (1-1 = 0) and so on... (frame 0, frame 1, frame 0, frame 1).
			else
				b.drawImage(tileImageAnimated[1 - (Tile.counter / speed)], x * TILESIZE - offset, y * TILESIZE, null);

		} else
		{
			b.drawImage(tileImage, x * TILESIZE - offset, y * TILESIZE, null);
		}

	}

}
