package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomTypeRepository extends MongoRepository<RoomType, String> {
}