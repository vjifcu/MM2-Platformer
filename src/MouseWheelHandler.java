import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

//Handles mouse wheel scrolling.
public class MouseWheelHandler implements MouseWheelListener
{
	//Stores currently selected tile
	char currentID = '#';

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		//When the mouse wheel rotates, change the currently selected tile to the next/previous one
		//depending on scroll direction
		if ((e.getWheelRotation() < 0 && currentID >= '"') || (e.getWheelRotation() > 0 && currentID <= 'M') )
		currentID = (char)((int)currentID + e.getWheelRotation());
		
		Level.mouseTile.setTile(0, 0, currentID);

	}

}
