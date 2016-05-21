import java.awt.AlphaComposite;
import java.awt.Graphics2D;

//A slippery tile which has reduced friction
public class IceTile extends Tile
{
	static int iceCounter;
	int counterOffset;
	int tempCounter;
	char realID;

	static double xvPrevious = 0;

	public IceTile(int x, int y, char image)
	{
		//code rounds down to the nearest 3rd number starting at 1, since there are 3
		// copies of each tile, but with the bright image appearing at different times.
		//ex. tile 2 would have the image for tile 1, but the flash would occur at a
		// different time than tile 1
		super(x, y, (char) (34 + ((image - 34) / 3) * 3));
		
		//realID is kept to differentiate between the 3 copies of the same image tile
		realID = image;
		
		//counterOffset determines when the flashes occur.
		counterOffset = 2 * (((int) image + 2) % 3);

	}

	// Overridden getID() to return realID instead of the default tileID to ensure
	// the 3 copies of the same tile can be distinguished between.
	@Override
	public char getID()
	{
		return realID;
	}

	//Draws tile at the location specified
	@Override
	public void draw(Graphics2D b, int x, int y, boolean mouse)
	{
		// If the tile is going to be placed at the mouse for use during level construction
		if (mouse)
		{
			//snap it to the grid
			x = x / 32 * 32 - offset % 32;
			y = y / 32 * 32;
			//set it to 50% opacity
			b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			// If it is animated, show the animation
			if (animated == true)
			{
				b.drawImage(tileImageAnimated[counter / speed], x, y, null);
			// If it is not animated, show the static image
			} else
			{
				b.drawImage(tileImage, x, y, null);
			}
			// Once completed, set opacity back to 100%
			b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		// If the tile is part of the level but needs to be drawn in a specific location
		// (such as when changing rooms)
		} else
		{
			//Draw the tile normally
			b.drawImage(tileImageAnimated[((iceCounter + counterOffset) % 6) / 4], this.x * TILESIZE + x, this.y * TILESIZE + y, null);
		}
	}

	// Default draw() used for drawing the level normally
	@Override
	public void draw(Graphics2D b, int offset)
	{
		if (!DrawPanel.player.onGround)
			xvPrevious = 0;
		Tile.offset = offset;

		b.drawImage(tileImageAnimated[((iceCounter + counterOffset) % 6) / 4], x * TILESIZE - offset, y * TILESIZE, null);

	}

	//Handles the player sliding on the tile's surface
	@Override
	public void groundCollision(Entity entity)
	{
		// If the entity on top of the tile is the player
		if (entity instanceof Player)
		{
			// And the player is not in the hurt animation
			if (DrawPanel.player.hitCooldown <= 80)
			{
				//If the player is moving with the motion of their slide, increase their x velocity
				if ((DrawPanel.player.xv > 0 && xvPrevious > 0) || (DrawPanel.player.xv < 0 && xvPrevious < 0))
					xvPrevious += DrawPanel.player.xv / 3.0;

				// If their movement is going to be less than a pixel, set it to 0.
				// this prevents weird scaling of the player due to their pixels not aligning
				// with the screen after it is scaled up to fit the user's monitor.
				if (Math.abs(xvPrevious) < 1)
					xvPrevious = 0;
				// Caps the movement off at the player's max speed
				else if (xvPrevious > DrawPanel.player.speed)
					xvPrevious = DrawPanel.player.speed;
				// Caps the movement off again, this time in the negative
				else if (xvPrevious < -DrawPanel.player.speed)
					xvPrevious = -DrawPanel.player.speed;
				// during normal sliding, slowly decrease player's speed as opposed to fully stopping it
				// This creates the sliding motion.
				else
					xvPrevious /= 1.035;

				// Now set the player's x velocity to xvPrevious (Collision checking is done afterwards, so no worries)
				if (xvPrevious != 0)
					DrawPanel.player.xv = xvPrevious;

				//set xvPrevious to the player's x velocity, getting it ready for the next loop
				xvPrevious = DrawPanel.player.xv;
			} else
				xvPrevious = 0;
		}

	}

}
