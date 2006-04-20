JAVAC = /System/Library/Frameworks/JavaVM.framework/Versions/1.5/Home/bin/javac
JAVA = /System/Library/Frameworks/JavaVM.framework/Versions/1.5/Home/bin/java
JAR = /System/Library/Frameworks/JavaVM.framework/Versions/1.5/Home/bin/jar
JDB = /System/Library/Frameworks/JavaVM.framework/Versions/1.5/Home/bin/jdb

CLASSES = TimeBlock.class Scheduler.class CourseSection.class Course.class \
				Schedule.class SchedulePanel.class SchedulerApp.class \
				CourseList.class ScheduleList.class SchedulerException.class \
				ScheduleColor.class

DEBUG_OPTS = -Xdebug -Xrunjdwp:transport=dt_socket,address=5000,server=y,suspend=n

JAR_NAME = Scheduler.jar

OPTIONS = -g

.PHONY: clean test debug

Scheduler.jar: Scheduler
	$(JAR) cmf manifest.txt $(JAR_NAME) *.class

Scheduler: $(CLASSES)

all: Scheduler Scheduler.jar

clean:
	rm *.class Scheduler.jar

test: Scheduler.jar
	$(JAVA) -jar Scheduler.jar

debug: Scheduler.jar
	$(JDB) -Xdebug SchedulerApp

%.class: %.java
	$(JAVAC) $(OPTIONS) $<
