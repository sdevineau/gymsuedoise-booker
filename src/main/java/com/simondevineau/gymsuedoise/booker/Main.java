package com.simondevineau.gymsuedoise.booker;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simondevineau.gymsuedoise.booker.conf.ConfLoader;
import com.simondevineau.gymsuedoise.booker.pojo.CourseBooker;
import com.simondevineau.gymsuedoise.booker.util.CourseException;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private Main() {
	}

	public static void main(String[] args) {
		LOGGER.warn("Be aware this program only runs with Google Chrome version above 57.");
		ConfLoader conf;
		try {
			conf = ConfLoader.getInstance();
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			// Retrieve a scheduler from schedule factory
			Scheduler scheduler = schedulerFactory.getScheduler();
			// Set up detail about the job
			JobDetail jobDetail = newJob(CourseBooker.class).usingJobData(ConfLoader.getInstance().getJobDataMap())
					.withIdentity("CourseRegister", "CourseRegisterGroup").build();
			Calendar cal = Calendar.getInstance();
			cal.setTime(conf.getDate());
			Trigger trigger = newTrigger().withIdentity("CourseRegisterTrigger", "CourseRegisterTriggerGroup")
					.startAt(cal.getTime()).build();
			Date now = new Date(System.currentTimeMillis());
			Long diffBetweenDates = conf.getDate().getTime() - now.getTime();

			if (diffBetweenDates > 0) {
				LOGGER.info("Waiting " + TimeUnit.MINUTES.convert(diffBetweenDates, TimeUnit.MILLISECONDS)
						+ " minutes before executing program.");
			} else {
				LOGGER.info("Your execution date is in the past. Program is gonna run now");
			}
			scheduler.scheduleJob(jobDetail, trigger);
			LOGGER.info("Starts the scheduler");
			// start the scheduler
			scheduler.start();
			LOGGER.info("Ends the scheduler");

		} catch (CourseException e) {
			LOGGER.error("Could not run program. See message below for more details.");
			LOGGER.debug(e.getMessage(), e);
			System.exit(0);
		} catch (SchedulerException e) {
			LOGGER.error("Could not run program. See message below for more details.");
			LOGGER.debug(e.getMessage(), e);
			System.exit(0);
		} 

	}

}
