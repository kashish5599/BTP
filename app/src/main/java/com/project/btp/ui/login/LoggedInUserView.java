package com.project.btp.ui.login;

import com.project.btp.data.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private LoggedInUser user;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(LoggedInUser user) {
        this.user = user;
    }

    String getDisplayName() {
        return user.getDisplayName();
    }

    Boolean getIsTeacher() {
        return user.getIsTeacher();
    }

    String getUserId() {
        return user.getUserId();
    }
}