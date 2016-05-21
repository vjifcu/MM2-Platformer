import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Clown Class. Is one half of the cog clown enemy. Controlled by the main CogClownEnemy class
public class ClownEnemy extends Entity
{

	boolean jumpFinished = false;
	boolean[] jumped =
	{ false, false, false };
	public int health = 1;
	public boolean alive = true;
	boolean hit = false;

	//Clown's images
	BufferedImage[] image = new BufferedImage[2];
	int counter = 0;

	public ClownEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);

		try
		{

			//Loads clown's images
			for (int i = 1; i <= 2; i++)
				image[i - 1] = ImageIO.read(new File("images/enemies/metal man/clown" + i + ".gif"));

		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void draw(Graphics2D b)
	{
		// If alive, draw clown
		if (health != 0)
			b.drawImage(image[counter / 5], getX() - (image[0].getWidth() - width) / 2 - DrawPanel.player.getX() + 270, getY() - 12, null);
		// If not alive
		else
		{
			//Draw death explosion
			b.drawImage(death[deathCounter / 2], getX() + 4 - DrawPanel.player.getX() + 270, getY() + 6, null);
			
			// Once death explosion is done being displayed, make enemy "officially" dead
			if (deathCounter < 6)
				deathCounter++;
			else
				alive = false;
		}

		//Used to alternate between two clown images to display
		if (counter < 9)
			counter++;
		else
			counter = 0;
	}

	//Makes the clown jump on the cog wheel before dropping down
	public void jump(double destination)
	{

		//If clown is below the "destination" (cog wheel) and it is falling
		jumpChecker: if (y + height + yv >= destination && yv > 0)
		{
			for (int i = 0; i <= 2; i++)
			{
				// jumped is an array of 3 booleans because the clown needs to jump 3 times.
				// Probably not the most efficient/best way to do it, but it works...
				if (!jumped[i])
				{
					//jump up
					yv = -7;
					jumped[i] = true;
					break jumpChecker;
				}
			}

			jumpFinished = true;

		}
	}

	@Override
	public void update()
	{
		// If the clown is alive, update it
		if (health != 0)
		{
			// update the x any y according to velocities (already run through collision)
			super.update();

			// If the enemy touches the player, damage the player by 5 which is actually a lot but oh well don't touch the clown next time XD
			if (getDisplayBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(5);

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					// If the player's bullets hits the enemy
					if (getDisplayBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
					{
						//Damage enemy, register hit, play hit sound, and destroy the player's bullet
						health--;
						hit = true;
						DrawPanel.playSound("enemyHit.wav");
						DrawPanel.player.bullets[i].alive = false;
					}
			}
			// If the enemy falls off screen, kill it
			if (y > DrawPanel.HEIGHT)
				alive = false;
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
		if (!alive)
			health = 0;
	}

	@Override
	public int getHealth()
	{
		return this.health;
	}

}
