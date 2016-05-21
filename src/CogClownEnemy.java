import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Main cog clown enemy class that controls the other two cog clown enemy subclasses.
//This class handles how the two classes interact together, and updates both of the classes.
public class CogClownEnemy extends Entity
{
	boolean connected;
	ClownEnemy clown;
	CogEnemy cog;

	boolean alive = true;
	int health = 5;
	boolean hit = false;
	boolean start = false;

	int direction;
	int counter = 0;

	public CogClownEnemy(int x, int y, int w, int h, double s)
	{
		super(x, y, w, h, s);
		//Creates a clown entity, and a cog entity
		clown = new ClownEnemy(x + 8, -50, 35, 50, s);
		cog = new CogEnemy(x, y, w, h, s);
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
		if (alive = false)
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
		//Updates the clown and the cog if they are alive
		if (cog.getAlive())
			cog.update();
		if (clown.getAlive())
			clown.update();

		//If neither entities are alive, the entire enemy is declared as dead
		if (!(cog.getAlive() || clown.getAlive()))
			alive = false;

	}

	@Override
	public void move()
	{
		// If the enemy comes in the player's view, make it start
		if(DrawPanel.player.x > cog.x - 220 && DrawPanel.player.x < cog.x)
			start = true;
		
		// If the clown isn't connected to the enemy yet
		if (!connected && start)
		{
			//Make the clown jump
			if (clown.y + clown.height + clown.yv >= cog.y + 8)
				clown.jump(y);
			
			//If it's done jumping
			if (clown.jumpFinished && cog.health != 0)
			{
				//Set connected to true since it is now sitting on the cog
				connected = true;
				
				//Side note - Clown's position is relative to the cog's position once connected = true.
				// Because of this, just moving the cog will move the whole enemy.
				
				//Make the cog drop
				cog.yv -= 6;
				
				// Set the enemy's direction towards were the enemy is
				if (DrawPanel.player.x >= cog.x)
					direction = -1;
				else
					direction = 1;
			}
			clown.move();
		}

		// If the cog is dead and the clown was previously connected to it
		if (cog.health == 0 && connected == true)
		{
			// Declare the entities as being disconnected, which causes the clown
			// to move independantly of the cog, and allows gravity to affect it.
			connected = false;
			
			// Make the clown hop a bit. Gravity is now in effect so the clown
			// will fall down shortly.
			clown.yv -= 6;
		}

		// If the entities are connected
		if (connected)
		{
			cog.move();
			
			//If the cog is on the ground, move it in the direction it is facing
			if (cog.onGround)
			{
				// If the clown is attached, move faster
				if (clown.health != 0)
					cog.xv = -5*direction;
				// If the clown is detached (killed), move slower
				else
					cog.xv = -2.5*direction;
			}
			//Collision checking for the cog wheel
			Level.getCurrentRoom().checkCollisions(cog);
			
			// Move clown so it is positioned above the cog wheel
			clown.y = cog.y - clown.height;
			clown.yv = cog.yv;
			clown.x = cog.x  + 8;
			
		}	

	}

	@Override
	public void draw(Graphics2D b)
	{
		//Draw the entities if they are alive
		if (cog.getAlive())
			cog.draw(b);
		if (clown.getAlive())
			clown.draw(b);

	}

}
