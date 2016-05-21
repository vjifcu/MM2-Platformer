import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

class Entity
{
	int width, height;
	double speed;
	double x, y, xBG;
	double yv, xv;
	double displayX = 0;
	boolean up, down, left, right, onGround, jumpHeld, canJump;
	boolean collision = true;
	private boolean alive = true;
	int hitCooldown = 0;
	private int health = 1;

	// increments after death, used to animate death[]
	int deathCounter = 0;

	// A death explosion to show that an enemy has been destroyed
	BufferedImage[] death = new BufferedImage[4];

	public Entity(int x, int y, int w, int h, double s)
	{
		this.x = x;
		this.y = y;
		speed = s;
		width = w;
		height = h;
		up = false;
		left = false;
		right = false;

		try
		{
			// Loads death explosion images
			for (int i = 1; i <= 4; i++)
				death[i - 1] = ImageIO.read(new File("images/enemies/death" + i + ".gif"));
		} catch (Exception e)
		{
		}

	}

	public int getHealth()
	{
		return health;
	}

	public boolean getAlive()
	{
		return this.alive;
	}

	public void setAlive(boolean setter)
	{
		alive = setter;
	}

	public int getBG()
	{
		return (int) displayX;
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

	public Rectangle getDisplayBounds()
	{
		return new Rectangle(getX() + (int) displayX, getY(), getWidth(), getHeight());
	}

	public void move()
	{
		xv = 0;
		yv += 1.25;
	}

	public void draw(Graphics2D b, int first, int second)
	{

	}

	public void update()
	{
		x += xv;
		y += yv;
	}

	public void draw(Graphics2D b)
	{
		// Collision box
		b.setColor(Color.PINK);
		b.fillRect(getX() - DrawPanel.player.getX() + 270, getY(), getWidth(), getHeight());
	}


}