import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player extends Entity
{

	// Player images
	BufferedImage[][] image;
	BufferedImage[] deathImage;
	BufferedImage[][] imageShoot;
	BufferedImage[] beam;

	// direction facing
	int direction = 0;

	// drawXV used so that the player is drawn running right even if collision checking makes
	// xv equal 0 because the player is running into a wall. This is used when determining what
	// frames to draw for the player.
	double drawXV = 0;

	// Used for animating through the player's frames
	int animationCounter = 14;

	int health = 28;
	int damage;
	boolean alive = true;
	boolean hit = false;

	// Health bar colors
	Color health1 = new Color(255, 244, 90);
	Color health2 = new Color(250, 255, 210);
	// Deathcounter used to animate the death balls
	int deathCounter = 0;
	// Player's bullets
	Bullet[] bullets;

	int shootCounter = 0;
	boolean shoot = false;
	boolean shootHeld = false;

	boolean beaming = true;
	boolean beamStart = true;
	boolean beamDown = true;
	int beamCounter = 0;

	// Sound variables
	boolean damageSound = true;
	boolean landSound = false;
	boolean beamSound = true;

	public Player(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);
		image = new BufferedImage[9][2];
		imageShoot = new BufferedImage[9][2];
		deathImage = new BufferedImage[4];
		beam = new BufferedImage[4];

		// Load images
		try
		{
			for (int k = 1; k <= 2; k++)
			{
				for (int i = 1; i <= 9; i++)
					image[i - 1][k - 1] = ImageIO.read(new File("images/megaman/" + i + "_" + k + ".gif"));
				for (int i = 1; i <= 6; i++)
					imageShoot[i - 1][k - 1] = ImageIO.read(new File("images/megaman/" + i + "_" + k + "_shoot.gif"));
			}
			for (int i = 1; i <= 4; i++)
			{
				deathImage[i - 1] = ImageIO.read(new File("images/megaman/death" + i + ".gif"));
				beam[i - 1] = ImageIO.read(new File("images/megaman/beam" + i + ".gif"));
			}
		} catch (IOException e)
		{
			System.out.println(e);
		}
		// Initialize player's bullets (only 3 on the screen at a time)
		bullets = new Bullet[3];
		for (int i = 0; i <= 2; i++)
			bullets[i] = new Bullet();

	}

	@Override
	public void setAlive(boolean setter)
	{
		alive = setter;
	}

	@Override
	public void move()
	{
		if (!Level.editMode)
		{
			if (hitCooldown <= 60)
			{
				//Sets x velocity equal to 0 every loop. The player stops immediately when keys are not pressed, no sliding/friction.
				//( at least until ice is introduced ;D ).
				xv = 0;

				//If the player is on the ground after being in the air (landSound = true) and he wasn't beaming, play the land sound.
				if (onGround && landSound && !beaming)
				{
					DrawPanel.playSound("playerLand.wav");
					landSound = false;
				// If the play is in the air and not beaming, make landSound = true.
				} else if (!onGround && !beaming)
					landSound = true;

				//If the player is on the ground and not holding up, allow the player to jump.
				//This prevents the player from continuously jumping by holding up.
				if (onGround == true && !up)
					canJump = true;

				//If the player is holding up, and is on the ground, and was able to jump
				if (up && onGround && canJump)
				{
					//make canJump false and make the player jump up
					canJump = false;
					yv = -16.5;
				}
				
				//Moves player depending on keys pressed
				if (left)
				{
					direction = 1;
					xv = -speed;
				}
				if (right)
				{
					direction = 0;
					xv = speed;
				}
				
				//Shoots if the shoot key is pressed. The player must press the key for each bullet fired.
				if (shoot && !shootHeld)
				{
					shootHeld = true;
					shootHandler();
				}
				if (!shoot)
					shootHeld = false;

				//Gravity (giving it a cap helps a bit with collision, and isn't too noticeable)
				if (yv < 10)
					yv += 1.25;
				else
					//Rather than a hard cap, make gravity increase slower and slower as it goes further over the cap.
					//Makes the cap less noticeable.
					yv += 1.25 / (yv - 9.5);

				if (!up && yv < 0)
					yv -= yv / 2;
			}
		//If in edit mode
		} else
		{
			//Move the player super fast. Ignore collision.
			if (up)
				y -= 20;
			if (left)
				x -= 20;
			if (right)
				x += 20;
			if (down)
				y += 20;
		}

		//drawXV equals the xv before it is run through collision
		drawXV = xv;

		// Once the hit cooldown runs out, hit = false and the player is able to take damage again
		if (hitCooldown == 1)
			hit = false;
		
		//If the player is not in their hitcooldown and they get hit
		if (hitCooldown == 0 && hit == true)
		{
			//Play the playerHit sound effect, damage the player, and set the cooldown to 100
			DrawPanel.playSound("playerHit.wav");
			health -= damage;
			hitCooldown = 100;
		}
		
		//If the cooldown is above 60
		if (hitCooldown > 60)
		{
			//Freeze the player into going slowly backwards to where they were facing
			xv = -(1 - (2 * direction));
			yv = 0;
			
			hitCooldown--;
		}
		
		if (hitCooldown > 0)
		{
			hitCooldown--;
		}

	}

	public void takeDamage(int damage)
	{
		hit = true;
		this.damage = damage;
	}

	@Override
	public void update()
	{
		//If the player needs to beam (start of level) beam him downwards
		if (beaming)
			beam(beamDown);

		//If health is below or at 0, set the player as dead
		if (health <= 0)
			alive = false;

		//If the player is dead
		if (!alive)
		{
			//freeze player and increment deathCounter (used for flashing death balls)
			health = 0;
			xv = 0;
			yv = 0;
			deathCounter++;
		}

		//Update x and y using x and y velocities (which have been run through collision checking)
		x += xv;
		y += yv;

		//update bullets
		for (int i = 0; i <= 2; i++)
			bullets[i].update();

		//If the room is 16 tiles wide, don't pan the level.
		if (Level.getCurrentRoom().maxX == 16)
		{
			x = 270;
			displayX += xv;
			if (displayX < -260)
				displayX = -260;
		//if the room is wider than 16 tiles
		} else
		{
			//Freeze the player in the middle of the screen if he is not near the edges
			if (x < 270 || displayX < 0)
			{
				x = 270;
				if (displayX + xv > 0)
					displayX = 0;
				else if (displayX + xv > -260)
					displayX += xv;

			//Pan the level according to the player's movement
			} else if (x > Level.getCurrentRoom().maxX * 32 - 242 || displayX > 0)
			{
				x = Level.getCurrentRoom().maxX * 32 - 242;
				if (displayX + xv < 0)
					displayX = 0;
				else
					displayX += xv;
			}
		}

	}

	//If a bullet is available to shoot, shoot it.
	public void shootHandler()
	{
		for (int i = 0; i < 3; i++)
		{
			if (bullets[i].alive == false)
			{
				bullets[i].shoot((int) x, (int) y, direction);
				DrawPanel.playSound("shootBullet.wav");
				break;
			}
		}
	}

	@Override
	public void draw(Graphics2D b)
	{
		//Draw the bullets
		for (int i = 0; i <= 2; i++)
			bullets[i].draw();

		//If the player is alive
		if (alive)
		{
			//If beaming, draw the beaming animation
			if (beaming)
				b.drawImage(beam[beamCounter], (int) displayX + 250, getY() - 20, null);
			else
			{
				//If the player hasn't recently shot a bullet
				if (shootCounter == 0)
				{
					//If the player was recently hit
					if (hitCooldown > 60)
						//Draw the "getting hit" animation for the player
						b.drawImage(image[(hitCooldown % 12) / 4 + 6][direction], (int) displayX + 250, getY() - 11, null);
					//If the player was hit a while ago, draw him normally but don't draw him for 2 frames every 2 frames.
					//This makes the player flash.
					else if (hitCooldown % 4 <= 1)
					{
						//If on the ground
						if (onGround)
						{
							//If the player is running, draw him doing his running animation
							if (drawXV != 0)
								b.drawImage(image[animationCounter / 6 - 1][direction], (int) displayX + 249, getY() - 21, null);
							//If the player is not moving, draw him standing still.
							else
								b.drawImage(image[0][direction], (int) displayX + 249, getY() - 21, null);
						//If in the air, draw him falling
						} else
							b.drawImage(image[5][direction], (int) displayX + 250, getY() - 11, null);
					}
				//If the player has recently shot a bullet
				} else
				{
					//If the player was recently hit
					if (hitCooldown > 60)
						//Draw the "getting hit" animation for the player
						b.drawImage(image[(hitCooldown % 12) / 4 + 6][direction], (int) displayX + 250 - 6, getY() - 11, null);
					//If the player was hit a while ago, draw him normally but don't draw him for 2 frames every 2 frames.
					//This makes the player flash.
					else if (hitCooldown % 4 <= 1)
					{
						//If on the ground
						if (onGround)
						{
							//If the player is running, draw him shooting and running
							if (drawXV != 0)
								b.drawImage(imageShoot[animationCounter / 6 - 1][direction], (int) displayX + 249 - 6, getY() - 21, null);
							//If the player is not moving, draw him shooting and standing
							else
								b.drawImage(imageShoot[0][direction], (int) displayX + 249 - 6, getY() - 21, null);
						//If in the air
						} else
							//Draw him falling and shooting
							b.drawImage(imageShoot[5][direction], (int) displayX + 250 - 6, getY() - 11, null);
					}
					shootCounter--;
				}
			}
			//Increment actionCounter. Used to animate the player
			if (animationCounter < 35)
				animationCounter++;
			else
				animationCounter = 12;
		} else
		{
			//Draws the fancy death bubble starburst
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
	}

	//Beam the player up or down
	public void beam(boolean downwards)
	{
		if (beamStart)
		{
			beamSound = true;
			
			// If going downwards, move the player up
			if (downwards)
				y = -getHeight();
			
			beamStart = false;
		}
		
		//If going downwards
		if (downwards)
		{
			//move the player downwards as long as they don't collide with a block (collision starts checking past the 8th row to avoid hitting ceiling)
			if ((int) (y + height + 16) / 32 < 8
					|| Level.getCurrentRoom().tiles[(int) (x + getBG()) / 32][(int) (y + height + 16) / 32].background == true)
				yv = 16;
			//If the player landed on a block
			else
			{
				//Play the beam landing sound
				if (beamSound)
				{
					DrawPanel.playSound("beamLanding.wav");
					beamSound = false;
				}
				
				//Increment counter to display the correct player animation
				if (animationCounter % 3 == 0)
					beamCounter++;
				
				//Once beam animation is done, set beaming false and get beamStart ready for next beaming.
				if (beamCounter == 3)
				{
					beaming = false;
					beamStart = true;
				}
			}
		//If going upwards
		} else
		{
			//Play beamSound right away
			if (beamSound)
			{
				DrawPanel.playSound("beamLanding.wav");
				beamSound = false;
			}
			
			//Show beaming animation first
			if (animationCounter % 3 == 0 && beamCounter > 0)
				beamCounter--;
			
			//Once beaming animation is done, move character up
			if (beamCounter == 0)
				yv = -16;

			//Once the player is offscreen
			if (y < -getHeight())
			{
				//set beaming false and get beamStart ready for next beaming.
				beaming = false;
				beamStart = true;
			}

		}
		//Prevents player from moving horizontally
		xv = 0;
	}

	//Draw death bubble (duh)
	public void drawDeathBubble(double x, double y)
	{
		DrawPanel.b.drawImage(deathImage[(deathCounter % 8) / 2], (int) (displayX + 264 + x), (int) (getY() + 7 + y), null);
	}

	//Draw health bar
	public void drawHealth(Graphics2D b)
	{
		b.setColor(Color.BLACK);
		b.fillRect(24, 13, 18, 142);
		
		b.setColor(health1);
		for (int i = 0; i < health; i++)
			b.fillRect(26, 150 - i * 5, 14, 3);
		
		b.setColor(health2);
		for (int i = 0; i < health; i++)
			b.fillRect(30, 150 - i * 5, 6, 3);
	}

}
