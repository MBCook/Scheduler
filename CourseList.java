/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.TreeSet;

// Checkbox code from http://www.devx.com/tips/Tip/5342

public class CourseList extends JList {
	private Scheduler sched;
	private JCheckBox[] ourBoxes;

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);	// From that site

	public CourseList() {
		super();

		// This stuff is from that site

		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					int index = locationToIndex(e.getPoint());

					if (index != -1) {
						JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
						checkbox.setSelected(!checkbox.isSelected());
						repaint();
					}
				}
			}
		);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// End stuff from that site

		sched = null;
		ourBoxes = new JCheckBox[0];

		setListData(ourBoxes);
	}

	public void unselectAll() {
		for (int i = 0; i < ourBoxes.length; i++) {
			ourBoxes[i].setSelected(false);
		}
	}

	// This is from that site

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index,
														boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(isSelected ?
				getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ?
				getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ?
				UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
         return checkbox;
      }
   }

	// End stuff from that site

	public String[] getSelected() {
		ArrayList<String> theList = new ArrayList<String>();

		for (int i = 0; i < ourBoxes.length; i++)
			if (ourBoxes[i].isSelected())
				theList.add(ourBoxes[i].getText());

		String[] forType = new String[0];

		return theList.toArray(forType);
	}

	public void setScheduler(Scheduler theScheduler) {
		sched = theScheduler;
		updateClassList();
	}

	public void updateClassList() {
		if (sched != null) {
			Enumeration<String> e = sched.getCourses();

			String[] courseNames = new String[sched.getCourseCount()];

			int i = 0;

			while (e.hasMoreElements()) {
				courseNames[i++] = e.nextElement();;
			}

			// Now we have the list, we need to sort it.
			// You'd think there'd be an easier way than putting everything
			//		in a tree set then back into the old array.

			TreeSet<String> ourTreeSet = new TreeSet<String>();

			for (i = 0; i < courseNames.length; i++) {
				ourTreeSet.add(courseNames[i]);
			}

			// Now get it back

			courseNames = ourTreeSet.toArray(courseNames);

			// Now we display it

			ourBoxes = new JCheckBox[sched.getCourseCount()];

			for (i = 0; i < sched.getCourseCount(); i++) {
				ourBoxes[i] = new JCheckBox(courseNames[i]);
			}

			setListData(ourBoxes);

//			setListData(courseNames);
		} else {
			ourBoxes = new JCheckBox[0];
			setListData(ourBoxes);
		}
	}
}