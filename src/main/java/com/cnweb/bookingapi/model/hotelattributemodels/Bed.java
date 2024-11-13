package com.cnweb.bookingapi.model.hotelattributemodels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bed {
    private int bedQuantity;
    private String bedType;
}
