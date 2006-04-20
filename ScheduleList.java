/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScheduleList extends JList {

	String[] noSchedules = {"No schedules"};

	public ScheduleList() {
		super();
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clearList();
	}

	public void setScheduleList(int scheduleCount) {
		if (scheduleCount < 1) {
			clearList();
		} else {
			String[] temp = new String[scheduleCount];

			for (int i = 0; i < scheduleCount; i++)
				temp[i] = "Schedule " + i;
			setListData(temp);
		}
	}

	public boolean scheduleSelected() {
		if ((String) getSelectedValue() == noSchedules[0])
			return false;
		else if (getSelectedValue() == null)
			return false;
		return true;
	}

	public int getSelectedSchedule() {
		if (scheduleSelected())
			return getSelectedIndex();
		return -1;
	}

	public void clearList() {
		setListData(noSchedules);
		clearSelection();
	}
}