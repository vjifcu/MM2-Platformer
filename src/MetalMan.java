import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Okay so this guy is like a lot of code and the comments would be
// horrendous to make so I'm just gonna be pretty general here okay kthx XD
public class MetalMan extends Entity
{
	// Colors for metal man's health bar
	Color health1 = new Color(160, 0, 0);
	Color health2 = new Color(250, 100, 100);

	static int health = 28;
	//Used for animating his health bar rising at the beginning
	double displayHealth = 0;
	boolean alive = true;

	boolean start = false;

	int numBlades;
	int totalBlades;
	int throwCounter = 0;

	boolean drawn = false;
	boolean hit = false;
	int hitCooldown = 0;

	int counter = 0;

	double jumpX = 0;
	boolean jump = false;

	int wait = 0;

	//How long to wait before changing the direction of the conveyor belt tiles
	int conveyorCounter = (int) randDouble(150, 350);
	char currentConveyorTile = '7';

	// Metal man's metal blades. up to 4 on the screen at a time.
	MetalBlade metalBlades[] = new MetalBlade[4];

	int direction = -1;
	
	//Metal man's images
	BufferedImage[][] image = new BufferedImage[10][2];
	//His "get hit" images
	BufferedImage hitImage;

	public MetalMan(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);
		
		health = 28;

