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

		RoomInfo entry = (RoomInfo) value;

		if (!isSelected) {
			renderer.setForeground (Color.blue);
		}
		renderer.setText (entry.mGameName + " (" + entry.mMapName + ") : " + entry.mUserCount + "/" + entry.mMaxUserCount);

		return renderer;
	}
}
