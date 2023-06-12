package org.fhi360.plugins.impilo.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncDetail {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
