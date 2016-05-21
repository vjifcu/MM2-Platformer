import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Scanner;
//It is 10:31pm and I have been commenting the entire saturday not even joking I am still in my pajamas
//so please excuse it if the comments become a bit less description
public class Room
{
	File mapFile;
	String levelName;
	Scanner sc;
	Scanner scEnemy;
	int maxX, maxY;
	final static int TILESIZE = Tile.TILESIZE;
	Tile[][] tiles;
	Entity[] enemiesBase;
	Entity[] enemies;
	int numEnemies;
	boolean bossRoom = false;
	int bossCounter = 0;

	//Loads the map according to the mapFile passed in. (mapFile being the name of the level)
	public void loadMap(String mapFile)
	{
		this.mapFile = new File(mapFile + ".txt");
		try
		{
			//puts a scanner on the map file
			sc = new Scanner(this.mapFile);
			//puts a scanner on the enemies file
			scEnemy = new Scanner(new File(mapFile + "enemies.txt"));
			sc.next();
			
			//reads the number of columns and rows
			maxX = Integer.parseInt(read(sc));
			maxY = Integer.parseInt(read(sc));
			
			//creates an array of that size
			tiles = new Tile[maxX][maxY];
			
			//reads in all the tiles from the map file and puts them into the array
			for (int y = 0; y <= maxY - 1; y++)
			{
				for (int x = 0; x <= maxX - 1; x++)
				{
					updateTile(x, y, sc.next().charAt(0));
				}

			}
			String holder = read(scEnemy);

			//Create the necessary enemies based on the enemies listed in the enemies file
			if (holder != null)
			{
				numEnemies = Integer.parseInt(holder);
				enemiesBase = new Entity[numEnemies];
				enemies = new Entity[numEnemies];
				for (int i = 1; i <= numEnemies; i++)
				{
					newEnemy(i - 1, Integer.parseInt(read(scEnemy)));
					enemies[i - 1] = enemiesBase[i - 1];
				}
			}

		} catch (Exception e)
		{
			System.out.println(e);
		}

	}

	//Boss script for the intro. It's just really specific stuff for the boss to do during his intro, not worth commenting.
	public void bossUpdate()
	{
		if (bossCounter > 0)
		{
			if (!DrawPanel.player.onGround)
			DrawPanel.player.yv += 1.25;
			DrawPanel.player.drawXV = 0;
			DrawPanel.player.update();
			checkCollisions(DrawPanel.player);
			updateEnemies();

			if (bossCounter > 120)
				drawEnemies();
			else if (bossCounter > 110)
				enemies[0].draw(DrawPanel.b, 2, 1);
			else if (bossCounter > 1)
				enemies[0].draw(DrawPanel.b, 3, 1);
			else
				drawEnemies();
		} else
		{
			DrawPanel.player.hitCooldown = 0;
			if (!DrawPanel.player.onGround)
			DrawPanel.player.yv += 1.25;
			DrawPanel.player.drawXV = 0;
			DrawPanel.player.update();
			checkCollisions(DrawPanel.player);
			updateEnemies();
			
			//Once the player defeats the boss, beam up and change to the next level.
			if (bossCounter == -100)
			{
				DrawPanel.player.beamDown = false;
				DrawPanel.player.beaming = true;
			} else if (bossCounter < -100)
			{
				if (DrawPanel.player.beaming == false)
				{
					DrawPanel.currentLevel = "flash man";
					DrawPanel.restart(true);
				}
			}
			System.out.println(bossCounter);
		}
		bossCounter--;
	}

	//Updates all the enemies in the room
	public void updateEnemies()
	{
		for (int i = 1; i <= numEnemies; i++)
		{
			if ( enemies[i-1].getAlive() == false && !(DrawPanel.player.x > enemiesBase[i - 1].getX() - 250 && DrawPanel.player.x < enemiesBase[i - 1].getX() + enemiesBase[i - 1].getWidth() + 270))
			{
				resetEnemy(i-1);
			}

			if (enemies[i - 1].getHealth() > 0)
			{
				enemies[i - 1].move();
				if (enemies[i - 1].collision == true)
					Level.checkCollisions(enemies[i - 1]);
			}
			enemies[i - 1].update();
		}
	}

	//Draws all the enemies in the room
	public void drawEnemies()
	{
		for (int i = 1; i <= numEnemies; i++)
		{
			if (enemies[i - 1].getAlive() == true)
				enemies[i - 1].draw(DrawPanel.b);
		}
	}

