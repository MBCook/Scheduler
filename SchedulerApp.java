/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JOptionPane;
import java.util.ArrayList;

public class SchedulerApp extends JFrame implements ListSelectionListener, ActionListener {

	private static SchedulePanel sp;

	private static Scheduler sched;

	private static JSplitPane hPane;
	private static JSplitPane vPane;

	private static JLabel courseLabel;
	private static JLabel scheduleLabel;

	private static Box courseBox;
	private static Box scheduleBox;

	private static JButton makeButton;
	private static JButton resetButton;

	private static CourseList cl;
	private static ScheduleList sl;

	private static JScrollPane coursePane;
	private static JScrollPane schedulePane;

	private static Schedule[] schedList;

	public SchedulerApp() {
		super("Scheduler");
		makeGUI();

		pack();
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// With any luck, that's it
//		System.out.println("End of SchedulerApp()");
	}

	private void makeGUI()  {
		Container c = getContentPane();

		/* Here is what our layout looks like:
		*
		*  ---------------------------------------------------
		*  |/-----------------\|                             |
		*  ||      label      ||                             |
		*  ||    CourseList   ||                             |
		*  ||      make       ||                             |
		*  ||      reset      ||                             |
		*  ||-----------------||         SchedulePanel       |
		*  ||      label      ||                             |
		*  ||   ScheduleList  ||                             |
		*  ||                 ||                             |
		*  |\-----------------/|                             |
		*  ---------------------------------------------------
		*
		* Where each of the containers is a JSplitPane
		*
		* Inside the left hand parts, there is a box layout model with
		*	label and a JScrollPane containing a JList implementation
		*
		*/

		courseLabel = new JLabel("Course Choices:");
		scheduleLabel = new JLabel("Schedule Choices:");

		sp = new SchedulePanel(640, 480);
		cl = new CourseList();
		sl = new ScheduleList();

		sl.addListSelectionListener(this);

		makeButton = new JButton("Calculate Schedules");

		makeButton.addActionListener(this);
		makeButton.setActionCommand("recalc");

		resetButton = new JButton("Reset All");

		resetButton.addActionListener(this);
		resetButton.setActionCommand("reset");

		coursePane = new JScrollPane(cl);
		schedulePane = new JScrollPane(sl);

		courseBox = Box.createVerticalBox();
		scheduleBox = Box.createVerticalBox();

		courseBox.add(courseLabel);
		courseBox.add(coursePane);
		courseBox.add(makeButton);
		courseBox.add(resetButton);

		scheduleBox.add(scheduleLabel);
		scheduleBox.add(schedulePane);

		vPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, courseBox, scheduleBox);
		hPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vPane, sp);

		c.add(hPane, "Center");

