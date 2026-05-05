package com.group273.course_registration.model;

// ABSTRACTION: Abstract class that cannot be instantiated directly
// Student and Lecturer will both inherit from this

public abstract class Person {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // Constructor
    public Person() {}

    public Person(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // ABSTRACTION: Abstract method — every Person must have a role
    // but each class defines what that role is
    public abstract String getRole();

    // ENCAPSULATION: Private fields accessed through public getters/setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // Returns full name — can be used by all child classes
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // POLYMORPHISM: toString() overridden
    @Override
    public String toString() {
        return "Person{name=" + getFullName() + ", role=" + getRole() + "}";



    }
}