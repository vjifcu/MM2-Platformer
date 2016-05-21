// Victor Jifcu's Culminating Assignment//

// Fun facts - This game is 5,251 lines of code
// There are a total of 27 classes.
// Largest Class - MetalMan - 566 lines - 10.78%
// 2nd largest - Player - 473 lines - 9.01%
// 3rd largest - BarrelEnemy - 447 lines - 8.51%
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineListener;
import javax.swing.*;

import java.awt.event.*;
import java.awt.image.*;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/*****************************************************************************************************/

// Window Setup
public class DoubleBuff
{
	JFrame window;
	DrawPanel panel;
	
	// Determines scaling factor from monitor resolution
	static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	static double scaleH = (int) (dim.getHeight() / 480);
	static double scaleW = scaleH * 1.1;

	//Creates game window
	public DoubleBuff()
	{
		window = new JFrame("Megaman 2");
		panel = new DrawPanel();
		
		// Resizes window according to monitor resolution
		window.setSize((int) (DrawPanel.WIDTH * scaleW + 17), (int) (DrawPanel.HEIGHT * scaleH + 39));
		window.setLocationRelativeTo(null);
		
		//Add all the listeners for mouse input
		window.addMouseListener(new MouseClickHandler());
		window.addMouseMotionListener(new MouseMotionHandler());
		window.addMouseWheelListener(new MouseWheelHandler());
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().add(panel);
		
		// Creates blank image to use as cursor
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Replaces mouse cursor with blank image when cursor is within window
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame
		window.getContentPane().setCursor(blankCursor);
		window.setVisible(true);
		

	}

	public void go()
	{
		panel.startGame();
	}

	public static void main(String[] args)
	{
		// Starts and runs game
		DoubleBuff game = new DoubleBuff();
		game.go();
	}
}

/*****************************************************************************************************/

class DrawPanel extends JPanel implements KeyListener
{
	// Audio variables, used for BG music and sound effects
	static AudioInputStream audioIn;
	static LineListener bgListener;
	static Clip clip;

	final static int WIDTH = 512;
	static final int HEIGHT = 480;
	
	// Used for double buffering
	static BufferedImage buffer;
	static Graphics2D b;
	
	static Player player;
	static Level level;
	
	// Holds string for the current level. Metal man is the first level.
	//changes to flash man for second level.
	static String currentLevel = "metal man";
	Room room1 = new Room();

	boolean paused = false;

	// Number of rows and columns of tiles
	static int maxX;
	static int maxY;

	public static MapFile map = new MapFile();

	public DrawPanel()
	{
		setIgnoreRepaint(true);
		addKeyListener(this);
		setFocusable(true);
	}

	/************************** KeyListener needs these **********************************************/
	public void keyTyped(KeyEvent e)
	{
	}

