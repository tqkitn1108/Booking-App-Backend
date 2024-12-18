package com.cnweb.bookingapi.model.hotelattributemodels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "facilities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facility {
    @Id
    private String id;
    private String name;
    private String label;
}
