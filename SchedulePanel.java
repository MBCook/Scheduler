/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SchedulePanel extends JPanel {

	private int width, height;

	private final int START_TIME = 700;
	private final int END_TIME = 2200;
	private final int TIME_LENGTH = ((END_TIME - START_TIME) / 100) * 60;
	private final int DAYS_IN_WEEK = 7;
	private final int BORDER_SIZE = 20;		// Left hand border is triple this

	private Font labelFont;
	private Font courseFont;
	private FontMetrics labelMetrics;
	private FontMetrics courseMetrics;

	private Schedule scheduleToDraw;

	private ScheduleColor ourColors;

	private boolean drawSched;

	public SchedulePanel(int w, int h) {

		width = w;			// Self explanitory
		height = h;

		setBackground(Color.white);
		setPreferredSize(new Dimension(width, height));

		setFocusable(false);

		setDoubleBuffered(true);	// Request double buffering

		scheduleToDraw = null;

		drawSched = false;

		ourColors = new ScheduleColor();

		// Setup our font

		labelFont = new Font("SansSerif", Font.PLAIN, 8);
		courseFont = new Font("SansSerif", Font.BOLD, 11);

		labelMetrics = this.getFontMetrics(labelFont);
		courseMetrics = this.getFontMetrics(courseFont);
	}

	public Schedule getSchedule() {
		return scheduleToDraw;
	}

	public void dontDraw() {
		drawSched = false;
	}

	public void setScheduleToDraw(Schedule sched) throws SchedulerException {
		if (sched == null)
			throw new SchedulerException("Input", "Told to draw a null schedule.");

		scheduleToDraw = sched;
		drawSched = true;
	}

	public int sectionsBeingDrawn() {
		if (scheduleToDraw != null)
			return scheduleToDraw.getCourseCount();
		else
			return -1;
	}

	protected void paintComponent(Graphics g) {
		// Draw things! First clear the background

		g.setColor(Color.WHITE);

		g.fillRect(0, 0, width, height);

		// Now draw the grid of classes. First horizontal lines, then virticle

		g.setColor(Color.BLACK);

		int i, x, y, yEnd;

		for (i = START_TIME; i <= END_TIME; i += 100) {
			try {
				y = timeToY(i);
				g.drawLine(dayToX(0), y, dayToX(7), y);
			} catch (SchedulerException e) {
				System.out.println("WARNING: Caught exception painting horizontal lines: " +
										e.getType() + ": " + e.getMessage());
				e.printStackTrace();
				System.out.println("");
			}
		}

		try {
			y = timeToY(START_TIME);
			yEnd = timeToY(END_TIME);

			for (i = 0; i <= DAYS_IN_WEEK; i++) {
				try {
					x = dayToX(i);
					g.drawLine(x, y, x, yEnd);
				} catch (SchedulerException e) {
					System.out.println("WARNING: Caught exception painting vertical lines: " +
											e.getType() + ": " + e.getMessage());
					e.printStackTrace();
					System.out.println("");
				}
			}
		} catch (SchedulerException e) {
			System.out.println("WARNING: Caught exception painting vertical lines: " +
									e.getType() + ": " + e.getMessage());
			e.printStackTrace();
			System.out.println("");
		}



		// Now we draw some lables (days first)

		g.setFont(labelFont);

		String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
								"Friday", "Saturday"};

		try {
			y = timeToY(START_TIME) - 3;					// 3 pixel gap between text and column

			for (i = 0; i < DAYS_IN_WEEK; i++) {
	
				x = (labelMetrics.stringWidth(days[i])) / 2;
				x = ((dayToX(i) + dayToX(i + 1)) / 2) - x;	// Center it above the column
	
				g.drawString(days[i], x, y);
			}
		} catch (SchedulerException e) {
			System.out.println("WARNING: Caught exception drawing day names: " +
									e.getType() + ": " + e.getMessage());
			e.printStackTrace();
			System.out.println("");
		}

		// Now the time labels

		String timeLabel;

		try {
			x = dayToX(0);

			int h = labelMetrics.getHeight() / 2;
	
			for (i = START_TIME; i <= END_TIME; i += 100) {
				timeLabel = timeToString(i);
	
				try {
					y = timeToY(i);
					y = y + h;
					g.drawString(timeLabel, x - labelMetrics.stringWidth(timeLabel) - 3, y);
				} catch (SchedulerException e) {
					System.out.println("WARNING: Caught exception drawing time labels: " +
											e.getType() + ": " + e.getMessage());
					e.printStackTrace();
					System.out.println("");
				}
			}
		} catch (SchedulerException e) {
			System.out.println("WARNING: Caught exception drawing time labels: " +
									e.getType() + ": " + e.getMessage());
			e.printStackTrace();
			System.out.println("");
		}

		// Now that we've drawn all that, it is time to draw anything else we've been told to.

		if (drawSched) {

			CourseSection tempSec;
			int sectionCount = scheduleToDraw.getCourseCount();
	
			if (sectionCount > 0) {
	//			System.out.println("There are " + sectionCount + " sections we are to draw.");

				g.setFont(courseFont);
	
				for (int j = 0; j < sectionCount; j++) {
					tempSec = scheduleToDraw.getACourse(j);
					drawSection(tempSec, j, g);
				}
			}
		}

//		System.out.println("");
//		System.out.println("");

		// That's it!
	}

	private void drawSection(CourseSection theSec, int colorNumber, Graphics g) {
		TimeBlock tb;

//		System.out.println("Asked to draw section " + theSec.getIDCode());

		for (int i = 0; i < DAYS_IN_WEEK; i++) {

			tb = null;

			try {
				tb = theSec.getDayBlock(i);
			} catch (SchedulerException e) {
				// Should never get here
				System.out.println("WARNING: Caught exception in drawSection: " +
												e.getType() + ": " + e.getMessage());
				e.printStackTrace();
				System.out.println("");
			}

			if (tb != null)
				drawTimeBlock(tb, i, colorNumber, theSec.getCourseName(), g);
		}
	}

	private void drawTimeBlock(TimeBlock tb, int dayNum, int colorNumber, String name, Graphics g) {

//		System.out.println("Asked to draw a block on day " + dayNum + " from " +
//							tb.getStart() + " to " + tb.getEnd());

		int sx, sy, ex, ey;

		try {
			sx = dayToX(dayNum);
			ex = dayToX(dayNum + 1);
	
			sy = timeToY(tb.getStart());
			ey = timeToY(tb.getEnd());
	
			int w = ex - sx;
			int h = ey - sy;

			g.setColor(ourColors.getFill(colorNumber));
	
			g.fillRect(sx + 1, sy + 1, w - 2, h - 2);	// The numberss are to make it fit in the box

			g.setColor(ourColors.getBorder(colorNumber));

			g.drawRect(sx + 1, sy + 1, w - 2, h - 2);	// The numbers are to make it fit in the box

			// Now draw the name of the course centered in the box we just drew

			int x = (sx + 1) + ((w - 2) / 2);			// Center of the box
			int y = (sy + 1) + ((h - 2) / 2);

			x = x - (courseMetrics.stringWidth(name) / 2);	// Make the string centered
			y = y + (courseMetrics.getHeight() / 2);

			g.drawString(name, x, y);					// Draw it!

		} catch (SchedulerException e) {
			System.out.println("WARNING: Caught exception in drawtimeBlock: " +
										e.getType() + ": " + e.getMessage());
			e.printStackTrace();
			System.out.println("");
		}
	}

	private String timeToString(int time) {
		// We only deal with hours, ignoring the minutes

		int hours = time / 100;

		if (hours < 12)
			return ("" + hours + ":00 AM");
		else
			if (hours > 12)
				return ("" + (hours - 12) + ":00 PM");
			else
				return ("12:00 PM");
	}

	private int dayToX(int day) throws SchedulerException {
		// Convert a day number into the left border of the column

		if ((day < 0) || (day > DAYS_IN_WEEK))
			throw new SchedulerException("Input", "Bad day of week: " + day);
		if (day == 0)					// These two save calculations
			return BORDER_SIZE * 3;
		if (day == DAYS_IN_WEEK)
			return width - BORDER_SIZE;

		double dayWidth = width - 4 * BORDER_SIZE;

		dayWidth = dayWidth / 7.0;

		return ((int) (dayWidth * day) + 3 * BORDER_SIZE);
	}

	private int timeToY(int time) throws SchedulerException {
		// Convert military time to a Y coordinate

		if ((time < START_TIME) || (time > END_TIME))
			throw new SchedulerException("Input", "Bad time: " + time);

		if (time == START_TIME)				// These two save us some calculations
			return BORDER_SIZE;
		if (time == END_TIME)
			return height - BORDER_SIZE;

		int hours = (time - START_TIME) / 100;	// Must remove the 7:00 head start from the time
		int minutes = time % 100;

		minutes = minutes + 60 * hours;

		double percent = ((double) minutes) / ((double) TIME_LENGTH);

		int size = height - (2 * BORDER_SIZE);

		double y = percent * ((double) size);

		int intY = (int) y;

		return intY + BORDER_SIZE;
	}
}