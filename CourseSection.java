/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import java.lang.String;

public class CourseSection {

	public static final int DAYS_IN_WEEK = 7;

	private TimeBlock[] blocks;	// When the class is held

	private String courseName;
	private String sectionName;
	private String idCode;
	private String coReq;

	private int contactMinutes = -1;

	public CourseSection(String theCourse, String theSection) {

		// Set some basic stuff

		courseName = theCourse;
		sectionName = theSection;

		coReq = null;

		// Make our own ID
		idCode = courseName + " " + sectionName;

		blocks = new TimeBlock[DAYS_IN_WEEK];

		for (int i = 0; i < DAYS_IN_WEEK; i++)
			blocks[i] = null;
	}

	public String getCoReq() {
		return coReq;
	}

	public void setCoReq(String newCoRec) {
		coReq = newCoRec;
	}

	public int getContactMinutes() {
		if (contactMinutes == -1)
			calculateContactHours();
		return contactMinutes;
	}

	public int getContactHours() {
		if (contactMinutes == -1)
			calculateContactHours();
		return contactMinutes / 60;	// Convert minutes to hours
	}

	public String getCourseName() {
		return courseName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public String getIDCode() {
		return idCode;
	}

	public String toString() {
		return getIDCode();
	}

	public void setIDCode(String theID) {
		idCode = theID;
	}

	public TimeBlock getDayBlock(int dayOfWeek) throws SchedulerException {
		if ((dayOfWeek < 0) || (dayOfWeek > DAYS_IN_WEEK - 1))
			throw new SchedulerException("Input", "Bad day of week: " + dayOfWeek);

		return blocks[dayOfWeek];
	}

	public void setDayBlock(TimeBlock theBlock, int dayOfWeek) throws SchedulerException {
		if ((dayOfWeek < 0) || (dayOfWeek > DAYS_IN_WEEK - 1))
			throw new SchedulerException("Input", "Bad day of week: " + dayOfWeek);

		// Note that we don't check if theBlock is null because that would be used
		//		to set a day to not having any hours.

		blocks[dayOfWeek] = theBlock;
	}

	public void setAllBlocks(TimeBlock sa, TimeBlock m, TimeBlock tu, TimeBlock w,
								TimeBlock th, TimeBlock f, TimeBlock su){
		blocks[0] = su;
		blocks[1] = m;
		blocks[2] = tu;
		blocks[3] = w;
		blocks[4] = th;
		blocks[5] = f;
		blocks[6] = sa;
	}

	private void calculateContactHours() {
		int num = 0;

		for (int i = 0; i < DAYS_IN_WEEK; i++) {
			if (blocks[i] != null)					// Skip blocks that are empty
				num += blocks[i].getLength();
		}

		contactMinutes = num;
	}
}