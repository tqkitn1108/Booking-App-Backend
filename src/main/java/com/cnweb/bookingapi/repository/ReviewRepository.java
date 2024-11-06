package com.cnweb.bookingapi.repository;

import com.cnweb.bookingapi.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {
}
