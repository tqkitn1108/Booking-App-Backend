package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.Review;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final MongoTemplate mongoTemplate;
    public List<Review> allReviews(ObjectId hotelId) {
        return Objects.requireNonNull(hotelRepository.findById(hotelId).orElse(null)).getReviews();
    }

    public List<Review> topRating(ObjectId hotelId) {
        List<Review> reviewList = allReviews(hotelId);
        reviewList.sort((o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));
        return reviewList.subList(0, 5);
    }

    public Review newReview(String content, int rating, ObjectId hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow();
        int numberOfReviews = hotel.getReviews().size();
        hotel.setRating((hotel.getRating() * numberOfReviews + rating) / (numberOfReviews + 1));
        Review review = new Review(content, rating);
        review.setReviewDate(LocalDate.now());
        reviewRepository.insert(review);
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().push("reviews").value(review))
                .first();
        return review;
    }

    public Review updateReview(ObjectId id, Review review) {
        Review currentReview = reviewRepository.findById(id).orElseThrow();
        Class<? extends Review> reviewClass = review.getClass();
        for (Field field : reviewClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(review);
                if (value != null) {
                    field.set(currentReview, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return reviewRepository.save(currentReview);
    }

    public void deleteReview(ObjectId hotelId, ObjectId reviewId) {
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().pull("reviews", reviewRepository.findById(reviewId).orElseThrow()))
                .first();
        reviewRepository.deleteById(reviewId);
    }
}
