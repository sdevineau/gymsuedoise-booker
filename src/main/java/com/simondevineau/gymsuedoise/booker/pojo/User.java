package com.simondevineau.gymsuedoise.booker.pojo;

public class User {

	private final String email;
	private final String password;
	private final String name;

	public User(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

}
