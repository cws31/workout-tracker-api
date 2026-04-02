package com.workouttrackerapi.admin.dto;

import jakarta.validation.constraints.NotNull;

public class BlockUserRequestDto {

    @NotNull(message = "admin should either block or unblock the user")
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}