	// Keyboard input
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		if (!paused)
		{
			if (key == KeyEvent.VK_LEFT)
				player.left = true;
			if (key == KeyEvent.VK_RIGHT)
				player.right = true;
			if (key == KeyEvent.VK_X)
				player.up = true;
			if (key == KeyEvent.VK_Z)
				player.shoot = true;
			if (key == KeyEvent.VK_DOWN)
				player.down = true;
			if (key == KeyEvent.VK_P)
				paused = true;
			if (key == KeyEvent.VK_ENTER)
				map.save("map", true);
			
			// Used for edit mode
			if (key == KeyEvent.VK_SPACE)
			{
				if (Level.editMode)
					Level.editMode = false;
				else
				{
					Level.editMode = true;
					player.yv = 0;
					player.xv = 0;
				}
			}
			
			// Used for resizing current room. Not meant for player use.
			if (key == KeyEvent.VK_CONTROL)
			{
				int newX = Integer.parseInt(JOptionPane.showInputDialog("new map X value:"));
				int newY = Integer.parseInt(JOptionPane.showInputDialog("new map Y value:"));
				map.changeSize(newX, newY);
			}
		} else
		{

			if (key == KeyEvent.VK_P)
			{
				paused = false;
				System.out.println("unpaused");
			}
		}
	}

	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_LEFT)
			player.left = false;
		if (key == KeyEvent.VK_RIGHT)
			player.right = false;
		if (key == KeyEvent.VK_X)
			player.up = false;
		if (key == KeyEvent.VK_Z)
			player.shoot = false;
		if (key == KeyEvent.VK_DOWN)
			player.down = false;
	}

	/************************** end methods needed for KeyListener ****************************************/

	// Plays sound effects. Is called from enemy and player classes for various sounds.
	public static synchronized void playSound(final String url)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("sounds/" + url));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e)
				{
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	// Creates player, double buffering image, level, tiles, sets up BG music, etc
	public static void initialize(String levelName) throws FileNotFoundException
	{

		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		player = new Player(270, 260, 24, 41, 4.5);

		level = new Level("map", levelName);

		try
		{
			// If level has musical intro before main BG loop, play it first.
			try
			{
				audioIn = AudioSystem.getAudioInputStream(new File("sounds/" + Level.name + " intro.wav"));
				clip = AudioSystem.getClip();
				clip.open(audioIn);
			// Otherwise, play BG music like normal.
			} catch (Exception e)
			{
				playBGMusic();
			}
			
		} catch (Exception e)
		{
			System.out.println(e);
		}

	}
	
	//Plays the looping background music
	public static void playBGMusic ()
	{
		try{
			audioIn = AudioSystem.getAudioInputStream(new File("sounds/" + Level.name + ".wav"));
			clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.loop(clip.LOOP_CONTINUOUSLY);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	// drawRect wrapper. Used for drawing collision boxes when testing new enemies.
	public static void drawRect(int x, int y, int w, int h, Color c, boolean fill)
	{
		b.setColor(c);
		if (fill)
			b.fillRect(x, y, w, h);
		else
			b.drawRect(x, y, w, h);
	}

	// Move player
	// This method used to move other enemies but that is now done in the room's update loop.
	public void moveEntities()
	{
		player.move();
	}

	// Update player
	// This method used to update other enemies but that is now done in the room's update loop.
	public void updateEntities()
	{
		player.update();
	}

	// Check player collision
	// This method used to check enemy collision but that is now done in the room's update loop.
	public void collisions()
	{
		if (!Level.editMode)
		{
			Level.checkCollisions(player);
		}
	}

	// Drawing is done here
	public void drawBuffer()
	{
		b = buffer.createGraphics();

		// Draw the level's tiles
		Level.drawBG(player, b);

		// If it is a boss room or the first stage, draw the enemies on top of the level
		if (!Level.name.equals("metal man") || Level.getCurrentRoom().bossRoom)
		{
			Level.drawLevel(player, b);
			if (!Level.changing)
			Level.getCurrentRoom().drawEnemies();
		// Otherwise, draw the enemies underneath the foreground level tiles
		} else
		{
			if (!Level.changing)
			Level.getCurrentRoom().drawEnemies();
			Level.drawLevel(player, b);
		}

		player.draw(b);
		player.drawHealth(b);

		b.dispose();
	}

	// takes what was drawn in the RAM and places it on the screen
	public void drawScreen()
	{
		Graphics2D g = (Graphics2D) this.getGraphics();

		//Scale the image, same as the window scaling
		g.scale(DoubleBuff.scaleW, DoubleBuff.scaleH);
		g.drawImage(buffer, 0, 0, this);
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

	// Main update method, calls specific update methods
	public void update()
	{
		if (!Level.changing)
		{
			moveEntities();
			collisions();
		}
		updateEntities();

	}

	// Changes room upon death or level completion
	public static void restart(boolean nextLevel)
	{
		// If player dies during boss battle, send them to the room just before the boss
		if (Level.getCurrentRoom().bossRoom && !nextLevel)
			Level.currentRoom--;
		
		// If they defeat the boss, set the current room back to 1
		if (nextLevel)
			Level.currentRoom = 1;
		
		try
		{
			// Stop current level's background music
			clip.stop();
			clip.close();
			// Initialize everything again (also restarts music)
			initialize(currentLevel);
		} catch (Exception e)
		{
			System.out.println(e);
		}
	}

	/** GAME LOOP **/
	public void startGame()
	{

		try
		{
			initialize(currentLevel);
		} catch (FileNotFoundException e1)
		{
		}

		// Delay time is adjusted dynamically according to FPS to reduce stutter
		final int FPS = 35;
		final long OPTIMAL_TIME = 1000000000 / FPS;
		long now;
		long ms_wait;

		while (true)
		{
			if (!paused)
			{

				try
				{
					now = System.nanoTime();
					
					// If not in a boss intro, run the main update methods for the player
					if (Level.getCurrentRoom().bossCounter == 0)
					{
						update();
						
						// If not changing between rooms, update the enemies
						if (!Level.changing)
							Level.getCurrentRoom().updateEnemies();
					}
					
					drawBuffer();
					drawScreen();

					// If BG music stops, run it again
					if (!clip.isRunning())
						playBGMusic();

						// If the player dies, restart. do not advance.
						if (player.deathCounter == 125)
							restart(false);
						else
						{
							// Adjusts delay to compensate for time it takes to run the loop, which can differ randomly.
							// This prevents stutter and ensures a smooth, even frame rate
							ms_wait = (now - System.nanoTime() + OPTIMAL_TIME) / 1000000;
							if (ms_wait > 0)
								Thread.sleep(ms_wait);
						}

				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		}
	}
}

