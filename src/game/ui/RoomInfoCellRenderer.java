package game.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by neczp on 2016. 10. 11..
 */
public class RoomInfoCellRenderer implements ListCellRenderer {
	private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer ();

	public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);
		//
		//		RoomConnection entry = (RoomConnection) value;
		//
		//		if (!isSelected) {
		//			renderer.setForeground (Color.blue);
		//		}
		//		renderer.setText (entry.gameName + ":   " + entry.currentUserCount + "/" + entry.maxUserCount);

		return renderer;
	}
}
