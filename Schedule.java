/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import java.lang.String;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Iterator;

public class Schedule {

	private ArrayList<CourseSection> sections;

	public Schedule() {

		// Set some basic stuff

		sections = new ArrayList<CourseSection>(4);
	}

	public CourseSection getACourse(int i) {
		return sections.get(i);
	}

	public int getCourseCount() {
		return sections.size();
	}

	public ListIterator<CourseSection> getListIterator() {
		return sections.listIterator();
	}

	public Iterator<CourseSection> getIterator() {
		return sections.iterator();
	}

	public ArrayList<String> getMissingCoReqs() {
		if (sections.size() == 0)
			return null;	// Can't be missing co-reqs if we don't have any courses

		// First we build a list of courses we need

		ArrayList<String> theList = new ArrayList<String>();

		ListIterator<CourseSection> cs = sections.listIterator();
		CourseSection tempSection;
		String temp;

		while (cs.hasNext()) {
			tempSection = cs.next();
			temp = tempSection.getCoReq();
			if (temp != null) {
				theList.add(temp);
			}
		}

//		System.out.println("Found " + sections.size() + " sections in this schedule.");

		// Now we remove the ones we find

		cs = sections.listIterator();	// Get a new iterator

		while (cs.hasNext()) {
			tempSection = cs.next();
			temp = tempSection.getCourseName();
			theList.remove(temp);					// Fullfilled this co-req
		}

//		System.out.println("This section has " + theList.size() + " outstanding requirements.");

		if (theList.size() == 0) {
			return null;							// All OK.
		} else {
			return theList;
		}
	}

	public void addSection(CourseSection theSec, Scheduler master) throws SchedulerException {
		String theName = theSec.getCourseName();

		String oldID = containsCourse(theName);

		if (oldID != null) {
			removeSection(oldID);
		}

		// Now we make sure it doesn't conflict

		if (sections.size() > 0) {
			ListIterator<CourseSection> cs = sections.listIterator();
			while (cs.hasNext()) {
				CourseSection otherSec = cs.next();
				if (master.sectionsConflict(theSec, otherSec)) {
//					System.out.println("Not adding " + theSec.getIDCode() + ": conflicts with " +
//											otherSec.getIDCode());

					throw new SchedulerException("Conflict", "Unable to add section " +
													theSec.getIDCode() + ", it conflicts with " +
													otherSec.getIDCode());
				}
			}
		}

		// If we're here, we add it

		sections.add(theSec);
	}

	public String containsCourse(String theName) {
		if (sections.size() > 0) {
			ListIterator<CourseSection> cs = sections.listIterator();
			while (cs.hasNext()) {
				CourseSection theSec = cs.next();
				if (theSec.getCourseName() == theName) {
					return theSec.getIDCode();
				}
			}
		}
		return null;
	}

	public String getCourseThatNeeds(String theName) {
		if (sections.size() > 0) {
			ListIterator<CourseSection> cs = sections.listIterator();
			while (cs.hasNext()) {
				CourseSection theSec = cs.next();
				if (theSec.getCoReq() == theName) {
					return theSec.getCourseName();
				}
			}
			return null;
		} else {
			return null;
		}
	}

	public void removeSection(CourseSection sectionToDrop) throws SchedulerException {
		removeSection(sectionToDrop.getIDCode());
	}

	public void removeAll() {
		sections = new ArrayList<CourseSection>(4);	
	}

	public void removeSection(String sectionID) throws SchedulerException {

		boolean removedSomething = false;
		String theName = null;

		if (sections.size() > 0) {
			ListIterator<CourseSection> cs = sections.listIterator();
			while (cs.hasNext()) {
				CourseSection theSec = cs.next();
				if (theSec.getIDCode() == sectionID) {
					removedSomething = true;
					theName = theSec.getCourseName();
					cs.remove();
					break;
				}
			}
		}

		// Did we get rid of something?

		if (removedSomething) {
			String coreqID = null;

			coreqID = getCourseThatNeeds(sectionID);

			if (coreqID != null)
				removeSection(coreqID);
		} else {
			throw new SchedulerException("CourseSection", "Section " + sectionID +
															" wasn't removed because it wasn't found.");
		}
	}
}