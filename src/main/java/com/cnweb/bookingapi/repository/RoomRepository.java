package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
}