//		System.out.println("End of makeGUI()");
	}	

	public static void main(String[] args) {

		System.out.println("");

		schedList = new Schedule[0];

		sched = new Scheduler();

		if (sched == null) {
			System.out.println("Unable to create a new scheduler!");
			System.out.println("Giving up and exiting.");
			System.exit(0);
		}

//		sched.fillFromFile("/Users/michael/Programming/Java/Scheduler/testdata.txt");

		try {
			sched.fillFromFile("testdata.txt");
		} catch (SchedulerException e) {
			System.out.println("Caught exception during fillFromFile: " +
									e.getType() + ": " + e.getMessage());
			e.printStackTrace();
			System.out.println("");
			System.exit(0);
		}

//		System.out.println("");

		SchedulerApp us = new SchedulerApp();

		cl.setScheduler(sched);
		resetEverything();
	}

	public void recalcEverything() {

		ArrayList<String[]> possibilities = null;

		String[] classList = cl.getSelected();

		if ((classList == null) || (classList.length == 0)) {
			sl.clearList();
			return;			// Nothing else to do
		}

		try {
			possibilities = sched.buildPossibles(classList);
		} catch (SchedulerException e) {
			if (e.getType() == "Conflict") {
				JOptionPane.showMessageDialog(this, "There are no valid schedules for those courses." +
													"\n" + e.getMessage(), "Conflict", JOptionPane.WARNING_MESSAGE);
				sl.clearList();
				return;	// Bail out since we can't continue.
			} else {
				JOptionPane.showMessageDialog(this, "Couldn't build possibilities list:\n\n" +
														"Type: " + e.getType() + "\n\n" +
														e.getMessage() + "\n\nExiting.",
														"Error", JOptionPane.ERROR_MESSAGE);
	//			System.out.println("Couldn't build possibilities list: " +
	//									e.getType() + ": " + e.getMessage());
				e.printStackTrace();
				System.out.println("");
				System.exit(0);
			}
		}

		String[] sections;

//		System.out.println("");
//		System.out.println("Now checking for valid schedules...");
//		System.out.println("");

		ArrayList<Schedule> validList = null;

		try {
			validList = sched.possiblesToSchedules(classList, possibilities);
		} catch (SchedulerException e) {
			JOptionPane.showMessageDialog(this, "Couldn't build schedules list:\n\n" +
												"Type: " + e.getType() + "\n\n" + e.getMessage() +
												"\n\nExiting.",
												"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.out.println("");
			System.exit(0);
		}

//		System.out.println("Found " + validList.size() + " valid schedules.");
//		System.out.println("");

		// Now we check to see if all courses are present that will be needed

		if (schedList.length > 0) {
			ArrayList<String> missing = schedList[0].getMissingCoReqs();
	
			if (missing != null) {
				String theMessage = "The following co-requirements are missing:\n\n";
	
				theMessage = theMessage + missing.get(0);
	
				for (int i = 1; i < missing.size(); i++)
					theMessage = theMessage + ", " + missing.get(i);
	
				JOptionPane.showMessageDialog(this, theMessage, "Missing Co-Reqs",
														JOptionPane.WARNING_MESSAGE);
			}
		}

		schedList = validList.toArray(schedList);

		sl.setScheduleList(schedList.length);

		try {
			sp.setScheduleToDraw(schedList[0]);
		} catch (SchedulerException e) {
			JOptionPane.showMessageDialog(this, "Couldn't get schedule to draw:\n\n" +
													"Type: " + e.getType() + "\n\n" + e.getMessage() +
													"\n\nExiting.",
													"Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.out.println("");
			System.exit(0);	
		}

//		System.out.println("Looking at schedule " + schedNum + ".");
//		System.out.println("");

		// Now select the first schedule if there is a valid one

		if (schedList.length >= 1) {
			sl.setSelectedIndex(0);
		}
	}

	public static void resetEverything() {
		schedList = new Schedule[0];
		sl.clearList();
		cl.unselectAll();
		sp.dontDraw();
		sp.repaint();
		cl.repaint();
		sl.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == makeButton) {
			if (e.getActionCommand() == "recalc") {
				// The coursee selection changed.
	
				recalcEverything();
			} else {
				System.out.println("Asked to perform unknown action for button: " +
										e.getActionCommand());
			}
		} else if (e.getSource() == resetButton) {
			if (e.getActionCommand() == "reset") {
				// Reset everything

				resetEverything();	
			} else {
				System.out.println("Asked to perform unknown action for button: " +
										e.getActionCommand());
			}
		} else {
			System.out.println("Action sent to us by unknown object: " + e.getSource());
		}
	}

	// This gets called when the selections change

	public void valueChanged(ListSelectionEvent e) {
		// Find out who had their selection changed
		if (e.getSource() == sl) {
			// The schedule list had a selection change

			if (schedList.length != 0) {
				int selectedNum = sl.getSelectedSchedule();
				if (selectedNum != -1) {
					try {
						sp.setScheduleToDraw(schedList[selectedNum]);
					} catch (SchedulerException ee) {
						// If we are here, then schedList[selectedNum] was null
						JOptionPane.showMessageDialog(this, "Couldn't set schedule to draw:\n\n" +
																"Type: " + ee.getType() +
																"\n\n" + ee.getMessage() +
																"\n\nExiting.",
																"Error", JOptionPane.ERROR_MESSAGE);
						ee.printStackTrace();
						System.out.println("");
						System.exit(0);
					}
					sp.repaint();
				}
			}

//			System.out.println("");
//			System.out.println("Schedule selection changed.");
//			System.out.println("");

		} else {
			System.out.println("");
			System.out.println("Got a valueChanged notification from unknown object: " +
									e.getSource());
			System.out.println("");
		}
	}
}