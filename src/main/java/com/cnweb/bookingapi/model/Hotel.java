package com.cnweb.bookingapi.model;

import com.cnweb.bookingapi.model.hotelattributemodels.Facility;
import com.cnweb.bookingapi.model.hotelattributemodels.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "hotels")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {
    @Id
    //    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private Integer star;
    @DocumentReference(lazy = true)
    private PropertyType type;
    private String dest;
    private String address;
    private String distance;
    private List<String> photos;
    @DocumentReference(lazy = true)
    private List<Facility> facilities;
    private String description;
    private Float rating;
    @DocumentReference
    private List<RoomType> roomTypes;
    @DocumentReference
    private List<Review> reviews;
    @DocumentReference(lazy = true)
    private List<Booking> bookings;

    public void updateRating(Integer reviewRating) {
        int numberOfReviews = reviews.size();
        rating = (rating * numberOfReviews + reviewRating) / (numberOfReviews + 1);
    }
}