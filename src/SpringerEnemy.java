import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Enemy that sweeps along the floor
public class SpringerEnemy extends Entity
{

	int counter = 0;
	int direction;
	int health = 1;
	boolean alive = true;
	boolean hit = false;
	boolean start = false;

	int imageCounter = 1;
	int imageCounter2 = 0;
	int counterDirection = 1;

	//Enemy image
	BufferedImage image[][] = new BufferedImage[2][3];

	public SpringerEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, 32, 30, s);
		direction = w;
		speed = 0.8;
		try
		{
			//Loads enemy image
			for (int i = 1; i <= 2; i++)
			{
				for (int k = 1; k <= 3; k++)
					image[i - 1][k - 1] = ImageIO.read(new File("images/enemies/metal man/springer" + i + "_" + k + ".gif"));
			}

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
	}

	@Override
	public int getHealth()
	{
		return this.health;
	}

	@Override
	public void update()
	{
		//If the enemy is alive
		if (health != 0)
		{
			super.update();
			
			//If the enemy touches the player, damage the player and set the counter to 100
			if (getBounds().intersects(DrawPanel.player.getBounds()))
			{
				DrawPanel.player.takeDamage(2);
				if (counter == 0)
					counter = 100;
			}

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					//If the player shoots a bullet at the enemy, deflect the bullet
					if (getDisplayBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
						DrawPanel.player.bullets[i].deflected = true;
			}
		}
	}

	@Override
	public void move()
	{
		//If the enemy comes into the player's view, make it start moving
		if (!start && DrawPanel.player.getX() > getX() - 240)
			start = true;

		super.move();

		if (start)
		{
			if (counter == 0)
			{
				//If moving left
				if (direction == -1)
				{
					//If the enemy has not hit a wall or the end of a ledge
					if (Level.getCurrentRoom().tiles[(getX()) / 32][(getY() + getHeight() + 8) / 32].background == false && Level.getCurrentRoom().tiles[(getX() - 2) / 32][getY() / 32].background == true)
					{
						//If the player is at the same height as the enemy, move very quickly
						if (DrawPanel.player.getY() + DrawPanel.player.getHeight() > getY() && DrawPanel.player.getY() + DrawPanel.player.getHeight() < getY() + getHeight() + 10)
							xv = -speed * 7;
						//If the player is not at the same height, move slowly
						else
							xv = -speed;
					//If the enemy has hit a wall or reached the end of the ledge, go in the opposite direction
					} else
					{
						direction = 1;
					}
				} else if (direction == 1)
				{
					//If the enemy has not hit a wall or the end of a ledge
					if (Level.getCurrentRoom().tiles[(getX() + width) / 32][(getY() + getHeight() + 8) / 32].background == false && Level.getCurrentRoom().tiles[(getX() + getWidth() + 2) / 32][getY() / 32].background == true)
					{
						//If the player is at the same height as the enemy, move very quickly
						if (DrawPanel.player.getY() + DrawPanel.player.getHeight() > getY() && DrawPanel.player.getY() + DrawPanel.player.getHeight() < getY() + getHeight() + 10)
							xv = speed * 7;
						//If the player is not at the same height, move slowly
						else
							xv = speed;
					//If the enemy has hit a wall or reached the end of the ledge, go in the opposite direction
					} else
						direction = -1;
				}

			}
			if (counter > 0)
				counter--;
		}

	}

	@Override
	public void draw(Graphics2D b)
	{
		//If enemy has not recently hit the player
		if (counter == 0)
		{
			//Draw enemy normally, facing in the direction it should be.
			if (direction == 1)
				b.drawImage(image[0][0], getX() - DrawPanel.player.getX() + 254, getY() - 33, null);
			else
				b.drawImage(image[1][0], getX() - DrawPanel.player.getX() + 254, getY() - 33, null);
		//If the enemy has recently hit the player
		}
		else
			//Draw it doing a taunting springing animation. Animated using imageCounter and imageCounter2
			b.drawImage(image[imageCounter2][imageCounter/2], getX() - DrawPanel.player.getX() + 254, getY() - 33, null);

		//Increments counters for the taunting animation
		imageCounter += counterDirection;
		if (imageCounter == 5 && counterDirection == 1)
			counterDirection = -1;
		else if (imageCounter == 0 && counterDirection == -1)
		{
			if (imageCounter2 == 0)
				imageCounter2 = 1;
			else
				imageCounter2 = 0;
			counterDirection = 1;
		}

	}
}
