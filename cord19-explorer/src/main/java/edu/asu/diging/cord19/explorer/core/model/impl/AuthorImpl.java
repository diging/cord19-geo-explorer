package edu.asu.diging.cord19.explorer.core.model.impl;

public class AuthorImpl {

    private String lastName;
    private String firstName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    @Override
    public boolean equals(Object obj) {
        AuthorImpl name = (AuthorImpl) obj;
        return (this.firstName+this.lastName).equals(name.getFirstName()+name.getLastName());
        
    }

}
