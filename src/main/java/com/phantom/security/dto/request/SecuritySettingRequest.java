package com.phantom.security.dto.request;

import jakarta.validation.constraints.NotNull;

public class SecuritySettingRequest {

    @NotNull(message = "설정 값은 필수입니다.")
    private Boolean isEnabled;

    // Getter와 Setter
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}