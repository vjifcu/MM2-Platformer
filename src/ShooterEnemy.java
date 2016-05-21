import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ShooterEnemy extends Entity
{
	//enemy's bullets
	FreeBullet[] bullet = new FreeBullet[2];
	int numShot = 0;
	int untilNextShot = 0;
	int health = 3;
	boolean hit = false;
	int direction;
	int frame = 0;
	boolean alive = true;
	boolean start = false;

	//Enemy's image
	BufferedImage[][] image = new BufferedImage[2][6];

	public ShooterEnemy(int x, int y, int w, int h, int direction)
	{
		super(x, y, w, h, direction);

		for (int i = 0; i <= 1; i++)
			bullet[i] = new FreeBullet();

		this.direction = -direction;

		try
		{
			//Load enemy's images
			for (int i = 1; i <= 2; i++)
			{
				for (int k = 1; k <= 6; k++)
					image[i - 1][k - 1] = ImageIO.read(new File("images/enemies/flash man/shooter" + i + "_" + k + ".gif"));
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

	//shoots the next available bullet, if there is one
	public void bulletHandler(int numOfShots)
	{
		if (!bullet[0].alive)
			bullet[0].shoot(getX() + 17 - 8 * direction, getY() + 16, (-19 + 14 * (numOfShots / 6)) * direction, -7.5 - 11 * (numOfShots / 6), true);
		else if (!bullet[1].alive)
			bullet[1].shoot(getX() + 19 - 8 * direction, getY() + 20, (-19 + 14 * (numOfShots / 6)) * direction, -7.5 - 11 * (numOfShots / 6), true);
	}

	@Override
	public void update()
	{
		super.update();

		//Updates bullets position
		for (int i = 0; i <= 1; i++)
			bullet[i].update();

		for (int i = 0; i <= 1; i++)
		{
			if (bullet[i].alive == true)
				//If the enemy hits the player with a bullet, damage the player by 3
				if (DrawPanel.player.getDisplayBounds().intersects(bullet[i].getBounds()))
					DrawPanel.player.takeDamage(3);
		}

		//If the enemy comes into the player's view, make it start moving
		if (DrawPanel.player.x > getX() - 280 && DrawPanel.player.x < getX() + getWidth() + 285)
			start = true;
		//If the enemy is out of the player's view, make it dead (it will respawn when it comes back into view)
		else if (start)
		{
			health = 0;
		}

		if (health != 0 && start)
		{
			//If the enemy touches the player, damage the player by 4
			if (getDisplayBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(4);

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					//If the player hits the enemy with a bullet
					if (getDisplayBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
					{
						//Damage the enemy, register the hit, play the hit sound, and destroy the player's bullet
						health--;
						hit = true;
						DrawPanel.playSound("enemyHit.wav");
						DrawPanel.player.bullets[i].alive = false;
					}
			}

			//Increments counters that determine when to shoot and determine frame to draw
			for (int i = 0; i <= 1; i++)
			{
				if (untilNextShot == 21)
				{
					bulletHandler(numShot);
					numShot++;
					untilNextShot = 0;
				}

			}
			untilNextShot++;
			
			//animate enemy depending on number of bullets shot
			if (numShot == 6)
			{
				if (untilNextShot % 4 == 0 && frame < 5)
					frame++;
			} else if (numShot > 11)
			{
				if (untilNextShot % 4 == 0 && frame > 0)
					frame--;
				else if (frame == 0)
					numShot = 0;
			}
		}
	}

	@Override
	public void draw(Graphics2D b)
	{

		//Draw the bullets
		for (int i = 0; i <= 1; i++)
			bullet[i].draw();

		//If the enemy is alive
		if (health != 0)
		{
			//Draw the enemy in the direction it is facing
			if (direction == -1)
				b.drawImage(image[1][frame], getX() - DrawPanel.player.getX() + 264, getY() - 5, null);
			else
				b.drawImage(image[0][frame], getX() - DrawPanel.player.getX() + 264, getY() - 5, null);
		//If the enemy is dead
		} else
		{
			//Draw the death explosion
			if (deathCounter < 6)
			{
				b.drawImage(death[deathCounter / 2], getX() - 4 - DrawPanel.player.getX() + 270, getY() + 4, null);
				deathCounter++;
			} else if (!bullet[0].alive && !bullet[1].alive)
				alive = false;
		}

		hit = false;
	}

}
