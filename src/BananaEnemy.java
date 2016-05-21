import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class BananaEnemy extends Entity
{

	// Enemy projectiles
	Banana[] bananas = new Banana[3];
	int untilNextBanana = 0;
	int health = 1;
	int frame = 0;
	boolean alive = true;

	BufferedImage image;

	public BananaEnemy(int x, int y, int w, int h, int s)
	{
		super(x + 16, y, w, h, s);

		try
		{
			// create the projectiles
			for (int i = 0; i < 3; i++)
				bananas[i] = new Banana();

			for (int i = 1; i <= 2; i++)
			{
				// load enemy image
				image = ImageIO.read(new File("images/enemies/flash man/bananaEnemy.gif"));
			}

		} catch (Exception e)
		{
			System.out.println(e);
		}

	}

	@Override
	public boolean getAlive()
	{
		return this.alive;
	}

	@Override
	public void setAlive(boolean setter)
	{
		alive = setter;
	}

	@Override
	public int getHealth()
	{
		return this.health;
	}

	// Shoots the next available projectile, if one is available (not alive)
	public void bananaHandler()
	{
		outerLoop: for (int i = 0; i < 3; i++)
		{
			if (!bananas[i].getAlive())
			{
				bananas[i].shoot(getX() - DrawPanel.player.getX() + 264, getY(), Math.random() * 6 - 3, -10);
				untilNextBanana = 0;
				break outerLoop;
			}
		}

	}

	// Move and check projectile's collision since it belongs to this class 
	// and won't be checked automatically by the current room
	@Override
	public void move()
	{
		super.move();
		for (int i = 0; i < 3; i++)
		{
			bananas[i].move();
			Level.checkCollisions(bananas[i]);
		}
	}

	@Override
	public void update()
	{
		super.update();

		if (health != 0)
		{
			// If enemy touches player, player takes 4 damage
			if (getDisplayBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(4);

			// If it is time for the next projectile, shoot it (requires an available projectile)
			for (int i = 0; i <= 1; i++)
			{
				if (untilNextBanana > 35)
				{
					bananaHandler();
				}

			}
			untilNextBanana++;

		}

		// Update projectiles
		for (int i = 0; i < 3; i++)
			bananas[i].update();

		// If all projectiles are destroyed, and the main body is already destroyed, the entire enemy officially dies
		outerIf: if (deathCounter == 6)
		{
			for (int i = 0; i < 3; i++)
			{
				if (!bananas[i].getAlive())
					break outerIf;
			}
			alive = false;
		}

	}

	@Override
	public void draw(Graphics2D b)
	{

		super.draw(b);

		b.drawImage(image, getX() - DrawPanel.player.getX() + 264, getY() - 5, null);

		for (int i = 0; i < 3; i++)
			bananas[i].draw(b);

	}
}

// BananaEnemy's projectile
class Banana extends Entity
{

	BufferedImage[] image = new BufferedImage[2];
	boolean alive = false;
	int health = 1;
	int timeAlive = 0;
	int deathTime = 0;
	double sideMotion = 0;
	boolean hit = false;
	int timer = 0;

	public Banana()
	{
		super(0, 0, 14, 30, 0);
		alive = false;
		try
		{
			// Load two images for the projectile
			for (int i = 1; i <= 2; i++)
				image[i - 1] = ImageIO.read(new File("images/enemies/flash man/banana" + i + ".gif"));
		} catch (Exception e)
		{
			System.out.println(e);
		}

	}

	@Override
	public boolean getAlive()
	{
		return alive;
	}

	@Override
	public void setAlive(boolean setter)
	{
		this.alive = setter;
	}

	@Override
	public int getHealth()
	{
		return this.health;
	}

	@Override
	public void move()
	{
		// Gravity
		yv += 1.25;

		if (onGround && yv > 0)
			xv = 0;
	}

	@Override
	public void update()
	{
		// Add the velocities to the position (after being run through collision)
		y += yv;
		x += xv;

		timeAlive++;

		if (health != 0)
		{

			// Checks if player's bullets have collided with projectile
			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					// If they have, subtract from projectile health, register the hit, play the hit sound, and get rid of the bullet
					if (getDisplayBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
					{
						health--;
						hit = true;
						DrawPanel.playSound("enemyHit.wav");
						DrawPanel.player.bullets[i].alive = false;
					}
			}

			
			for (int i = 0; i <= 1; i++)
			{
				// If the player collides with the projectile
				if (DrawPanel.player.getDisplayBounds().intersects(getDisplayBounds()))
					// Damage the player by 3
					DrawPanel.player.takeDamage(3);
			}
		}

		if (deathCounter == 6)
			setAlive(false);

		// If the projectile is alive for longer than deathTime or its helath reaches 0, set alive to false
		if (timeAlive > deathTime || health == 0)
			setAlive(false);
		
		// Increment the counter for displaying the two projectile images
		if (timer < 10)
			timer++;
		else
			timer = 0;
	}

	// Sets all variables to have the projectile shoot out from the main enemy
	public void shoot(int x, int y, double xv, double yv)
	{
		setAlive(true);
		timeAlive = 0;
		timer = 0;
		health = 2;
		deathTime = (int) (Math.random() * 200) + 150;
		this.x = x;
		this.y = y;
		sideMotion = xv;
		this.xv = xv;
		this.yv = yv;
	}

	@Override
	public void draw(Graphics2D b)
	{
		if (alive && !hit)
		{
			// Using timer, alternate between the two projectile images rapidly
			b.drawImage(image[timer/6], getX() - 4, getY(), null);
		}
		hit = false;
	}

}
