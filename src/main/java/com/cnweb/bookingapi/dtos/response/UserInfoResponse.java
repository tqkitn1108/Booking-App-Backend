package com.cnweb.bookingapi.dtos.response;

import com.cnweb.bookingapi.model.Role;
import com.cnweb.bookingapi.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private String userId;
    private String userEmail;
    private String userFullName;
    private Role userRole;

    public UserInfoResponse(User user) {
        this.userId = user.getId();
        this.userEmail = user.getEmail();
        this.userFullName = user.getFullName();
        this.userRole = user.getRole();
    }
}