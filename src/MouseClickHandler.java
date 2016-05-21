import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//Handles mouse clicks. Most of these methods are mandatory to override and I haven't added anything to them
class MouseClickHandler implements MouseListener
{

	// The mouse has its own tile. This is the tile that the user has
	// currently selected to place onto the map
	static char tile = '0';
	static int currentMouseButton;

	public static void changeTile(char newTile)
	{
		tile = newTile;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// Get the tile coordinates that the user has clicked in
		int x = (int) (((e.getX() - 8) / DoubleBuff.scaleW + Tile.offset) / 32);
		int y = (int) (((e.getY() - 28) / DoubleBuff.scaleH) / 32);

		try
		{
			// If the left mouse button is clicked
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				//Replace the clicked tile with the mouse's current Tile
				Level.getCurrentRoom().updateTile(x, y, Level.mouseTile.getID());
			// If the right mouse button is clicked
			} else if (e.getButton() == MouseEvent.BUTTON3)
			{
				//Replace the clicked tile with a blank tile
				Level.getCurrentRoom().updateTile(x, y, '!');
			}
		} catch (Exception er)
		{
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		currentMouseButton = e.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		currentMouseButton = -1;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

}
