package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.Hotel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends MongoRepository<Hotel, ObjectId> {
    Optional<Hotel> findByName(String name);
    List<Hotel> findAllByLocality(String locality);
}
