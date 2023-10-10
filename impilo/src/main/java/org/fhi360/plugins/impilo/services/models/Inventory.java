package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Inventory {
    private UUID reference;
    private Integer balance;
}
