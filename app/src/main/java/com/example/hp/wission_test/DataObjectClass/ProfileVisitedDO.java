package com.example.hp.wission_test.DataObjectClass;

public class ProfileVisitedDO {

    private String email;
    private boolean visitedProfiePage;

    public ProfileVisitedDO() {
    }

    public ProfileVisitedDO(String email, boolean visitedProfiePage) {
        this.email = email;
        this.visitedProfiePage = visitedProfiePage;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVisitedProfiePage() {
        return visitedProfiePage;
    }

}
