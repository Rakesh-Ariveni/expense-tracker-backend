package com.rakesh.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RegisterRequest {

    @Schema(example = "Rupesh")
    private String name;

    @Schema(example = "rupesh@test.com")
    private String email;

    @Schema(example = "1234")
    private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