		for (int i = 1; i <= 10; i++)
		{
			for (int k = 1; k <= 2; k++)
			{
				try
				{
					//loads images
					image[i - 1][k - 1] = ImageIO.read(new File("images/enemies/metal man/metal" + i + "_" + k + ".gif"));
					hitImage = ImageIO.read(new File("images/enemies/metal man/metal_hit.gif"));
				} catch (Exception e)
				{
				}
			}
		}
		//Initialize his metal blades
		for (int i = 0; i <= 3; i++)
			metalBlades[i] = new MetalBlade();

	}

	public void bossIntro()
	{
		super.move();
		Level.getCurrentRoom().checkCollisions(this);
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
		if (health > 0)
		{
			super.update();
			
			drawn = false;
			//Make metal man start all his actions when bossCounter reaches 0, meaning the intro is done
			if (Level.getCurrentRoom().bossCounter == 0)
				start = true;

			// If metal man touches the player, damage the player by 4
			if (getBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(4);

			for (int i = 0; i <= 2; i++)
			{
				if (DrawPanel.player.bullets[i].alive == true)
					//If the player hits metal man with a bullet
					if (getBounds().intersects(DrawPanel.player.bullets[i].getBounds()) && hitCooldown == 0)
					{
						//Damage metal man by 2, register the hit, play the hit sound, Destroy the
						//player's bullet, and give metal man a coolDown before he can get hit again
						health -= 3;
						hit = true;
						DrawPanel.playSound("enemyHit.wav");
						DrawPanel.player.bullets[i].alive = false;
						hitCooldown = 20;
					}
			}
		}
		//Update his metal blades
		for (int i = 0; i <= 3; i++)
			metalBlades[i].update();

		// If metal man is killed and the intro was already performed, decrease bossCounter
		if (health <= 0 && Level.getCurrentRoom().bossCounter == 0)
			Level.getCurrentRoom().bossCounter = -1;
		
		if (hitCooldown > 0)
			hitCooldown--;

	}

	//Change the direction metal man is facing
	public void changeDirection()
	{
		if (direction == 1)
			direction = -1;
		else
			direction = 1;
	}

	// Change the conveyor tiles to the opposite of what they are
	public void changeConveyorTile()
	{
		if (currentConveyorTile == '7')
		{
			Level.getCurrentRoom().updateTile(14, 12, '6');
			Level.getCurrentRoom().updateTile(1, 12, '6');
			currentConveyorTile = '6';
		} else
		{
			Level.getCurrentRoom().updateTile(14, 12, '7');
			Level.getCurrentRoom().updateTile(1, 12, '7');
			currentConveyorTile = '7';
		}

	}

	//Returns a random double from the range given
	public double randDouble(int start, int end)
	{
		return ((Math.random() * (end - start)) + start);
	}

	//Shoots the next available blade, if there is one
	public void bladeHandler()
	{
		for( int i = 0; i < 3; i++)
		{
			if (metalBlades[i].alive == false)
			{
				metalBlades[i].shoot(getX(), getY(), (int) (DrawPanel.player.displayX + 270), DrawPanel.player.getY(), false);
				break;
			}
		}
	}

	// Makes metal man throw a blade. handles various variables and metal man's vertical velocity.
	public void throwBlade()
	{
		if (throwCounter == 0)
			throwCounter = 11;
		else if (throwCounter == 3)
		{
			bladeHandler();
			numBlades--;
			yv /= 8;
		}
	}

	//Makes metal man jump up before he throws blades. 
	//He will jump higher if he is going to throw more blades.
	public void jumpThrow(int numBlades)
	{
		yv = -8 * numBlades;
	}

	@Override
	public void move()
	{
		super.move();

		// If the intro is done
		if (start)
		{

			if (onGround && !jump)
			{
				// If the player is too close
				if (Math.abs(DrawPanel.player.displayX + 270 - x) <= 160)
				{
					// Jump and face the other direction
					jump = true;
					yv = -24;
					if ((x > 240 && direction == 1) || (x < 240 && direction == -1))
					{
						changeDirection();
					}
					jumpX = ((240 - x) + 130 * direction) / 40.0;
				}
			// If he's on the ground, set jump to be false
			} else if (onGround && jump)
				jump = false;
			
			// If he's jumping
			if (jump)
			{
				// Once he reaches the peak of his jump, shoot a blade
				if (yv > -1 && yv < 1)
					metalBlades[3].shoot(getX(), getY(), (int) (DrawPanel.player.displayX + 270), DrawPanel.player.getY(), true);
				xv = jumpX;
			}
			
			// Once conveyorCounter has counted down to 0
			if (conveyorCounter == 0)
			{
				//change the direction of the conveyor belt
				changeConveyorTile();
				//Give conveyorCounter a range to randomize from for the next time to change
				conveyorCounter = (int) randDouble(150, 350);
			} else
				conveyorCounter--;

			//If not jumping
			if (!jump)
			{
				if (wait == 0)
				{
					if (onGround)
					{
						//choose how many blades to fire
						numBlades = (int) randDouble(1, 4);
						totalBlades = numBlades;
						//Jump up. More blades = higher jump
						yv = -6 * numBlades - 4;
						//How long to wait on the conveyor belt before the next jump
						wait = (int) randDouble(5, 30);
					}
				// If in the air
				} else if (!onGround)
				{
					// SIDE NOTE - When a blade is thrown, metal man's vertical velocity is reduced to near 0
					// so that he almost pauses in mid-air. That is why I can check for yv being less than
					// -5 as a time to shoot blades.
					// Throw the blade once falling fast enough
					if (yv > -5 && numBlades > 0)
					{
						throwBlade();
					}
				} else
				{
					wait--;
					throwCounter = 0;
				}
			}

			if (throwCounter > 0)
				throwCounter--;

		}

	}

	public void drawHealth(Graphics2D b)
	{
		//Draw his health bar
		b.setColor(Color.BLACK);
		b.fillRect(43, 13, 20, 142);
		b.setColor(health1);

		//If his health bar is filling up
		if (displayHealth < 28)
		{
			//Draw the tiny bars of health according to displayHealth
			for (int i = 0; i < displayHealth; i++)
				b.fillRect(45, 150 - i * 5, 16, 3);
			b.setColor(health2);
			for (int i = 0; i < displayHealth; i++)
				b.fillRect(50, 150 - i * 5, 6, 3);
			//Increase the displayHealth so it animates up
			displayHealth += 0.4;
		// Else if the animation is already done
		} else
		{
			//Draw the tiny bars of health according to health
			for (int i = 0; i < health; i++)
				b.fillRect(45, 150 - i * 5, 16, 3);
			b.setColor(health2);
			for (int i = 0; i < health; i++)
				b.fillRect(50, 150 - i * 5, 6, 3);
		}
	}

	//Draw metal man. Used during intro.
	public void draw(Graphics2D b, int first, int second)
	{
		b.drawImage(image[first][second], getX() - 17, getY() - 31, null);
		drawn = true;
		hit = false;
	}

	//Draw metal man. Used during battle.
	@Override
	public void draw(Graphics2D b)
	{
		if (Level.getCurrentRoom().bossCounter < 90)
			drawHealth(b);

		for (int i = 0; i <= 3; i++)
			metalBlades[i].draw();

		//If the conveyor belt is going to be changed, flash the screen white
		if (conveyorCounter < 2)
		{
			b.setColor(new Color(255, 255, 255));
			b.fillRect(0, 0, 480, 512);
		}

		if (!drawn)
		{
			// If metal man is alive, draw him
			if (health > 0)
			{
				if (hitCooldown % 4 >= 2)
					b.drawImage(hitImage, getX() - 17, getY() - 31, null);
				else
				{
					//If on ground, draw metal man running
					if (onGround == true)
					{
						if (Level.getCurrentRoom().bossCounter != 0)
							b.drawImage(image[0][(int) (x / 240)], getX() - 17, getY() - 31, null);
						else if (currentConveyorTile == '6')
							b.drawImage(image[(counter / 5) + 4][(int) (x / 240)], getX() - 17, getY() - 31, null);
						else if (currentConveyorTile == '7')
							b.drawImage(image[(counter / 5) + 4][(int) (x / 240)], getX() - 17, getY() - 31, null);
					//If he's in the air, draw him falling/jumping
					} else
					{
						if (yv > -1 && Level.getCurrentRoom().bossCounter == 0)
						{
							if (throwCounter > 6 || throwCounter < 2)
								b.drawImage(image[9][(int) (x / 240)], getX() - 17, getY() - 31, null);
							else
								b.drawImage(image[8][(int) (x / 240)], getX() - 17, getY() - 31, null);
						} else
							b.drawImage(image[1][(int) (x / 240)], getX() - 17, getY() - 31, null);
					}
					drawn = true;
				}
			// If he is dead, draw the pretty starburst of flashing bubbles
			} else
			{
				deathBurst();
				deathCounter++;
			}
		}
		//Counter used for animating metal man's frames
		if (counter < 19)
			counter++;
		else
			counter = 0;

		hit = false;
	}

	//Draws the pretty starburst of flashing bubbles wooo probably horribly inefficient sorry XD
	public void deathBurst()
	{
		drawDeathBubble(deathCounter * 2, 0);
		drawDeathBubble(-deathCounter * 2, 0);
		drawDeathBubble(0, deathCounter * 2);
		drawDeathBubble(0, -deathCounter * 2);
		drawDeathBubble(deathCounter, 0);
		drawDeathBubble(-deathCounter, 0);
		drawDeathBubble(0, deathCounter);
		drawDeathBubble(0, -deathCounter);
		drawDeathBubble(deathCounter * 1.4, deathCounter * 1.4);
		drawDeathBubble(deathCounter * 1.4, -deathCounter * 1.4);
		drawDeathBubble(-deathCounter * 1.4, deathCounter * 1.4);
		drawDeathBubble(-deathCounter * 1.4, -deathCounter * 1.4);
	}

	public void drawDeathBubble(double x, double y)
	{
		DrawPanel.b.drawImage(death[(deathCounter % 8) / 2], (int) (getX() + x), (int) (getY() + 7 + y), null);
	}

}

