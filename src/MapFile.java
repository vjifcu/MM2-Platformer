import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MapFile
{

	// name of the current Level
	public String fileName = Level.fileName;

	// Saves the map. Used when someone has edited a map and wants to save their changes.
	public void save(String fileName, boolean showPopup)
	{
		Room room = Level.getCurrentRoom();
		BufferedWriter writer = null;
		try
		{
			//Creates a new file or overwrites existing file
			File logFile = new File("mapData/" + Level.name + "/" + Level.fileName + Level.currentRoom + ".txt");

			//Makes a writer, and puts in number of rooms, columns, and rows
			writer = new BufferedWriter(new FileWriter(logFile));
			writer.write(Level.sections + " ");
			writer.write(room.maxX + " " + room.maxY + " ");
			
			// Writes each individual tile in the correct order to the file
			for (int y = 0; y <= room.maxY - 1; y++)
			{
				for (int x = 0; x <= room.maxX - 1; x++)
				{
					writer.write(room.tiles[x][y].getID() + " ");
				}
			}

			// If a popup is desired (auto-save can be enabled, which does not cause popups)
			if (showPopup)
			{
				//Tells user where the map has been saved to
				JFrame save = new JFrame("Save Alert");
				save.add(new JLabel("map saved to: " + logFile.getCanonicalPath()));
				save.pack();
				save.setVisible(true);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				// Close the writer regardless of what happens
				writer.close();
			} catch (Exception e)
			{
			}
		}
	}

	//Used to change the size of a level, to make it longer or shorter.
	public void changeSize(int newX, int newY)
	{
		BufferedWriter writer = null;
		Room room = Level.getCurrentRoom();
		
		//determines difference in rows and columns between current room size and requested room size.
		int differenceX = room.maxX - newX;
		int differenceY = room.maxY - newY;
		try
		{
			//creates new file or overwrites existing file
			File logFile = new File("mapData/" + Level.name + "/" + Level.fileName + Level.currentRoom + ".txt");

			//Makes a writer, and puts in number of rooms, along with new number of columns and rows.
			writer = new BufferedWriter(new FileWriter(logFile));
			writer.write(Level.sections + " ");
			writer.write(newX + " " + newY + " ");
			
			//For the size/area requested, write the tiles to the file (cuts off tiles out of the new boundaries)
			for (int y = 0; y <= room.maxY - 1 && y <= room.maxY - 1 - differenceY; y++)
			{
				for (int x = 0; x <= room.maxX - 1 && x <= room.maxX - 1 - differenceX; x++)
				{
					writer.write(room.tiles[x][y].getID() + " ");
				}
				//If the map is bigger than before, put ! (blank tiles) in the new spaces. For x-axis
				for (int empty = 1; empty <= newX - room.maxX; empty++)
					writer.write("! ");

			}
			//If the map is bigger than before, put ! (blank tiles) in the new spaces. For y-axis
			for (int newLines = 1; newLines <= newY - room.maxY; newLines++)
			{
				for (int empty = 1; empty <= newX; empty++)
					writer.write("! ");
			}

			//Tells user where the new map has been saved to
			JFrame save = new JFrame("Hello World");
			save.add(new JLabel("map saved to: " + logFile.getCanonicalPath()));
			save.pack();
			save.setVisible(true);
		} catch (Exception e)
		{
		}
		try
		{
			// Close the writer regardless of what happens
			writer.close();
		} catch (Exception e)
		{
		}
	}

}