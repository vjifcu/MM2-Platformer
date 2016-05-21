import java.awt.Graphics2D;

//Conveyor belt tile class that pushes the player in a specific direction
public class ConveyerTile extends Tile
{

	int direction = 0;

	public ConveyerTile(int x, int y, char image)
	{
		super(x, y, image);
	}

	// Conveyor tiles scan to the right continously until they reach the end of the 
	// conveyor belt, where an arrow conveyor belt tile is found. The tileID of this tile
	// reveals which direction the arrow is pointing, which is returned to all the conveyor tiles
	// on that line of conveyor belts so that they can all push the player in that direction.
	
	// returns a 1 or -1 depending on which direction it pushes the player
	public int connectedTo()
	{
		// If it is an arrow tile, return 1 or -1 depending on tileID (direction pointing)
		if (tileID == '6')
			return 1;
		else if (tileID == '7')
			return -1;
		else
		// If it is a regular conveyor tile, pass it on to the next tile to the right.
		// The program will keep passing over the tiles until it reaches an arrow tile,
		// Which will set the direction of all the tiles in that line.
			return ((ConveyerTile) Level.getCurrentRoom().tiles[x + 1][y]).connectedTo();
	}

	@Override
	public void groundCollision(Entity entity)
	{
		//Determines direction to push player
		if (tileID == '6')
			direction = 1;
		else if (tileID == '7')
			direction = -1;
		else
			direction = ((ConveyerTile) Level.getCurrentRoom().tiles[x + 1][y]).connectedTo();

		//Pushes player as long as he is not doing the hurt animation
		if (entity.hitCooldown < 60)
			entity.xv += 3.3 * direction;

	}

}
