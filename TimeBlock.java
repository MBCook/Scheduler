/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

public class TimeBlock {

	private int startHour;	// Times are stored military style, 0730 for example
	private int endHour;

	public TimeBlock(int start, int end) {
		if (start < end) {
			startHour = start;
			endHour = end;
		} else {
			startHour = end;	// Can't have negative blocks of time
			endHour = start;
		}
	}

	public int getStart() {
		return startHour;
	}

	public int getEnd() {
		return endHour;
	}

	public int getLength() {
		// Returns the length in minutes, so we must calculate it from military time

		int realStartHour = startHour / 100;
		int realEndHour = endHour / 100;
		int realStartMinute = startHour % 100;
		int realEndMinute = endHour % 100;

		return ((realStartHour * 60) + realStartMinute) - ((realEndHour * 60) + realEndMinute);
	}

	public boolean conflictsWith(TimeBlock other) {
		int os = other.getStart();
		int oe = other.getEnd();

		if ((os <= endHour) && (os >= startHour)) {
			// They start before we end, and end after us, there is a conflict
			return true;
		} else if ((oe >= startHour) && (oe <= endHour)) {
			// They end after we start
			return true;
		} else {
			// No conflict
			return false;
		}
	}

	public TimeBlock getConfict(TimeBlock other) throws SchedulerException {
		int os = other.getStart();
		int oe = other.getEnd();

		int cs = 0;		// Conflict start and end
		int ce = 0;

		if ((os <= endHour) && (os >= startHour)) {
			cs = os;	// Conflict starts where the other block does
			if (endHour > oe)
				ce = oe;	// We end after the other, conflict ends when they do
			else
				ce = endHour;	// Conflict ends when we do
		} else if ((oe >= startHour) && (oe <= endHour)) {
			ce = oe;	// Conflict ends where the other block does
			if (startHour < os)
				cs = os;	// We start before the other, conflict starts when they do
			else
				cs = startHour;	// Conflict starts when we do
		} else {
			return null;	// No conflict
		}

		TimeBlock newBlock = new TimeBlock(cs, ce);	// Make a time block from the conflict and return it

		if (newBlock == null)
			throw new SchedulerException("Memory", "Unable to allocate a new TimeBlock.");

		return newBlock;
	}
}