	//Resets the enemy if their spawn positions go out of the player's view
	public void resetEnemy(int index)
	{
		if (enemiesBase[index] instanceof DrillEnemy)
			enemies[index] = new DrillEnemy(enemiesBase[index].getX(), enemiesBase[index].getY(),
					enemiesBase[index].getWidth(), enemiesBase[index].getHeight(), enemiesBase[index].speed);
		else if (enemiesBase[index] instanceof CogClownEnemy)
			enemies[index] = new CogClownEnemy(enemiesBase[index].getX(), enemiesBase[index].getY(),
					enemiesBase[index].getWidth(), enemiesBase[index].getHeight(), enemiesBase[index].speed);
		else if (enemiesBase[index] instanceof BarrelEnemy)
			enemies[index] = new BarrelEnemy(enemiesBase[index].getX(), enemiesBase[index].getY(),
					enemiesBase[index].getWidth(), enemiesBase[index].getHeight(), enemiesBase[index].speed);
		else if (enemiesBase[index] instanceof ShooterEnemy)
			enemies[index] = new ShooterEnemy(enemiesBase[index].getX(), enemiesBase[index].getY(),
					enemiesBase[index].getWidth(), enemiesBase[index].getHeight(), (int)enemiesBase[index].speed);
		else if (enemiesBase[index] instanceof MechEnemy)
			enemies[index] = new MechEnemy(enemiesBase[index].getX(), enemiesBase[index].getY(),
					enemiesBase[index].getWidth(), enemiesBase[index].getHeight(), enemiesBase[index].speed);
	}
	
