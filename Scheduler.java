/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import java.lang.String;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.Boolean;
import java.lang.Exception;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class Scheduler {

	private Hashtable<String, Boolean> conflictList;
	private Hashtable<String, Course> courseList;

	public Scheduler() {
		conflictList = new Hashtable<String, Boolean>();
		courseList = new Hashtable<String, Course>();
//		System.out.println("End of Scheduler()");
	}

	public Course getCourse(String courseName) {
		return courseList.get(courseName);
	}

	public void addCourse(Course theCourse) {
		courseList.put(theCourse.getCourseName(), theCourse);
//		System.out.println("Added a course named " + theCourse.getCourseName());
//		System.out.println("End of addCourse()");
	}

	public int getCourseCount() {
		return courseList.size();
	}

	public void fillFromFile(String fileName) throws SchedulerException {

		// First we've got to open the file

		BufferedReader theFile;

		try {
			theFile = new BufferedReader(new FileReader(fileName));
		} catch (Exception e) {
			throw new SchedulerException("File", "Unable to open \"" + fileName +
													"\" to read in from: " + e);
		}

		// Now we read out the lines

//		System.out.println("Starting to read lines our of \"" + fileName + "\"...");
//		System.out.println("");

		String theLine;
		boolean keepGoing = true;

		int i = 0;

		while (keepGoing) {
			// Get the next line

			i++;

//			System.out.println("Line: " + i);

			try {
				theLine = theFile.readLine();
			} catch (Exception e) {
				// Oops, something went wrong.

				try {						// Last ditch effort at closing the file
					theFile.close();
				} catch (Exception ee) {
					// It failed? PANIC!!!!!
				}

				throw new SchedulerException("File", "Encountered an error at line " + i + ": " + e);
			}

			if (theLine == null) {
				// We're out of info, so we're done

//				System.out.println("Out of file at line " + i + ", done.");
				keepGoing = false;
			} else if (theLine.trim().length() == 0) {
				// The line was empty, so we'll skip it

//				System.out.println("Line " + i + " was blank, skipping.");
			} else if (theLine.charAt(0) == '#') {
				// We ignore lines starting with a hash sign

//				System.out.println("Line " + i + " was a comment, skipping.");
			} else if ((theLine.charAt(0) == 'C') && (theLine.charAt(1) == ':')) {
				// Got a new course to add

				try {
					Course newCourse = courseFromString(theLine.substring(2, theLine.length()));
					addCourse(newCourse);
				} catch (SchedulerException e) {
					try {						// Last ditch effort at closing the file
						theFile.close();
					} catch (Exception ee) {
						// It failed? PANIC!!!!!
					}

					throw e;
				}

			} else if ((theLine.charAt(0) == 'S') && (theLine.charAt(1) == ':')) {
				// Got a new section to add

				try {
					CourseSection newSection = sectionFromString(theLine.substring(2, theLine.length()));
					addSection(newSection);
				} catch (SchedulerException e) {
					try {						// Last ditch effort at closing the file
						theFile.close();
					} catch (Exception ee) {
						// It failed? PANIC!!!!!
					}

					throw e;
				}
			} else {
				// What we got wasn't understood! Complain!

				try {						// Last ditch effort at closing the file
					theFile.close();
				} catch (Exception e) {
					// It failed? PANIC!!!!!
				}

				throw new SchedulerException("Parsing", "Unable to parse line " + i + ".");
			}
		}

		try {
			theFile.close();	// Close the file
		} catch (Exception e) {
			throw new SchedulerException("IO", "Unable to close the file: " + e);
		}

//		System.out.println("");
//		System.out.println("Done reading lines from the file.");

//		Course temp = courseList.get("PSYC315");

//		System.out.println("");
//		temp.printKeys("fillFromFile");
//		System.out.println("");

//		System.out.println("End of fillFromFile()");

	}

	public void addSection(CourseSection theSection) throws SchedulerException {
		// First, find out if we already have course

		if (courseList.containsKey(theSection.getCourseName())) {
			// OK, the course is there. Add this to it

			Course tempCourse = courseList.get(theSection.getCourseName());

			tempCourse.addSection(theSection);

//			System.out.println("Added section id " + theSection.getIDCode() + " to the " +
//									theSection.getCourseName() + " course.");

		} else {
			throw new SchedulerException("Course", "Unable to add a section for the nonexistant " +
														"course " + theSection.getCourseName() + ".");
		}
//		System.out.println("End of addSection()");
	}

	public Enumeration<String> getCourses() {
//		System.out.println("End of getCourses()");
		return courseList.keys();
	}

	public Course courseFromString(String theString) throws SchedulerException {
		// This takes a string (line) and makes a course for us from it.

		// Here is an example line:
		//
		//	CIS123:CIS100
		//
		// Format:
		//
		//  COURSE:[CO-REQ]


//		System.out.println("Asked to interpret string as a new course:");
//		System.out.println("\t" + theString);

		String[] parts = theString.split(":");		// Split the line into substrings

		int numParts = parts.length;

		if ((numParts != 1) && (numParts != 2))		// Must be 1 or 2 parts
			throw new SchedulerException("Parse", "Wrong number of parts, " + numParts + ".");

		String theCourse = parts[0];

		Course newCourse = new Course(theCourse);

		if (newCourse == null)
			throw new SchedulerException("Memory", "Unable to allocate a new Course.");

		if (numParts == 2) {
			newCourse.setCoReq(parts[1]);
//			System.out.println("\tCourse name: " + theCourse + ", coreq: " + parts[1] + ".");
		} else {
//			System.out.println("\tCourse name: " + theCourse + ".");
		}

		// Simple, that's it.

//		System.out.println("End of courseFromString()");

		return newCourse;
	}

	private int getDayNum(String theCode) throws SchedulerException {

		int len = theCode.length();
		char cOne, cTwo;

		if ((len > 2) || (len <= 0)) {
			return -1;	// Day codes are 1 or 2 characters
		} else {
			cOne = theCode.charAt(0);

			if (len == 2) {
				cTwo = theCode.charAt(1);

				if (cOne == 'S') {
					if (cTwo == 'U')
						return 0;
					else if (cTwo == 'A')
						return 6;
					else
						return -1;
				} else if (cOne == 'T') {
					if (cTwo == 'U')
						return 2;
					else if (cTwo == 'H')
						return 4;
					else
						return -1;
				} else {
					throw new SchedulerException("Parse", "Given bad day code: " + theCode + ".");
				}
			} else {
				if (cOne == 'M')
					return 1;
				else if (cOne == 'W')
					return 3;
				else if (cOne == 'F')
					return 5;
				else
					throw new SchedulerException("Parse", "Given bad day code: " + theCode + ".");
			}
		}
	}

	public CourseSection sectionFromString(String theString) throws SchedulerException {
		// This takes a string (line) and makes a section for us from it.

		// Here is an example line:
		//
		//	CIS123:P:CIS123 P:M:900:1200:TH:900:1100
		//
		// Format:
		//
		//  COURSE:SECTION:[ID]:hours
		//
		// hours is grouops of DAY:START:END

//		System.out.println("Asked to interpret string as a course section:");
//		System.out.println("\t" + theString);

		ArrayList<String> dayCodes = new ArrayList<String>();

		dayCodes.add(0, "SU");
		dayCodes.add(1, "M");
		dayCodes.add(2, "TU");
		dayCodes.add(3, "W");
		dayCodes.add(4, "TH");
		dayCodes.add(5, "F");
		dayCodes.add(6, "SA");

		String[] parts = theString.split(":");		// Split the line into substrings

		int numParts = parts.length;				// Number of substrings

		if ((numParts < 5) || (numParts > 24)) {		// Must be at least 5 parts,
														//	and the max is 24 (3 + 7 * 3)
														//					(info + days * perDay)
			throw new SchedulerException("Parse", "Bad number of parts: " + numParts + ".");
		}

		String theCourse = parts[0];
		String theSection = parts[1];
		String theID = null;

		int i;

		if (!dayCodes.contains(parts[2])) {
			// The third field is the code

			theID = parts[2];

			i = 3;		// Where to look next

//			System.out.println("\tCourse name: " + theCourse + ", section: " + theSection +
//									", id: " + theID + ".");
		} else {
//			System.out.println("\tCourse name: " + theCourse + ", section: " + theSection + ".");

			i = 2;
		}

		// So we'll create our section with what we know right now

		CourseSection newSection = new CourseSection(theCourse, theSection);

		if (newSection == null)
			throw new SchedulerException("Section", "Unable to allocate a new CourseSection.");

		if (theID != null)
			newSection.setIDCode(theID);

		// Now we extract the day info.

		TimeBlock temp;

		boolean foundBlocks = false;

		String a, b, c;

		int dayNum, j;

		while (i < numParts) {

			// First, get the triplet of information

			try {
				a = parts[i];
				b = parts[i + 1];
				c = parts[i + 2];
				i = i + 3;
			} catch (Exception e) {
				throw new SchedulerException("Parse", "Ran out of parts at position " + i +
														": " + e);
			}

//			System.out.println("\tDay string: " + a + ", start string: " + b +
//									", end string: " + c + ".");

			// Interpret the day

			if (!dayCodes.contains(a)) {
				throw new SchedulerException("Parse", "Invalid day code: " + a);
			} else {
				dayNum = getDayNum(a);	// We don't catch the exception, but it should never fail

//				System.out.println("\t" + a + " was day number " + dayNum + ".");
			}

			// Now figure out the start and end times

			Integer startInt = null;
			Integer endInt = null;

			int start, end;

			try {
				startInt = new Integer(b);
				start = startInt;
				endInt = new Integer(c);
				end = endInt;
			} catch (Exception e) {
				throw new SchedulerException("Parse", "Unable to interpret " + a + " and " + b +
														": " + e);
			}

			// Got the numbers, make sure they are valid military time

			if (!isMilitaryTime(start)) {
				throw new SchedulerException("Parse", "Start of time " + start + " is not valid " +
														"military time.");
			}

			if (!isMilitaryTime(end)) {
				throw new SchedulerException("Parse", "Start of time " + end + " is not valid " +
														"military time.");
			}

			if (start >= end) {
				throw new SchedulerException("Parse", "Class ends (" + end + ") before it starts (" +
														start + ").");
			}

			// If we got here, we can add the time block

			temp = new TimeBlock(start, end);

			if (temp == null)
				throw new SchedulerException("Memory", "Unable to allocate a time block.");

			newSection.setDayBlock(temp, dayNum);

//			System.out.println("\tAdded the time block for day " + dayNum + " to run between " +
//									start + " and " + end + ".");

			foundBlocks = true;	// Mark that we found at least one block of time
		}

		if (!foundBlocks) {
			throw new SchedulerException("Parse", "No time blocks for section " +
													newSection.getIDCode() + ".");
		} else {

//			System.out.println("\tInterpreted the string, new section has been created.");

//			System.out.println("End of sectionFromString()");

			return newSection;
		}
	}

	private boolean isMilitaryTime(int theNum) {
		int hours = theNum / 100;
		int minutes = theNum % 100;

		if ((hours < 0) || (hours > 23))
			return false;
		if ((minutes < 0) || (minutes > 59))
			return false;

		return true;
	}

	public boolean coursesEverConflict(Course courseOne, Course courseTwo) throws SchedulerException {
		// Find out if there is any combination of the two courses that doesn't conflict

		if (courseOne == null)
			throw new SchedulerException("Input", "cEC: courseOne was null.");
		if (courseTwo == null)
			throw new SchedulerException("Input", "cEC: courseTwo was null.");

		Enumeration<String> cOne, cTwo;

		cOne = courseOne.getSections();
		cTwo = courseTwo.getSections();

		if ((cOne.hasMoreElements() == false) || (cTwo.hasMoreElements() == false))
			return false;		// One of the courses has no sections. No conflicts.

		String temp;

		CourseSection tempOne, tempTwo;

		while (cOne.hasMoreElements()) {			// For each section of course one
			temp = cOne.nextElement();
			tempOne = courseOne.getSection(temp);

			while (cTwo.hasMoreElements()) {		// Look at each sectiono of course two
				temp = cTwo.nextElement();
				tempTwo = courseTwo.getSection(temp);

				if (sectionsConflict(tempOne, tempTwo))	// And see if they conflict
					return true;
			}
		}

		return false;	// If we're here, we found no conflicts
	}

	public boolean coursesAlwaysConflict(Course courseOne, Course courseTwo) throws SchedulerException {
		// Find out if there is any combination of the two courses that doesn't conflict

		if (courseOne == null)
			throw new SchedulerException("Input", "cAC: courseOne was null.");
		if (courseTwo == null)
			throw new SchedulerException("Input", "cAC: courseTwo was null.");

		Enumeration<String> cOne, cTwo;

		cOne = courseOne.getSections();
		cTwo = courseTwo.getSections();

		if ((cOne.hasMoreElements() == false) || (cTwo.hasMoreElements() == false))
			return false;		// One of the courses has no sections. No conflicts.

		String temp;

		CourseSection tempOne, tempTwo;

		while (cOne.hasMoreElements()) {			// For each section of course one
			temp = cOne.nextElement();
			tempOne = courseOne.getSection(temp);

			while (cTwo.hasMoreElements()) {		// Look at each sectiono of course two
				temp = cTwo.nextElement();
				tempTwo = courseTwo.getSection(temp);

				if (!sectionsConflict(tempOne, tempTwo))	// And see if they conflict
					return false;
			}
		}

		return true;	// If were here, things always conflicted
	}

	public ArrayList<Schedule> possiblesToSchedules(String[] courses,
														ArrayList<String[]> possibilities) 
															throws SchedulerException {
		// Take a list of possible schedules, and turn it into a list of real working schedules.

		if (possibilities == null)
			throw new SchedulerException("Input", "Bad list of possibilities.");

		ArrayList<Schedule> retVal = new ArrayList<Schedule>();

		if (retVal == null)
			throw new SchedulerException("Memory", "Unable to allocate an ArrayList of Schedules.");


		// Now we got through the possibilities, one at a time, and try to make schedules.

		Schedule temp;
		String[] line;
		Course tc;
		CourseSection ts;

		int numCourses = courses.length;

		for (int i = 0; i < possibilities.size(); i++) {
			temp = new Schedule();		// A schedule to work with

			if (temp == null)
				throw new SchedulerException("Memory", "Unable to allocate a new Schedule.");

			line = possibilities.get(i);
			
			for (int j = 0; j < numCourses; j++) {
				tc = courseList.get(courses[j]);
				ts = tc.getSection(line[j]);
				try {
					temp.addSection(ts, this);
				} catch (SchedulerException e) {
					if (e.getType() == "Conflict") {	// Things conflicted
						temp = null;
						break;
					} else {
						throw e;
					}
				}
			}

			if (temp != null)
				retVal.add(temp);
		}

		return retVal;
	}

	public ArrayList<String[]> buildPossibles(String[] theCourses) throws SchedulerException {
		// We are given a list of strings with the courses the person would like to take.
		// First we have to see if a valid schedule is even possible

		Course courseOne, courseTwo;

		if (theCourses == null)
			throw new SchedulerException("Input", "Given an empty list of courses.");

		int numCourses = theCourses.length;

		if (numCourses > 1) {	// Things can't conflict unless there is more than one course
			for (int i = 0; i < numCourses; i++) {
				courseOne = courseList.get(theCourses[i]);
				if (courseOne == null)
					System.out.println("Course " + i + " was null.");
				for (int j = i + 1; j < numCourses; j++) {
					courseTwo = courseList.get(theCourses[j]);
					if (courseTwo == null)
						System.out.println("Course " + j + " was null.");
					if (coursesAlwaysConflict(courseOne, courseTwo)) {
						throw new SchedulerException("Conflict", courseOne.getCourseName() +
														" and " + courseTwo.getCourseName() + 
														" always conflict.");
					}
				}
			}
		}

//		System.out.println("No fatal conflicts.");

//		System.out.println("");

		// Next we get a list of each of the courses' sections

		String[][] sectionList = new String[theCourses.length][];
		Enumeration<String> tempEnum;

		if (sectionList == null)
			throw new SchedulerException("Memory", "Unable to allocate a String double array.");

		int[] parts = new int[numCourses];	// Number of sections in each course

		if (parts == null)
			throw new SchedulerException("Memory", "Unable to allocate an array of ints.");

		int combinations = 1;

		for (int i = 0; i < numCourses; i++) {			// For each course
			courseOne = courseList.get(theCourses[i]);
			tempEnum = courseOne.getSections();

			parts[i] = courseOne.getSectionCount();

//			System.out.println("Course " + i + " has " + parts[i] + " sections.");

			sectionList[i] = new String[parts[i]];	// Allocate a list for sections

			if (sectionList[i] == null)
				throw new SchedulerException("Memory", "Unable to allocate a String list.");

			int j = 0;

			combinations = combinations * parts[i];		// Figure out the number of permutations

//			System.out.println("Course " + i + " has " + parts[i] + " sections.");

			while (tempEnum.hasMoreElements()) {
				sectionList[i][j] = tempEnum.nextElement();		// Fill it
				j = j + 1;
			}
		}

//		System.out.println("");
//		System.out.println("Total of " + combinations + " combinations.");
//		System.out.println("");

		// At this point, sectionList[][] has the stuff we need in it.
		// First we'll allocate all the stuff

		ArrayList<String[]> retVal = new ArrayList<String[]>(combinations);

		if (retVal == null)
			throw new SchedulerException("Memory", "Unable to allocate an ArrayList of String lists.");

		String[] tempStringList;

		for (int i = 0; i < combinations; i++) {
			tempStringList = new String[numCourses];	// Create a list for us

			if (tempStringList == null)
				throw new SchedulerException("Memory", "Unable to allocate a String list.");

			retVal.add(i,tempStringList);
		}

		// Now we generate the combinations
		// This is a little complex, so bare with me.
		// Pretend there are X classes called a, b, c, ...
		// And class a has A sections, b has B sections, etc.
		// Now here is where things go:
		//
		// Each class gets 'count' entries in a row.
		// 		'count' is: 1 for the last class,
		//					Z for the second to last,	(if there are 26 classes)
		//					Z * Y for the third to last, .....
		//					Z * Y * X for the fourth to last, .....
		//					all the way up to Z * X * .. .* B for the first class (a)
		//
		// Then we skip 'skip' entries (so the new start is old start + skip)
		//		'skip' is:  Z for the last class (if there are 26)
		//					Z * Y for the second to last,
		//					Z * Y * X for the third to last, ...
		//					all the way up to B * C * ... X * Y * Z for the 2nd to first
		//					and first is the same as second (B * C * ... * X * Y *Z)
		//
		// So for the first class, count and skip are B * C * ... up to the last class
		//			Note that both of these are combinations / A.
		// For the last class, count is 1 and skip is the numer of sections in that class
		// For all else, count is (letter + 1) * (letter + 2) * ... * (last letter) * 1,
		//				 skip is (letter) * (letter + 1) * (letter + 2) * ... (last letter)

		// We'll do the first row column first

		int count, skip, i, start;	// i is the line, j is the section letter

		count = combinations / parts[0];
		skip = count;

		start = 0;

		String[] tempStr;

		if (numCourses > 1) {		// If there is only one course, we let the next part do it
			for (start = 0; start < combinations; start += skip) {	// Where to start writing the letters
				for (i = start; i < start + count; i++) {			// How many to write
					tempStr = retVal.get(i);
					tempStr[0] = sectionList[0][start / skip];		// Write it!
//					System.out.println("Set possibility " + i + " course " + 0 + " to section " +
//											(start / skip) + ", " + sectionList[0][start / skip]);
				}						//      start / skip tells us which section letter we are on
			}
		}

//		System.out.println("");

		// Now we do the last column

		skip = parts[numCourses - 1];
		count = 1;

		for (i = 0; i < skip; i++) {						// Which letter to write
			for (start = i; start < combinations; start += skip) {		// Where to start
				tempStr = retVal.get(start);
				tempStr[numCourses - 1] = sectionList[numCourses - 1][i];	// Write it!

//				System.out.println("Set possibility " + start + " course " + (numCourses - 1) +
//										" to section " + i + ", "
//										+ sectionList[numCourses - 1][i]);

			}
		}

		if (numCourses > 2) {			// Skip this if there are only two columns
			// Now the general case
	
			count = 1;
	
			for (int courseNum = numCourses - 2; courseNum > 0; courseNum--) {	// Which column
				count = count * parts[courseNum + 1];
				skip = count * parts[courseNum];

				for (int sectionNum = 0; sectionNum < parts[courseNum]; sectionNum++) {	// Which section
					for (start = sectionNum * count; start < combinations; start += skip) {	// Where to start
						for (i = 0; i < count; i++) {										// Number to set
							tempStr = retVal.get(i + start);
							tempStr[courseNum] = sectionList[courseNum][sectionNum];		// Write it!
						}
					}
				}
			}
		}

		// At this point, retVal should have the list. Let's just hope it works.

		return retVal;
	}

	public boolean sectionsConflict(CourseSection secOne, CourseSection secTwo) throws SchedulerException {
		// First, we'll extract the ids of the two sections

		String idOne = secOne.getIDCode();
		String idTwo = secTwo.getIDCode();

		if (idOne == idTwo)
			throw new SchedulerException("Input", "The two courses we were given were the same.");

		// Now we'll see if that is in our hashtable

		if (idOne.compareTo(idTwo) > 0) {	// Put the strings in a consistant order
			String temp = idOne;
			idOne = idTwo;
			idTwo = temp;
		}

		String keyString = idOne + idTwo;

		if (conflictList.containsKey(keyString))	// Have we checked these two before?
			return conflictList.get(keyString);		// Yep, return the answer

		// OK, we'll just have to figure it out

		boolean theyConflict = false;

		TimeBlock one, two;

		for (int i = 0; i < CourseSection.DAYS_IN_WEEK; i++) {
			one = secOne.getDayBlock(i);
			two = secTwo.getDayBlock(i);

			if ((one != null) && (two != null) && (one.conflictsWith(two))) {
				theyConflict = true;
				break;
			}
		}

		// Now we now if they conflict or not, so we'll save the result and return it

		conflictList.put(keyString, theyConflict);

		return theyConflict;
	}
}