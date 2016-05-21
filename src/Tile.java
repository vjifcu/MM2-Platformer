import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.File;

import javax.imageio.ImageIO;

//The superclass for all the tiles
public class Tile
{

	int x;
	int y;
	static int TILESIZE = 32;
	static int counter = 0;
	static int speed = 5;
	boolean background;
	boolean animated = false;
	static BufferedImage[] tileset;
	static BufferedImage[][] tilesetAnimated;
	static int divisor;
	BufferedImage tileImage;
	BufferedImage[] tileImageAnimated = new BufferedImage[2];
	char tileID;
	boolean editMode = Level.editMode;

	int animationOffset = 0;

	static int offset;
	static int offsetY = 5;

	//When you create a tile, you have to tell it which tile it is
	public Tile(int x, int y, char image)
	{
		setTile(x, y, image);
	}

	/** Making an array filled with the level's tile's images, then making each tile grab their image from that array has been proven through multiple
	 * tests to be at least 5x faster than making each tile grab its image directly from the folder **/
	
	//Used for setting up an array filled with the level's tiles.
	public static void setTileSet()
	{

		tileset = new BufferedImage[45];
		tilesetAnimated = new BufferedImage[45][2];

		for (int k = 1; k <= 45; k++)
		{
			try
			{
				tileset[k - 1] = ImageIO.read(new File("images/tiles/" + Level.name + "/" + k + ".bmp"));
			} catch (Exception e)
			{
			}
			try
			{
				for (int i = 1; i <= 2; i++)
				{
					tilesetAnimated[k - 1][i - 1] = ImageIO.read(new File("images/tiles/" + Level.name + "/" + k + "_" + i + ".bmp"));
				}

			} catch (Exception e)
			{
			}

		}

	}

	// Sets a tile to have a specific image. Grabs the image from the tileset arrays
	public void setImage(int requestedTile)
	{

		try
		{
			tileImage = tileset[requestedTile - 1];
			animated = false;
		} catch (Exception e)
		{
		}

		try
		{
			if (tilesetAnimated[requestedTile - 1][0] != null)
			{
				for (int i = 1; i <= 2; i++)
					tileImageAnimated[i - 1] = tilesetAnimated[requestedTile - 1][i - 1];
				animated = true;
			}
		} catch (Exception e)
		{
		}

	}

	//sets the requested tile to the new tile
	public void setTile(int x, int y, char image)
	{
		this.x = x;
		this.y = y;

		tileID = image;
		
		setImage((int) (tileID) - 33);


		if (image == '!' || image >= '@')
			this.background = true;
		else
			this.background = false;

	}

	public char getID()
	{
		return tileID;
	}

	//Increments the counter, which is used by animated tiles
	public static void counterAdd()
	{
		counter++;
		if (counter > speed * 2 - 1)
		{
			counter = 0;
			if (IceTile.iceCounter > 4)
				IceTile.iceCounter = 0;
			else
				IceTile.iceCounter++;
		}
	}

	//Draws a tile in a specific location.
	public void draw(Graphics2D b, int x, int y, boolean mouse)
	{
		//If the x and y are coming from the mouse's position
		if (mouse)
		{
			//Tile it so that it is rounded to the correct tile
			x = x / 32 * 32 - offset % 32;
			y = y / 32 * 32;
			//set the tile image to 50% opacity
			b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			
			//Draw the tile
			if (animated == true)
			{
				b.drawImage(tileImageAnimated[counter / speed], x, y, null);
			} else
			{
				b.drawImage(tileImage, x, y, null);
			}
			
			//reset opacity
			b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			
		//if the x and y are requested normally
		} else
		{
			//draw the tiles
			if (animated == true)
			{

				b.drawImage(tileImageAnimated[Tile.counter / speed], this.x * TILESIZE + x, this.y * TILESIZE + y, null);

			} else
			{
				b.drawImage(tileImage, this.x * TILESIZE + x, this.y * TILESIZE + y, null);
			}
		}
	}

	public void groundCollision(Entity entity)
	{

	}

	//Draws the tiles generally. This is used for drawing the entire level rather than drawing
	// a specific tile in a specific place.
	public void draw(Graphics2D b, int offset)
	{
		this.offset = offset;

		if (animated == true)
		{

			b.drawImage(tileImageAnimated[Tile.counter / speed], x * TILESIZE - offset, y * TILESIZE, null);

		} else
		{
			b.drawImage(tileImage, x * TILESIZE - offset, y * TILESIZE, null);
		}

	}

}
