import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class MechEnemy extends Entity
{

	int health = 14;
	boolean hit = false;
	int direction;
	int displayDirection;
	int actionCounter = 20;
	boolean alive = true;
	boolean shot = true;
	boolean jumping = false;
	boolean jumped = false;
	int jumpCounter = 0;

	// mechEnemy's normal images
	BufferedImage[][] image = new BufferedImage[2][3];
	// Super bright images to display when hit, acts as a visual indicator that
	// the enemy has been hit.
	BufferedImage[][] imageHit = new BufferedImage[2][3];

	// Bullets for mechEnemy to shoot
	FreeBullet[] bullet = new FreeBullet[5];

	public MechEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);

		direction = (int) s;

		try
		{

			for (int i = 1; i <= 2; i++)
			{
				for (int k = 1; k <= 3; k++)
				{
					// Loads images
					image[i - 1][k - 1] = ImageIO.read(new File("images/enemies/flash man/mech" + i + "_" + k + ".gif"));
					imageHit[i - 1][k - 1] = ImageIO.read(new File("images/enemies/flash man/mech" + i + "_" + k + "_hit.gif"));
				}
			}

			// Initializes bullets
			for (int i = 0; i <= 4; i++)
				bullet[i] = new FreeBullet();

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

	@Override
	public void update()
	{
		super.update();

		// sets displayDirection depending on direction
		if (direction <= 0)
			displayDirection = 0;
		else
			displayDirection = 1;

		// Update bullets
		for (int i = 0; i <= 4; i++)
			bullet[i].update();

		for (int i = 0; i <= 1; i++)
		{
			// If the enemy's bullets hit the player, damage the player by 3
			if (bullet[i].alive == true)
				if (DrawPanel.player.getDisplayBounds().intersects(bullet[i].getBounds()))
					DrawPanel.player.takeDamage(3);
		}

		if (health != 0)
		{
			// If the enemy touches the player, damage the player by 6
			if (getDisplayBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(6);

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					// If the player hits the enemy with a bullet
					if (getDisplayBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
					{
						// Damage the enemy, register hit, play the hit sound, and destroy the bullet
						health--;
						hit = true;
						DrawPanel.playSound("enemyHit.wav");
						DrawPanel.player.bullets[i].alive = false;
					}
			}
		}

	}

	// Shoots the next available bullet, if there is one.
	public void bulletHandler()
	{
		loop: for (int i = 0; i <= 4; i++)
		{
			if (!bullet[i].alive)
			{
				bullet[i].shoot(getX() - DrawPanel.player.getX() + 280 + 32 * direction, getY() + 40, 14 * direction,
						13 + (60 - actionCounter) / 8.0, false);
				break loop;
			}
		}
	}

	@Override
	public void move()
	{
		// gravity
		yv += 1.25;

		if (onGround && !jumping)
		{
			if (jumpCounter > 0)
				jumpCounter--;

			// friction
			xv /= 2;
			// keeps enemy from falling/jumping
			yv = 0;
		} else if (jumpCounter > 0 && jumpCounter < 9 && jumping)
			jumpCounter++;
		else if (jumpCounter == 9 && !jumped)
		{
			// Make the enemy jump
			yv = -14;
			jumped = true;
			// Jump towards the direction it is facing
			if (direction == -1)
				xv = -5.5;
			else
				xv = 5.5;
			
			jumping = false;
			jumpCounter++;
		}

		// Makes the enemy randomly decide what to do (with certain factors affecting likelihoods)
		if (actionCounter == 45)
		{
			double decision = Math.random();

			// Make enemy face player
			if (getBounds().getCenterX() - DrawPanel.player.displayX - DrawPanel.player.getBounds().getCenterX() > 0)
				direction = -1;
			else
				direction = 1;

			// randomly choose to either jump or shoot. Enemy will not shoot two times in a row. likelihood of jumping depends on
			// how close the player is to the enemy. The farther the player is, the more likely it is to jump.
			if (shot || decision < 0.15 + Math.abs(getBounds().getCenterX() - DrawPanel.player.displayX - DrawPanel.player.getBounds().getCenterX()) / 500)
			{
				shot = false;
				jumping = true;
				jumped = false;
				jumpCounter++;

			} else
			{
				shot = true;
				bulletHandler();
			}

		}

		// If actionCounter has reached 80, and the enemy's last move was to jump, reset actionCounter.
		// This makes the enemy start another action.
		if (actionCounter > 80 && !shot)
		{
			actionCounter = 0;
		// If it is shooting, reset actionCounter once it reaches 125 since shooting takes longer to perform.
		} else if (actionCounter > 125)
			actionCounter = 0;
		// If actionCounter is below 125 and the enemy has chosen to shoot as its action, make it shoot.
		else if (shot && actionCounter % 15 == 0 && actionCounter > 45)
		{
			bulletHandler();
		}

		actionCounter++;
	}

	@Override
	public void draw(Graphics2D b)
	{
		// If the enemy is alive
		if (health != 0)
		{
			//Draw super-bright image if recently hit
			if (hit)
				b.drawImage(imageHit[displayDirection][jumpCounter / 5], getX() - DrawPanel.player.getX() + 249, getY() - 12, null);
			//Draw normal image if not recently hit
			else
				b.drawImage(image[displayDirection][jumpCounter / 5], getX() - DrawPanel.player.getX() + 249, getY() - 12, null);
		} else
		{
			// If death explosion isn't finished animating
			if (deathCounter < 6)
			{
				//Draw the death explosion
				b.drawImage(death[deathCounter / 2], getX() - 4 - DrawPanel.player.getX() + 288, getY() + 30, null);
				deathCounter++;
			//Else if the death explosion is finished, declare enemy "officially" dead once the bullets are dead.
			} else if (!bullet[0].alive && !bullet[1].alive)
				alive = false;
		}

		//Reset hit
		hit = false;
		
		//Draw the bullets
		for (int i = 0; i <= 4; i++)
			bullet[i].draw();

	}
}
