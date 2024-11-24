package com.cnweb.bookingapi.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchSuggestion {
    private String id;
    private String name;
}
