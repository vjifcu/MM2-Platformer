import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//A bullet that moves "freely", which means it can be shot in
// any direction, as opposed to the Bullet class, which always
// moves horizontally with no gravity.
public class FreeBullet
{
	double x, y, xv, yv;
	int width = 14;
	int height = 10;
	boolean alive = false;
	int direction;
	boolean gravity;
	
	// bullet image
	BufferedImage image;

	boolean deflected = false;

	public FreeBullet()
	{
		try
		{
			// Loads bullet images
			image = ImageIO.read(new File("images/megaman/bullet.gif"));
		} catch (IOException e)
		{
		}
	}

	//Shoots the bullet from origin specified, with the velocities specificied, with or without gravity
	public void shoot(int x, int y, double xv, double yv, boolean gravity)
	{
		if (!alive)
		{
			// Shooting sound for enemies
			DrawPanel.playSound("enemyShoot.wav");
			
			// sets bullet variables
			this.gravity = gravity;
			this.x = x;
			this.y = y;
			this.xv = xv;
			this.yv = yv;
			
			// Sets alive to true, which enables the bullet to update
			alive = true;
		}
	}

	public void update()
	{
		if (alive)
		{
			// Update x and y according to velocities
			x += xv;
			y += yv;
			
			// If gravity is true
			if (gravity)
			{
				// gravity also adds "wind resistance" to look more realistic
				//decrease x velocity slightly
				xv /= 1.02;
				// increase y velocity to make bullet fall
				yv += 1.25;
			}
		}
		//If bullet goes offscreen, set it to be dead
		if (x - DrawPanel.player.getX() + 270 > DrawPanel.WIDTH || x - DrawPanel.player.getX() + 270 < 0 || y > DrawPanel.HEIGHT)
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

	// Old bounding box code. Kept since some older enemies/methods referenced it
	// and did not cause any noticeable issues.
	public Rectangle getBounds()
	{
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public void draw()
	{
		if (alive)
		{
			DrawPanel.b.drawImage(image, (int) (x - DrawPanel.player.getX() + 270), getY() - 1, null);
		}
	}

	// New bounding box code. This code takes into account the player's movement near the
	// edge of a level where the player is unlocked from the center of the screen.
	public Rectangle getDisplayBounds()
	{
		return new Rectangle((int) (x - DrawPanel.player.getX() + 270), getY(), getWidth(), getHeight());
	}

}
