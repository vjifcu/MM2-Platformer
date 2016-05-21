import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class BarrelEnemy extends Entity
{

	boolean alive = true;
	int health = 2;

	// start is used so that the enemy does not start moving until it comes into
	// player's view
	boolean start = false;

	double headPosition;

	// Used for moving each body part individually
	int wiggleCounter = 0;

	int counter = 0;
	int moveCounter = 0;

	// Counts for how long enemy is seperated
	int brokenCounter = 0;

	// creates the head and body parts
	BarrelHeadEnemy head;
	BarrelBodyEnemy[] body;

	public BarrelEnemy(int x, int y, int w, int h, double s)
	{
		super(x - 50, y, w, h, s);

		// creates head
		head = new BarrelHeadEnemy(x, y, w, h, s);

		// creates 3 body parts
		body = new BarrelBodyEnemy[3];
		for (int i = 0; i < 3; i++)
			body[i] = new BarrelBodyEnemy(x, y, w, h, s);
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

	// Increments counters used for wiggling body parts
	public void wiggle()
	{
		if (counter < 71)
			counter++;
		else
			counter = 0;

		if (counter % 24 <= 11)
			moveCounter++;
		else
			moveCounter--;
	}

	@Override
	public void update()
	{
		// If the head dies, the whole enemy dies
		if (!head.alive)
			alive = false;
		// Increment the wiggle counters
		wiggle();
	}

	@Override
	public void move()
	{
		// Start the enemy's movement when it comes into player's view
		if (DrawPanel.player.x > head.x - 240)
			start = true;
		// If the enemy is started and the head is alive
		if (start && head.health != 0)
		{
			// If the enemy is arranged normally
			if (!head.assembling)
			{
				// move the bottom body part left
				body[2].move();
				body[2].xv = -1;

				Level.getCurrentRoom().checkCollisions(body[2]);
				body[2].update((moveCounter - 5) * -0.2);

				// Arrange the above body parts, based on the bottom body part's position is
				body[1].y = body[2].y - 32;
				head.y = body[1].y - 32;
				body[0].y = head.y - 32;

				// Move the bodies
				for (int i = 0; i < 2; i++)
				{
					body[i].xv = body[2].xv;
					body[i].update((moveCounter - 5) * 0.2);
				}

				// Move the head to the proper position
				head.xv = body[2].xv;
				head.update((moveCounter - 5) * -0.2);

			// if the enemy was recently disassembled
			} else if (head.assembling && brokenCounter < 79)
			{

				for (int i = 0; i < 3; i++)
				{
					body[i].move();
					head.move();
					Level.getCurrentRoom().checkCollisions(head);
					head.update(0);

					if (!body[i].shotUp)
					{
						// Make the body parts destroyable
						body[i].weak = true;

						body[i].yv -= 14;
						body[i].shotUp = true;
					}

					// shoot the body parts forward
					body[i].xv = -9 + i * 3;

					// Check body parts collision
					for (int k = 0; k <= 1; k++)
					{
						try
						{
							if (Level.getCurrentRoom().tiles[(body[i].getX() + body[i].getWidth() * k) / 32][(body[i].getY() + body[i].height + 8) / 32].background == false)
								body[i].xv = 0;
						} catch (Exception e)
						{
						}
					}
					Level.getCurrentRoom().checkCollisions(body[i]);

					// update body parts position based on velocity ran through collision
					body[i].update(0);
				}
				brokenCounter++;
			// If the enemy is ready to put itself together again
			} else if (brokenCounter == 79)
			{
				head.assembling = true;

				// start the body parts from below screen
				body[2].y = 480 + 128;
				headPosition = head.y;

				// arrange body parts below topmost body part
				for (int i = 0; i < 3; i++)
				{
					body[i].y = body[2].y - i * 32;
					body[i].x = head.x;
					body[i].xv = 0;
					body[i].shotUp = false;
				}
				brokenCounter++;
			// If enemy is putting itself together
			} else if (brokenCounter == 80)
			{
				for (int i = 0; i < 3; i++)
				{
					// set body parts alive (in case they were destroyed
					body[i].health = 1;
					body[i].alive = true;
					// make body parts invulnerable again
					body[i].weak = false;
				}
				// Move the body parts up towards the head
				body[2].y -= 18;
				body[1].y = body[2].y - 32;
				body[0].y = body[1].y - 64;

				// update body part positions
				for (int i = 0; i < 3; i++)
					body[i].update(0);
				// update head position
				head.update(0);

				// If the body parts have reached the head
				if (body[0].y + 32 < head.y)
					// Raise the head to the proper position below the top body part
					head.y = body[0].y + 32;

				if (body[0].y + 96 <= headPosition)
				{
					head.assembling = false;
					brokenCounter = 0;
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D b)
	{
		if (head.health != 0)
			for (int i = 0; i < 3; i++)
				body[i].draw(b);
		head.draw(b);
	}

}

// enemy's head
class BarrelHeadEnemy extends Entity
{
	int counter = 0;

	boolean shotUp = false;
	boolean assembling = false;

	int health = 2;
	boolean hit = false;
	boolean alive = true;

	// head image
	BufferedImage image;

	public BarrelHeadEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);

		try
		{
			// load head image
			image = ImageIO.read(new File("images/enemies/metal man/barrelHead.gif"));
		} catch (Exception e)
		{
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

	public Rectangle getWeakSpot()
	{
		return new Rectangle(getX(), getY() + 10, width, 14);
	}

	@Override
	public void move()
	{
		xv = 0;
		yv += 0.75;
	}

	public void update(double counter)
	{
		// update enemy only when it is alive
		if (health != 0)
		{
			x += counter;

			super.update();

			// If enemy touches player
			if (getDisplayBounds().intersects(DrawPanel.player.getDisplayBounds()))
				// damage player by 3
				DrawPanel.player.takeDamage(3);

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					// If player hits the enemy's weak spot (eyes)
					if (getWeakSpot().intersects(DrawPanel.player.bullets[i].getBounds()) && !assembling)
					{
						// damage enemy, register hit, play sound, disassemble
						// if hit for first time, and delete player's bullet
						health--;
						hit = true;
						DrawPanel.playSound("enemyHit.wav");
						if (health != 0)
							assembling = true;
						DrawPanel.player.bullets[i].alive = false;
					// If the player misses the weak spot
					} else if (getDisplayBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
						// deflect the player's bullet
						DrawPanel.player.bullets[i].deflected = true;
			}
		}

	}

	@Override
	public void draw(Graphics2D b)
	{
		// Draw head if the enemy is still alive
		if (health != 0)
			b.drawImage(image, (int) (x - DrawPanel.player.getX() + 270), (int) y, null);
		// If the enemy is dead
		else
		{
			// Display a small death explosion
			b.drawImage(death[deathCounter / 2], getX() - 4 - DrawPanel.player.getX() + 270, getY() + 4, null);
			// Continue displaying death explosion animation until deathcounter = 6
			if (deathCounter < 6)
				deathCounter++;
			// Once deathcounter reaches 6, the enemy is destroyed for good
			else
				alive = false;
		}

		hit = false;
	}

}

//enemy's body parts
class BarrelBodyEnemy extends Entity
{
	int counter = 0;

	boolean shotUp = false;

	// If false, cannot be damaged
	// If true, can be damaged
	boolean weak = false;

	int health = 1;
	boolean hit = false;
	boolean alive = true;

	//body part image
	BufferedImage image;

	public BarrelBodyEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);

		try
		{
			//loads body part image
			image = ImageIO.read(new File("images/enemies/metal man/barrelBody.gif"));
		} catch (Exception e)
		{
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

	public void update(double counter)
	{
		x += counter;

		super.update();

		// If body part is alive
		if (health != 0)
		{
			// If body part touches enemy, damage enemy by 3
			if (getDisplayBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(3);

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					// If player hits body part with bullet
					if (getBounds().intersects(DrawPanel.player.bullets[i].getBounds()))
					{
						// If not weak, deflect the bullet
						if (!weak)
							DrawPanel.player.bullets[i].deflected = true;
						// If weak, take damage, register hit, and destroy the bullet
						else
						{
							health--;
							hit = true;
							DrawPanel.player.bullets[i].alive = false;
						}
					}
			}
		}

	}

	@Override
	public void draw(Graphics2D b)
	{
		// If body part is alive, draw it
		if (health != 0)
			b.drawImage(image, (int) (x - DrawPanel.player.getX() + 270), (int) y, null);
		// If body part is dead
		else if (deathCounter < 6)
		{
			//Draw death explosion
			b.drawImage(death[deathCounter / 2], getX() - DrawPanel.player.getX() + 270, getY(), null);
			deathCounter++;
		}

		hit = false;
	}

}