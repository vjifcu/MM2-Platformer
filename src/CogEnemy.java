import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Cog Class. Is one half of the cog clown enemy. Controlled by the main CogClownEnemy class
public class CogEnemy extends Entity
{
	int counter = 0;

	int health = 3;
	boolean hit = false;
	boolean alive = true;

	// Cog images
	BufferedImage[] image = new BufferedImage[2];
	// Super bright image displayed when hit, acts as a visual indicator
	// that the enemy has been hit.
	BufferedImage hitImage;

	public CogEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);

		try
		{

			// Load images
			for (int i = 1; i <= 2; i++)
				image[i - 1] = ImageIO.read(new File("images/enemies/metal man/cog" + i + ".gif"));
			hitImage = ImageIO.read(new File("images/enemies/metal man/cogHit.gif"));

		} catch (IOException e)
		{
			e.printStackTrace();
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

	@Override
	public void update()
	{
		// Update if enemy is alive
		if (health != 0)
		{
			// update the x any y according to velocities (already run through collision)
			super.update();

			// If the player hits the cog wheel, damage the player by 4
			if (getDisplayBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(4);

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
				{
					// If the player hits the cog wheel with a bullet
					if (getDisplayBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
					{
						// Damage cog wheel, register hit, play hit sound, and delete player bullet
						health--;
						hit = true;
						DrawPanel.playSound("enemyHit.wav");
						DrawPanel.player.bullets[i].alive = false;
					}
				}
			}
		}

	}

	@Override
	public void draw(Graphics2D b)
	{
		// Draws collision box
		// super.draw(b);

		//If the enemy just got hit, display the super-bright image
		if (hit)
			b.drawImage(hitImage, getX() - (image[0].getWidth() - width) / 2 - DrawPanel.player.getX() + 270, getY() - 6, null);
		// If the enemy was not just hit and is still alive, draw the enemy
		else if (health != 0)
			b.drawImage(image[counter / 5], getX() - (image[0].getWidth() - width) / 2 - DrawPanel.player.getX() + 270, getY() - 6, null);
		// If the enemy has just died
		else
		{
			// Draw death explosion
			b.drawImage(death[deathCounter / 2], getX() + 6 - DrawPanel.player.getX() + 270, getY() + 6, null);
			//If death explosion animation is finished, set enemy as "officially" dead
			if (deathCounter < 6)
				deathCounter++;
			else
				alive = false;
		}

		hit = false;

		// Used for alternating between two cog images
		if (counter < 9)
			counter++;
		else
			counter = 0;
	}

}
