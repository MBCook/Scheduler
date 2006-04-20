/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import java.lang.String;
import java.util.Hashtable;
import java.util.Enumeration;

public class Course {

	// The various sections, with the key being section id

	private Hashtable<String, CourseSection> sections;

	private String courseName;
	private String coReq;

	public Course(String theCourse) {

		// Set some basic stuff

		courseName = theCourse;

		sections = new Hashtable<String, CourseSection>(4);	// We expect 4 sections,
															//		which is less than
															//		Java's default of 11.
//		System.out.println("End of Course()");
	}

	public String getCoReq() {
//		System.out.println("End of getCoReq()");
		return coReq;
	}

	public void setCoReq(String newCoRec) {
		coReq = newCoRec;

		Enumeration<CourseSection> theSections = sections.elements();

		while (theSections.hasMoreElements())
			theSections.nextElement().setCoReq(coReq);	// Make sure all the sections know
															//		the new co-req
//		System.out.println("End of setCoReq()");
	}

	public String getCourseName() {
//		System.out.println("End of getCourseName()");
		return courseName;
	}

	public String toString() {
		return getCourseName();
	}

	public Enumeration<String> getSections() {
//		System.out.println("End of getSections()");
		return sections.keys();
	}

	public void printKeys(String where) {
		System.out.println(where + ": " + sections.toString());
//		System.out.println("End of printKeys()");
	}

	public CourseSection getSection(String sectionName) throws SchedulerException {
//		System.out.println("End of getSection()");

		CourseSection temp = sections.get(sectionName);

		if (temp == null)
			throw new SchedulerException("Course", "Bad section name: " + sectionName);

		return temp;
	}

	public void addSection(CourseSection theSection) {
		// First, set our variables so that they are consistant

		theSection.setCoReq(coReq);

		// Now add it

		sections.put(theSection.getSectionName(), theSection);
//		System.out.println("End of addSection()");
	}

	public int getSectionCount() {
//		System.out.println("End of getSectionCount()");
		return sections.size();
	}
}