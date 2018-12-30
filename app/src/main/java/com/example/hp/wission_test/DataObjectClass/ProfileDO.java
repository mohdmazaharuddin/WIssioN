package com.example.hp.wission_test.DataObjectClass;

public class ProfileDO {

    private String name;
    private String profileUrl;
    private String email;
    private String age;
    private String gender;

    public ProfileDO(String name, String age, String gender, String profileUrl, String email) {
        this.age = age;
        this.gender = gender;
        this.name = name;
        this.profileUrl = profileUrl;
        this.email = email;
    }

    public ProfileDO() {
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
