import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class DrillEnemy extends Entity
{
	public Player player = DrawPanel.player;
	String fileString = "images/enemies/metal man/drill";
	
	// Drill images
	BufferedImage[] image = new BufferedImage[4];
	
	// Super bright image to display when hit, acts as a visual indicator that the enemy has been hit.
	BufferedImage[] hitImage = new BufferedImage[2];
	int counter = 0;
	int direction = -1;
	boolean alive = false;
	boolean hit = false;
	int delay = 0;

	int health = 2;

	public DrillEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);
		try
		{
			//Loads drill images
			for (int i = 1; i <= 4; i++)
				image[i - 1] = ImageIO.read(new File(fileString + i + ".gif"));
			//Loads hit images
			for (int i = 1; i <= 2; i++)
				hitImage[i - 1] = ImageIO.read(new File(fileString + "hit" + i + ".gif"));
		} catch (Exception e)
		{
		}
		// Make the enemy not be affected by collision with tiles
		collision = false;
	}

	@Override
	public void update()
	{
		// Update enemy if it is alive
		if (health != 0)
		{
			super.update();

			if (alive)
			{
				// If it touches the player, damage the player by 2
				if (getBounds().intersects(player.getBounds()))
					player.takeDamage(2);

				for (int i = 0; i <= 2; i++)
				{
					if (player.bullets[i].alive == true)
						// If the player hits the enemy with a bullet
						if (getBounds().intersects(player.bullets[i].getBounds()))
						{
							// Damage the enemy, register the hit, play the hit sound, and destroy the bullet
							health--;
							hit = true;
							DrawPanel.playSound("enemyHit.wav");
							player.bullets[i].alive = false;
						}
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D b)
	{
		if (alive)
		{
			if (health != 0)
			{
				// if moving downwards
				if (direction == 1)
				{
					if (hit)
						// Display the super-bright hit image, pointing downwards
						b.drawImage(hitImage[1], getX() - (image[0].getWidth() - width) / 2 - player.getX() + 270, getY() - 4, null);
					else
						//Display the normal image, pointing downwards
						b.drawImage(image[(counter / 4) + 2], getX() - (image[0].getWidth() - width) / 2 - player.getX() + 270, getY() - 4, null);
				// if moving upwards
				} else
				{
					if (hit)
						//Display the super-bright hit image, pointing upwards
						b.drawImage(hitImage[0], getX() - (image[0].getWidth() - width) / 2 - player.getX() + 270, getY() - 6, null);
					else
						//Display the normal image, pointing upwards
						b.drawImage(image[(counter / 4)], getX() - (image[0].getWidth() - width) / 2 - player.getX() + 270, getY() - 6, null);
				}
				
				// Increments counter. Counter is used to animate the normal images
				if (counter < 7)
					counter++;
				else
					counter = 0;
			// If the enemy is dead
			} else
			{
				// Draw the death explosion
				b.drawImage(death[deathCounter / 2], getX() - 6 - DrawPanel.player.getX() + 270, getY() + 4, null);
				
				//Once the death explosion is finished, set the enemy "officially" dead
				if (deathCounter < 6)
					deathCounter++;
				else
					alive = false;
			}
		}

		//Resets hit variable so the bright image will only be drawn for a split-second.
		// Also gets it ready for the next shot.
		hit = false;

	}

	// Returns a random double from the range given
	public double randDouble(int start, int end)
	{
		return ((Math.random() * (end - start)) + start);
	}

	//Gives the drill enemy a random direction to start with
	public int randDir()
	{
		if (randDouble(-1, 1) > -0.05)
			return direction = 1;
		else
			return direction = -1;
	}

	@Override
	public void move()
	{
		// If the delay has finished counting down
		if (delay == 0)
		{
			// If the enemy is alive, move it up or down depending on direction
			if (alive)
				yv = speed * direction;

			// If the drill has gone off screen
			if ((y >= DrawPanel.HEIGHT && direction == 1) || (y <= 0 && direction == -1))
			{
				// If the player is within the area the drill enemys should spawn in
				if (player.getX() > 2250 && player.getX() < 3100)
				{
					//make the enemies alive
					alive = true;
					health = 2;
					
					//randomly choose up or down as their direction
					randDir();
					
					//Put them high or low on the screen, hidden behind the tiles
					y = 270 - (185 * direction);
					// Move their X relative to the player, so they spawn nearby
					x = player.getX() + randDouble(-100, 600);
					
					//set their respawn delay
					delay = (int) randDouble(0, 175);
				} else
					alive = false;
			}
		} else
			delay--;

	}

}
