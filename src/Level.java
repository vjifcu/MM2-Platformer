import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Level
{

	static int sections;
	static String fileName;
	static Scanner sc;
	static Room[] rooms;
	static int currentRoom = 1;
	static String name;

	static boolean doorSound = true;

	// Speed for changing rooms
	static double multiplier = 68.0;

	static Tile mouseTile = new Tile(0, 0, '0');;

	static int xBG = 0;
	static boolean editMode = false;

	static int shiftX = 0;
	static int shiftY = 0;

	static int xDirection;
	static int yDirection = 0;

	static boolean changing = false;

	// Loads the level
	public Level(String fileName, String name)
	{
		Level.fileName = fileName;
		this.name = name;
		
		// Creates array filled with corresponding level's tiles
		Tile.setTileSet();

		try
		{
			// Gets map file
			sc = new Scanner(new File("mapData/" + name + "/" + Level.fileName + "1.txt"));

			// Gets number of rooms for the current level
			sections = Integer.parseInt(sc.next());
			// Creates the rooms
			rooms = new Room[sections];

			// Loads the map for each room
			for (int i = 1; i <= sections; i++)
			{
				rooms[i - 1] = new Room();
				rooms[i - 1].loadMap("mapData/" + name + "/" + Level.fileName + i);
			}

			// Last room is set as the boss room
			rooms[sections - 1].bossCounter = 160;
			rooms[sections - 1].bossRoom = true;

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

	}

	// Handles movement from one room to the next
	public static void changeRoom(int x, int y, boolean bossDoor, Graphics2D b)
	{
		changing = true;
		shiftX = 0;
		shiftY = 0;
		xDirection = x;
		yDirection = -y;
		if (bossDoor)
			multiplier = 73.0;
		else
			multiplier = 68.0;
	}

	public static void drawLevel(Entity entity, Graphics2D b)
	{
		// If not changing rooms
		if (changing == false)
		{
			// Draw level normally (scrolling dictated by player movement)
			int x = (int) entity.getBounds().getCenterX() - DrawPanel.WIDTH / 2;
			rooms[currentRoom - 1].drawRoom(x - 32, (int) entity.getBounds().getCenterX() + entity.height + DrawPanel.WIDTH / 2, x - 26, b);
			BossDoorTile.counter = 39;

			// If in edit mode, draw semi-transparent selected tile over mouse
			if (editMode)
			{
				mouseTile.draw(b, MouseMotionHandler.mouseX, MouseMotionHandler.mouseY, true);
			}
		// Else if changing rooms
		} else if (changing == true && multiplier == 68)
		{
			// Draw both rooms, scrolling over time
			b.setColor(Color.BLACK);
			b.fillRect(0, 0, DrawPanel.WIDTH, DrawPanel.HEIGHT);
			rooms[currentRoom - 1].drawRoom(-(rooms[currentRoom - 1].maxX - 16) * 32 - shiftX * xDirection, shiftY * yDirection, b);
			rooms[currentRoom].drawRoom((DrawPanel.WIDTH - shiftX * xDirection) * xDirection,
					shiftY * yDirection + DrawPanel.HEIGHT * Math.abs(yDirection), b);

			// Lock player in place while rooms are changing
			entity.x = 270;
			entity.yv = 0;
			entity.xv = 0;

			// Depending on the direction of the room change, adjust the room's X or Y
			if (xDirection != 0)
			{
				if (shiftX < DrawPanel.WIDTH)
				{
					entity.displayX -= DrawPanel.HEIGHT / multiplier * xDirection;
					shiftX += DrawPanel.WIDTH / 60;
				} else
				{
					changing = false;
					currentRoom++;
				}
			} else
			{
				entity.yv = DrawPanel.HEIGHT / 72.0 * yDirection;
				if (shiftY < DrawPanel.HEIGHT)
					shiftY += DrawPanel.HEIGHT / 60.0;
				else
				{
					changing = false;
					currentRoom++;
				}
			}
			Tile.counterAdd();
		// Else if the next room has boss doors
		} else
		{
			b.setColor(Color.BLACK);
			b.fillRect(0, 0, DrawPanel.WIDTH, DrawPanel.HEIGHT);
			if (BossDoorTile.counter > 0)
			{
				//play the boss door sounds
				if (doorSound)
				{
					DrawPanel.playSound("bossDoor.wav");
					doorSound = false;
				}
				
				// Draw the rooms and update the boss door tiles to be background tiles.
				// Gives the illusion of the doors opening
				rooms[currentRoom - 1].drawRoom(-(rooms[currentRoom - 1].maxX - 16) * 32 - shiftX * xDirection, shiftY * yDirection, b);
				rooms[currentRoom - 1].updateTile(rooms[currentRoom - 1].maxX - 1, BossDoorTile.counter / 10 + 4, getCurrentRoom().tiles[rooms[currentRoom - 1].maxX - 2][BossDoorTile.counter / 10 + 4].tileID);
				
				// Lock player in place
				entity.x = 270;
				entity.yv = 0;
				entity.xv = 0;
				
				BossDoorTile.counter--;
			} else
			{
				// Draw two rooms
				rooms[currentRoom - 1].drawRoom(-(rooms[currentRoom - 1].maxX - 16) * 32 - shiftX * xDirection, shiftY * yDirection, b);
				rooms[currentRoom].drawRoom((DrawPanel.WIDTH - shiftX * xDirection) * xDirection,
						shiftY * yDirection + DrawPanel.HEIGHT * Math.abs(yDirection), b);

				// Depending on direction of room change, adjust X or Y
				if (xDirection != 0)
				{
					if (shiftX < DrawPanel.WIDTH)
					{
						entity.displayX -= DrawPanel.HEIGHT / multiplier * xDirection;
						shiftX += DrawPanel.WIDTH / 60;

					} else if (BossDoorTile.counter > -40)
					{
						// Play boss door sound
						if (!doorSound)
						{
							DrawPanel.playSound("bossDoor.wav");
							doorSound = true;
						}
						rooms[currentRoom].drawRoom((DrawPanel.WIDTH - shiftX * xDirection) * xDirection, shiftY * yDirection + DrawPanel.HEIGHT
								* Math.abs(yDirection), b);
						//Return background tiles to boss door tiles again
						rooms[currentRoom].updateTile(0, (BossDoorTile.counter * -1) / 10 + 4, '4');
						BossDoorTile.counter--;
					} else
					{
						changing = false;
						currentRoom++;
					}
				} else
				{
					entity.yv = DrawPanel.HEIGHT / 72.0 * yDirection;
					if (shiftY < DrawPanel.HEIGHT)
						shiftY += DrawPanel.HEIGHT / 60.0;
					else
					{
						changing = false;
						currentRoom++;
					}
				}
			}
			Tile.counterAdd();
		}
	}

	public static void drawLevel(int x, int y, int roomNumber, Graphics2D b)
	{
		rooms[roomNumber].drawRoom(x, y, b);
	}

	public static void drawBG(Entity entity, Graphics2D b)
	{
		if (changing == false)
		{
			int x = (int) entity.getBounds().getCenterX() - DrawPanel.WIDTH / 2;
			rooms[currentRoom - 1].drawBG(x - 32, (int) entity.getBounds().getCenterX() + entity.height + DrawPanel.WIDTH / 2, x - 26, b);
		}
	}

	public static Room getCurrentRoom()
	{
		return rooms[currentRoom - 1];
	}

	public static void checkCollisions(Entity entity)
	{
		rooms[currentRoom - 1].checkCollisions(entity);
	}

}
