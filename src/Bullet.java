import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bullet
{
	int x;
	int y;
	int width = 14;
	int height = 10;
	boolean alive = false;
	int direction;
	// image for bullet
	BufferedImage image;
	// true when the deflected sound should be played
	boolean deflectedSound = true;

	// If true, the bullet gets deflected
	boolean deflected = false;

	public Bullet()
	{
		try
		{
			// Load bullet Image
			image = ImageIO.read(new File("images/megaman/bullet.gif"));
		} catch (IOException e)
		{
		}
	}

	public void shoot(int x, int y, int direction)
	{
		if (!alive)
		{
			// Prepares bullet to play deflected sound
			deflectedSound = true;

			// determines direction shot
			if (direction == 0)
				this.direction = 1;
			else
				this.direction = -1;

			// Shoots bullet in direction determined
			this.x = (int) (x + 10 * this.direction + DrawPanel.player.displayX);
			this.y = y + 11;

			// Resets deflected variable in case last shot bullet was deflected
			deflected = false;

			// Sets bullet alive
			alive = true;
			DrawPanel.player.shootCounter = 15;
		}
	}

	public void update()
	{
		if (alive)
		{
			if (deflected)
			{
				if (deflectedSound)
				{
					DrawPanel.playSound("bulletDeflected.wav");
					deflectedSound = false;
				}
				// Move bullet left and up if deflected
				x -= 18;
				y -= 12;
				// If bullet goes off screen, make it dead
				if (y < 0)
					alive = false;
			} else
				// Move bullet normally if not deflected
				x += direction * 15;
		}

		// If bullet goes off screen, make it dead
		if (x - 1 - DrawPanel.player.getX() + 270 > DrawPanel.WIDTH || x - 1 - DrawPanel.player.getX() + 270 < 0)
			alive = false;
	}

	public int getX()
	{
		return (int) x;
	}

	public int getY()
	{
		return (int) y;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Rectangle getBounds()
	{
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public void draw()
	{
		//Draw bullet when it is alive
		if (alive)
			DrawPanel.b.drawImage(image, (int) (x - 1 - DrawPanel.player.getX() + 270), y - 1, null);
	}

}
