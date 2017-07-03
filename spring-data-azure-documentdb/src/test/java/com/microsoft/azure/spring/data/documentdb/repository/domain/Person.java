package com.microsoft.azure.spring.data.documentdb.repository.domain;


public class Person {
    private String firstName;
    private String lastName;
    private String phone;

    private String id;

    public Person() {
        this(null, null, null, null);
    }

    public Person(String id, String fname, String lname, String phone) {
        this.firstName = fname;
        this.lastName = lname;
        this.phone = phone;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(String id) {
        id = id;
    }

    public void setFirstName(String fname) {
        this.firstName = fname;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
