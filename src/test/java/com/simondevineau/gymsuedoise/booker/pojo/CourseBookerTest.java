package com.simondevineau.gymsuedoise.booker.pojo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.simondevineau.gymsuedoise.booker.conf.ConfLoader;
import com.simondevineau.gymsuedoise.booker.pojo.Course;
import com.simondevineau.gymsuedoise.booker.pojo.CourseBooker;
import com.simondevineau.gymsuedoise.booker.pojo.User;
import com.simondevineau.gymsuedoise.booker.util.CourseException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CourseBookerTest {

	private static WebDriver driver;
	private static CourseBooker courseRegister;

	@BeforeClass
	public static void setup() {
		driver = new ChromeDriver();
	}

	@Before
	public void init() {
		String id = "testId";
		User user = new User("Marine", "marine.arnaud85@gmail.com", "Mouti1012");
		Course course = new Course(ConfLoader.DEFAULT_BOOKING_COURSE_URL, id);
		courseRegister = new CourseBooker(driver, course, user);
		courseRegister.getHomePageElement();
	}

	@AfterClass
	public static void tearDown() {
		driver.close();
	}

	@Test
	public void z1_should_login() {
		try {
			courseRegister.login();
		} catch (CourseException e) {
			Assert.fail();
		}
	}

	@Test
	public void z2_should_be_logged() throws CourseException {
		Assert.assertTrue(courseRegister.isLoggedIn());
	}

	@Test
	public void should_get_email_login_field() throws CourseException {
		Assert.assertNotNull(courseRegister.getEmailLoginElement());
	}

	@Test
	public void should_get_password_login_field() throws CourseException {
		Assert.assertNotNull(courseRegister.getPasswordLoginElement());

	}

	@Test
	public void should_get_submitlogin_btn() throws CourseException {
		Assert.assertNotNull(courseRegister.getSumitLoginElement());

	}

	@Test
	public void should_get_suitable_course_url() {
		String expected = ConfLoader.DEFAULT_BOOKING_COURSE_URL + "testId";
		Assert.assertEquals(expected, courseRegister.getCourse().getExpandedBookingUrl());
	}

	@Test(expected = CourseException.class)
	public void course_should_not_exist() throws CourseException {
		Assert.assertFalse(courseRegister.isExistingCourse());
	}

	@Test(expected = CourseException.class)
	public void z3_course_should_exist() throws CourseException {
		courseRegister.getCourse().setId("365984");
		Assert.assertFalse(courseRegister.isExistingCourse());
	}

	@Test(expected = CourseException.class)
	public void should_be_booked() throws CourseException {
		Assert.assertFalse(courseRegister.isBooked());
	}

}
