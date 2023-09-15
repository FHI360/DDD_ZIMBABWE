package org.fhi360.plugins.impilo.services.models;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Name {
    @CsvBindByName(column = "given_name")
    public String givenName;
    @CsvBindByName(column = "family_name")
    public String familyName;
}
