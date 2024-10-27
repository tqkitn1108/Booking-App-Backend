package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.Hotel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HotelRepository extends MongoRepository<Hotel, ObjectId> {
}