//Metal blades that metal man throws
class MetalBlade
{
	double x;
	double y;
	int direction;
	double yv;
	double xv;
	int width = 30;
	int height = 30;
	boolean alive = false;

	int counter = 0;

	//metal blade images
	BufferedImage image[] = new BufferedImage[2];

	public MetalBlade()
	{
		try
		{
			//loads metal blade images
			for (int i = 1; i <= 2; i++)
				image[i - 1] = ImageIO.read(new File("images/enemies/metal man/metalBlade" + i + ".gif"));
		} catch (IOException e)
		{
		}
	}

	//shoots the metal blades from the source given, towards the destination
	public void shoot(int x, int y, int xDestination, int yDestination, boolean jump)
	{
		if (!alive)
		{
			//set metal blade's x and y to the inputted x and y
			this.x = x;
			this.y = y;
			
			//If jumping overhead, the X velocity needs to be calculated more accurately
			if (jump)
			{
				//shoot downwards
				yv = 13;
				
				//calculate x so it will be shot towards the player
				xv = (xDestination - x) / (Math.abs(yDestination - y) / 12.0);
				if (xv > 10)
					xv = 10;
				direction = 1;
			//else if not jumping overhead, the Y velocity needs to be calculated more accurately
			} else
			{
				//Shoot horizontally (this will be multiplied by 1 or -1 depending on direction)
				xv = 13;
				
				//calculate y so it will be shot towards player
				yv = (yDestination - y) / (Math.abs(xDestination - x) / 12.0);
				if (yv > 10)
					yv = 10;
				
				//Determine direction to shoot in
				if (x > xDestination)
					direction = -1;
				else
					direction = 1;
			}

			//Play the metal blade shooting sound effect
			DrawPanel.playSound("metalBlade.wav");

			alive = true;
		}
	}

	public void update()
	{
		if (alive)
		{
			//update x and y according to velocities
			x += xv * direction;
			y += yv;
		}
		
		//If metal blade goes offscreen, set it as dead
		if (getX() > 512 || getX() < -32 || getY() > 480)
			alive = false;

		//If metalman is still alive
		if (MetalMan.health > 0)
		{
			//If the metal blade hits the player, damage him by 4
			if (getBounds().intersects(DrawPanel.player.getDisplayBounds()))
				DrawPanel.player.takeDamage(4);
		}

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

	public void draw()
	{
		if (alive)
		{
			DrawPanel.b.drawImage(image[counter / 4], (int) (getX()), getY() - 1, null);
		}
		if (counter < 7)
			counter++;
		else
			counter = 0;
	}

	public Rectangle getDisplayBounds()
	{
		return new Rectangle(getX() + (int) DrawPanel.player.displayX, getY(), getWidth(), getHeight());
	}

}
