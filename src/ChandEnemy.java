import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ChandEnemy extends Entity
{

	int riseCounter = 0;
	int initialY;
	boolean onGround = false;
	
	// ChandEnemy file
	File imageFile = new File("images/enemies/metal man/chand.gif");
	// ChandEnemy image
	BufferedImage image;
	
	int centerX;
	boolean fall = false;
	
	// Plays drop sound when true
	boolean hitSound = false;
	Player player = DrawPanel.player;

	public ChandEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y + 8, w, h, s);
		initialY = y;

		try
		{
			//Loads ChandEnemy Image
			image = ImageIO.read(imageFile);
		} catch (Exception e)
		{
		}
		centerX = (image.getWidth() - width) / 2;
	}

	@Override
	public void move()
	{
		//If player comes close to enemy, make it fall and prepare enemy to play the hitSound
		if (x - player.x <= 50 && (x + width) - player.x >= -35 && riseCounter == 0)
		{
			fall = true;
			hitSound = true;
		}

		// If enemy is falling, apply gravity to pull it down
		if (fall)
			yv += 1.4;

		try
		{
			// If enemy hits the floor
			if (Level.getCurrentRoom().tiles[getX() / 32][(getY() + height + 1) / 32].background == false)
			{
				// Stop falling and play the hitsound
				yv = 0;
				fall = false;
				riseCounter++;
				if (hitSound)
				{
					DrawPanel.playSound("chandSlam.wav");
					hitSound = false;
				}
			}

			//after enemy has been on the floor for a bit
			if (riseCounter >= 30)
			{
				//Slowly rise up
				yv = -0.9;
				//If the enemy hits the ceiling, stop rising
				if (Level.getCurrentRoom().tiles[getX() / 32][(getY() - 9) / 32].background == false)
				{
					yv = 0;
					riseCounter = 0;
				}
			}
		} catch (Exception e)
		{
		}

	}

	@Override
	public void update()
	{
		// Move enemy's y position based on y velocity
		y += yv;
		
		// If player touches the enemy, the player takes 4 damage
		if (getBounds().intersects(player.getBounds()))
			player.takeDamage(4);

		for (int i = 0; i <= 2; i++)
		{
			//If the player hits the enemy with a bullet, deflect the bullet
			if (player.bullets[i].alive == true)
				if (getBounds().intersects(player.bullets[i].getBounds()))
					player.bullets[i].deflected = true;
		}
	}

	@Override
	public void draw(Graphics2D b)
	{
		//Draw the enemy
		b.drawImage(image, getX() - (image.getWidth() - width) / 2 - player.getX() + 270, getY() - image.getHeight() + 29, null);

	}

}
