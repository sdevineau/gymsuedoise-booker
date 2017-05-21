package com.simondevineau.gymsuedoise.booker.util;

public class CourseException extends Exception {
	private static final long serialVersionUID = 1L;

	public CourseException(String string) {
		super(string);
	}

	public CourseException(String message, Throwable e) {
		super(message, e);
	}
}
