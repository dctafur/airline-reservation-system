package com.misw.reservation.entity;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Passenger")
public class Passenger {

	@Id
	@UuidGenerator
	private String id;

	private String firstName;

	private String lastName;

	private int age;

	private String gender;

	@Column(unique = true)
	private String phone;

	@OneToMany(targetEntity = Reservation.class, cascade = CascadeType.ALL)
	@JsonIgnoreProperties({ "passenger", "price", "flights" })
	private List<Reservation> reservations;

	public Passenger() {
	};

	public Passenger(
			String firstname,
			String lastname,
			int age,
			String gender,
			String phone) {
		this.firstName = firstname;
		this.lastName = lastname;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

}
