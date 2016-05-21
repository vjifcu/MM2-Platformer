import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

//Handles mouse movement. Most of these methods are mandatory to override and I haven't added anything to them
public class MouseMotionHandler implements MouseMotionListener
{

	public static int mouseX;
	public static int mouseY;
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		//Update mouseX and mouseY with the current position of the mouse
		//Used to draw transparent tile over mouse
		mouseX = (int) (e.getX()/DoubleBuff.scaleW) - 4 + (Tile.offset%32);
		mouseY = (int) ((e.getY() - 28)/DoubleBuff.scaleH);
		
		//Update x and y with the current tile position of the mouse
		//Used for updating tiles
		int x = (int) (((e.getX() - 8) / DoubleBuff.scaleW + Tile.offset) / 32);
		int y = (int) (((e.getY()- 28) / DoubleBuff.scaleH) / 32);
		
		try{
			//If the mouse has left clicked
			if (MouseClickHandler.currentMouseButton == 1)
			{
				//Replace the tile clicked with the mouse's current tile
				Level.getCurrentRoom().updateTile(x, y, Level.mouseTile.getID());
			//If the mouse has right clicked
			} else if (MouseClickHandler.currentMouseButton == 3)
			{
				//Replace the tile clicked with the blank tile
				Level.getCurrentRoom().updateTile(x, y, '!');
			}
			}catch(Exception er){}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		//Update mouseX and mouseY with the current position of the mouse
		mouseX = (int) (e.getX()/DoubleBuff.scaleW) - 4 + (Tile.offset%32);
		mouseY = (int) ((e.getY() - 28)/DoubleBuff.scaleH);
	}

}
