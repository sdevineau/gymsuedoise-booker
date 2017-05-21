package com.simondevineau.gymsuedoise.booker.pojo;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simondevineau.gymsuedoise.booker.conf.ConfLoader;
import com.simondevineau.gymsuedoise.booker.util.CourseException;

public class CourseBooker implements Job {
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseBooker.class);
	private WebDriver driver;
	private Course course;
	private User user;

	public CourseBooker() {
		driver = new ChromeDriver();
	}

	public CourseBooker(WebDriver driver, Course course, User user) {
		this.driver = driver;
		this.course = course;
		this.user = user;
	}

	public String getHomePageElement() {
		if (!driver.getCurrentUrl().equals(course.getExpandedBookingUrl())) {
			driver.get(course.getExpandedBookingUrl());
		}
		return driver.getCurrentUrl();

	}

	public void login() throws CourseException {
		driver.get(course.getExpandedBookingUrl());
		getLoginButtonElement().click();
		getEmailLoginElement().sendKeys(user.getEmail());
		getPasswordLoginElement().sendKeys(user.getPassword());
		getSumitLoginElement().click();

	}

	public boolean isLoggedIn() throws CourseException {
		try {
			WebElement el = driver.findElement(By.cssSelector("header.sf-Header > a.js-AccountLink"));
			return el != null && el.isDisplayed() && el.getText().toLowerCase().contains(user.getName().toLowerCase());
		} catch (NoSuchElementException e) {
			throw new CourseException("Could not find account link to check the user is logged in.", e);
		}
	}

	public boolean isExistingCourse() throws CourseException {
		driver.get(course.getDetailsUrl());
		try {
			WebElement el = driver.findElement(By.cssSelector("div.schedule-header > div:nth-child(1) > h2"));
			return el != null && el.isDisplayed() && el.getText().toLowerCase().contains("Détails");
		} catch (NoSuchElementException e) {
			throw new CourseException("Seems the course does not exist. Are you sure you give the good course id ?", e);
		}
	}

	public void bookCourse() throws CourseException {
		driver.get(course.getExpandedBookingUrl());
	}

	public boolean isBooked() throws CourseException {
		try {
			WebElement el = driver.findElement(By.cssSelector("header.sf-Header > a.js-AccountLink"));
			return el != null && el.getText() != null
					&& el.getText().toLowerCase().contains(user.getName().toLowerCase().trim());

		} catch (NoSuchElementException e) {
			throw new CourseException("Could not find account link to check the course is booked.", e);
		}
	}

	public WebElement getLoginButtonElement() throws CourseException {
		try {
			return driver.findElement(By.cssSelector("header.sf-Header > a.js-BtnLogin"));
		} catch (NoSuchElementException e) {
			throw new CourseException("Could not find login button to log in the user.", e);
		}
	}

	public WebElement getEmailLoginElement() throws CourseException {
		try {
			return driver.findElement(By.cssSelector("div.js-loginBox > form > input[name=\"em\"]"));
		} catch (NoSuchElementException e) {
			throw new CourseException("Could not find email field to log in the user.", e);
		}

	}

	public WebElement getPasswordLoginElement() throws CourseException {
		try {
			return driver.findElement(By.cssSelector("div.js-loginBox > form > input[name=\"pw\"]"));
		} catch (NoSuchElementException e) {
			throw new CourseException("Could not find password field to log in the user.", e);
		}
	}

	public WebElement getSumitLoginElement() throws CourseException {
		try {
			return driver.findElement(By.cssSelector("div.js-loginBox > form > input.sf-LogBox__submit"));
		} catch (NoSuchElementException e) {
			LOGGER.debug(e.getMessage(), e);
			throw new CourseException("Could not find submit button to log in the user.");
		}
	}

	private void exitProgram() {
		LOGGER.debug("Closing the web driver");
		driver.quit();
		LOGGER.info("The program is gonna exit");
		System.exit(0);
	}

	private void executeBooking() throws JobExecutionException {
		try {
			bookCourse();
			if (isBooked()) {
				LOGGER.info("The course has been booked");
			} else {
				LOGGER.error("Could not book the course");
			}
		} catch (CourseException e) {
			driver.close();
			throw new JobExecutionException(e.getMessage(), e);
		}
	}

	private void executeLogin() throws JobExecutionException {
		try {
			LOGGER.info("Logging in user " + user.getName());
			login();
			isLoggedIn();
		} catch (CourseException e) {
			driver.close();
			throw new JobExecutionException(e.getMessage(), e);
		}
	}

	private void checkIfCourseExists() throws JobExecutionException {
		try {
			isExistingCourse();
		} catch (CourseException e) {
			driver.close();
			throw new JobExecutionException(e.getMessage(), e);
		}
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getMergedJobDataMap();
		String name = data.getString(ConfLoader.NAME);
		String email = data.getString(ConfLoader.EMAIL);
		String password = data.getString(ConfLoader.AUTHENTICATION_SECRET);
		this.setUser(new User(name, email, password));
		String homePage = data.getString(ConfLoader.COURSE_BOOKING_URL_PROP);
		String id = data.getString(ConfLoader.COURSE_ID);
		this.setCourse(new Course(homePage, id));

		LOGGER.info("The program is gonna book the course " + course.getExpandedBookingUrl() + " for " + user.getName()
				+ " at " + data.getString(ConfLoader.PROGRAMM_EXEC_DATE));
		try {
			checkIfCourseExists();
			executeLogin();
			executeBooking();

		} catch (Exception e) {
			LOGGER.error("An error occured during program execution. See the log file for more information.");
			LOGGER.error(e.getMessage());
			LOGGER.debug(e.getMessage(), e);
			exitProgram();
		}

	}

}
