/***************************************************\
|												 	|
|				Copyright ©2005, 2006			 	|
|					Michael Cook				 	|
|												 	|
\***************************************************/

import java.lang.Exception;
import java.lang.String;

public class SchedulerException extends Exception {
	String exceptionType;

	SchedulerException(String theType, String message) {
		super(message);
		exceptionType = theType;
	}

	public String getType() {
		return exceptionType;
	}
}