package com.simondevineau.gymsuedoise.booker.pojo;

public class Course {

	private String bookingUrl;
	private String id;

	public Course(String bookingUrl, String id) {
		super();
		this.bookingUrl = bookingUrl;
		this.setId(id);

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExpandedBookingUrl() {
		return getBookingUrl() + getId();
	}
	
	public String getDetailsUrl() {
		return "https://www.gymsuedoise.com/cours/detail/?id=" + getId();
	}

	private String getBookingUrl() {
		return bookingUrl;
	}

	public void setBookingUrl(String bookingUrl) {
		this.bookingUrl = bookingUrl;
	}

}