	//Creates a new enemy
	public void newEnemy(int index, int enemyType)
	{
		try
		{
			if (enemyType == 1)
				enemiesBase[index] = new ChandEnemy(Integer.parseInt(read(scEnemy)) * 32 + 6, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 2)
				enemiesBase[index] = new DrillEnemy(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 3)
				enemiesBase[index] = new CogClownEnemy(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 4)
				enemiesBase[index] = new BarrelEnemy(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 5)
				enemiesBase[index] = new SpringerEnemy(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 10)
				enemiesBase[index] = new MetalMan(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 11)
				enemiesBase[index] = new ShooterEnemy(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 12)
				enemiesBase[index] = new MechEnemy(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
			else if (enemyType == 13)
				enemiesBase[index] = new BananaEnemy(Integer.parseInt(read(scEnemy)) * 32, Integer.parseInt(read(scEnemy)) * 32,
						Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)), Integer.parseInt(read(scEnemy)));
		} catch (Exception e)
		{
		}
	}

	//Updates the chosen tile with the given tileID
	public void updateTile(int x, int y, char newID)
	{
		if (Level.name == "metal man")
		{
			if (newID >= 'H' && newID <= 'J')
				tiles[x][y] = new OffsetTile(x, y, newID);
			else if (newID >= '5' && newID <= '7')
				tiles[x][y] = new ConveyerTile(x, y, newID);
			else if (newID == '4')
				tiles[x][y] = new BossDoorTile(x, y, newID);
			else
				tiles[x][y] = new Tile(x, y, newID);
		} else if (Level.name == "flash man")
		{
			if (newID >= '"' && newID <= '6')
				tiles[x][y] = new IceTile(x, y, newID);
			else
				tiles[x][y] = new Tile(x, y, newID);
		}
	}

	public String read(Scanner sc) throws Exception
	{

		while (sc.hasNext())
		{
			return sc.next();
		}
		return null;
	}

	//Draws the background tiles
	public void drawBG(int startX, int endX, int xBG, Graphics2D b)
	{
		b.setColor(Color.BLACK);
		b.fillRect(0, 0, DrawPanel.WIDTH, DrawPanel.HEIGHT + 32);
		for (int x = tile(startX); x <= tile(endX) - 1; x++)
		{
			for (int y = 0; y <= maxY - 1; y++)
			{
				try
				{
					if (tiles[x][y].background == true)
						tiles[x][y].draw(b, xBG);
				} catch (Exception e)
				{
				}
			}
		}
	}

	//Draws the foreground tiles
	public void drawRoom(int startX, int endX, int xBG, Graphics2D b)
	{
		for (int x = tile(startX); x <= tile(endX) - 1; x++)
		{
			for (int y = 0; y <= maxY - 1; y++)
			{
				try
				{
					if (tiles[x][y].background == false)
						tiles[x][y].draw(b, xBG);
				} catch (Exception e)
				{
				}
			}
		}

		if (bossCounter != 0)
			bossUpdate();

		Tile.counterAdd();
	}

	//Draws all the tiles
	public void drawRoom(int drawX, int drawY, Graphics2D b)
	{
		for (int x = 0; x <= maxX - 1; x++)
		{
			for (int y = 0; y <= maxY - 1; y++)
				tiles[x][y].draw(b, drawX, drawY, false);
		}
	}

	//Divides the given number by 32. It is integer rounding so it rounds down to the nearest 32.
	//It "tiles" the number in the sense that it snaps the number to the tile it belongs in.
	public static int tile(double num)
	{
		return (int) num / TILESIZE;
	}

	/**The collision works in an interesting way. Very very few actions affect an entity's x and y variables directly.
	*Instead, they affect their x velocity and y velocity variables. Before the xv and yv are added to the x and y, they are
	*run through this collision check. The check takes the yv first and determines if the movement would cause the entity to
	*end up inside of, or crossing through, a tile. If it does, then yv is reduced to bring the player just to the edge of the tile.
	*This way, the player will never end up inside a tile, no matter how quickly they are moving. This same process is repeated
	*for the x velocity. This solves the "punching through a piece of paper" issue With other types of collision.**/
	//Checks collisions.
	public void checkCollisions(Entity entity)
	{
		entity.onGround = false;
		int offset;

		try
		{
			if (entity.yv >= 0)
				offset = 1;
			else
				offset = -1;

			outerloop: for (int y = 1; y <= 2; y++)
			{
				for (int x = 0; x <= tile(entity.x + entity.getBG() + entity.width) - tile(entity.x + entity.getBG()); x++)
				{
					if (tiles[tile(entity.x + entity.getBG()) + x][tile(entity.getBounds().getCenterY()) + y * offset].background == false)
					{
						if (offset == 1)
							entity.yv = Math.min(entity.yv, (tile(entity.getBounds().getCenterY()) + y * offset) * TILESIZE
									- (entity.y + entity.height) - offset);
							if (!entity.up && entity instanceof Player)
								entity.yv = Math.max(entity.yv, 0);
						if (offset == -1)
							entity.yv = Math.max(entity.yv, (tile(entity.getBounds().getCenterY()) + y * offset + 1) * TILESIZE - entity.y - offset);
						if (tiles[tile(entity.x + entity.getBG()) + x][tile(entity.y + entity.height + 5)].background == false)
						{
							entity.onGround = true;
							if (!(entity instanceof CogEnemy) && !(entity instanceof MetalMan))
								(tiles[tile(entity.x + entity.getBG()) + x][tile(entity.y + entity.height + 15)]).groundCollision(entity);
						}
						if (tiles[tile(entity.x + entity.getBG()) + x][tile(entity.y + entity.height + 16)].background == false)
						{
							if (!entity.up)
								entity.canJump = true;
						}
						break outerloop;
					}
				}
			}
		} catch (Exception e)
		{
		}

		try
		{
			if (entity.xv >= 0)
				offset = 1;
			else
				offset = -1;
			outerloop: for (int x = 1; x <= 2; x++)
			{
				for (int y = 0; y <= tile(entity.y + entity.height) - tile(entity.y); y++)
				{
					if (tiles[tile(entity.getBounds().getCenterX() + entity.getBG()) + x * offset][tile(entity.y) + y].background == false)
					{
						if (offset == 1)
						{
							entity.xv = Math.min(entity.xv, (int) ((tile(entity.getBounds().getCenterX() + entity.getBG()) + x) * TILESIZE - (entity.x+ entity.width + entity.getBG()))- offset);
							entity.xv = Math.max(-1, entity.xv);
						}
						else
						{
							entity.xv = Math.max(entity.xv, (int) ((tile(entity.getBounds().getCenterX() + entity.getBG()) + x * offset + 1)
									* TILESIZE - (entity.x + +entity.getBG()))
									- offset);
							entity.xv = Math.min(entity.xv, 1);
						}
						break outerloop;
					}
				}
			}

		} catch (Exception e)
		{
		}

		//If the player goes to the edge of the room's wall, change the room
		if (entity.x + entity.getBG() + entity.width > Level.getCurrentRoom().maxX * 32 && entity instanceof Player)
			Level.changeRoom(1, 0, false, DrawPanel.b);
		//If the player falls down at the edge of a level, change the room
		else if (entity.y + entity.height + entity.yv > DrawPanel.HEIGHT && entity.displayX != 0 && entity instanceof Player)
			Level.changeRoom(0, 1, false, DrawPanel.b);
		//If the player falls down away from the edge of a room, kill the player
		if (entity.y > DrawPanel.HEIGHT)
		{
			entity.setAlive(false);
		}
	}

}
