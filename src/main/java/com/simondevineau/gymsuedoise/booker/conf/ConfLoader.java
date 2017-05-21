package com.simondevineau.gymsuedoise.booker.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simondevineau.gymsuedoise.booker.util.CourseException;

public class ConfLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfLoader.class);
	public static final String CONFIGURATION_FILE_PATH = "configuration.properties";
	public static final String CONFIGURATION_FILE_PATH_PARAM = "conf.file.path";
	public static final String NAME = "user.firstname";
	public static final String AUTHENTICATION_SECRET = "user.password";
	public static final String PROGRAMM_EXEC_DATE = "program.execution.date";
	public static final String COURSE_ID = "course.id";
	public static final String EMAIL = "user.email";
	public static final String COURSE_BOOKING_URL_PROP = "course.home.page";
	public static final String DEFAULT_BOOKING_COURSE_URL = "https://www.gymsuedoise.com/resa/bk/?id=";
	private static ConfLoader instance;
	private Properties properties;

	private ConfLoader(File propertiesFile) throws CourseException {

		this.properties = new Properties(getDefaultProperties());
		try {
			this.properties.load(new FileInputStream(propertiesFile));
			putIfAbsent(properties, COURSE_BOOKING_URL_PROP, DEFAULT_BOOKING_COURSE_URL);
			LOGGER.debug("Loaded configuration from " + propertiesFile.getAbsolutePath());

		} catch (IOException e) {
			throw new CourseException("Could not load the configuration file located at " + propertiesFile, e);
		}
		File chromedriverPath = null;
		if (Utils.isWindows()) {
			chromedriverPath = new File("selenium/windows/googlechrome/64bit/chromedriver.exe");
		} else if (Utils.isMac()) {
			chromedriverPath = new File("selenium/osx/googlechrome/64bit/chromedriver");
		}else{
			chromedriverPath = new File("UNDEFINED");
		}
		LOGGER.debug("Using " + chromedriverPath.getAbsolutePath() + " chrome driver");
		System.setProperty("webdriver.chrome.driver", chromedriverPath.getAbsolutePath());
	}

	private void putIfAbsent(Properties properties, String key, String value) {
		if (StringUtils.isBlank("" + properties.getProperty(key))) {
			properties.put(key, value);
		}
	}


	public static ConfLoader getInstance() throws CourseException {
		if (instance != null) {
			return instance;
		}
		String confPathParameter = System.getProperty(CONFIGURATION_FILE_PATH_PARAM);
		String confFilePath = confPathParameter == null ? CONFIGURATION_FILE_PATH : confPathParameter;
		File confFile = new File(confFilePath);
		if (confFile.exists()) {
			return new ConfLoader(confFile);
		}
		throw new CourseException("Could not find configuration file mandatory to run the program");

	}

	public String getName() throws CourseException {
		return getPropertyOrException(NAME);
	}

	public String getPassword() throws CourseException {
		return getPropertyOrException(AUTHENTICATION_SECRET);
	}

	public String getEmail() throws CourseException {
		return getPropertyOrException(EMAIL);
	}

	public String getCourseId() throws CourseException {
		return getPropertyOrException(COURSE_ID);
	}

	public String getStringDate() throws CourseException {
		return getPropertyOrException(PROGRAMM_EXEC_DATE);
	}

	public String getCourseUrl() throws CourseException {
		return getPropertyOrException(COURSE_BOOKING_URL_PROP);
	}

	public Date getDate() throws CourseException {
		String stringFormat = "dd/MM/yyyy-HH:mm";
		DateFormat format = new SimpleDateFormat(stringFormat, Locale.FRENCH);
		String source = getStringDate();
		try {
			return format.parse(source);
		} catch (ParseException e) {
			throw new CourseException("Could not parse the date given. Please respect the format " + stringFormat, e);
		}

	}

	private String getPropertyOrException(String key) throws CourseException {
		String name = properties.getProperty(key);
		if (StringUtils.isNotBlank(name)) {
			return name;
		}
		throw new CourseException(
				"Could not load the " + key + " in the configuration file. Are you sure you filled it properly?");
	}

	public JobDataMap getJobDataMap() throws CourseException {
		// Call the methods do check if property are well set
		getName();
		getEmail();
		getPassword();
		getCourseId();
		getDate();
		return new JobDataMap(properties);

	}

	private Properties getDefaultProperties() {
		Properties prop = new Properties();
		prop.put(COURSE_BOOKING_URL_PROP, DEFAULT_BOOKING_COURSE_URL);
		return prop;
	}

}
