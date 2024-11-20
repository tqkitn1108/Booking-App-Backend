package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.ReviewDto;
import com.cnweb.bookingapi.model.Booking;
import com.cnweb.bookingapi.model.Hotel;
import com.cnweb.bookingapi.model.Review;
import com.cnweb.bookingapi.repository.HotelRepository;
import com.cnweb.bookingapi.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
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

    public List<Review> allReviews(String hotelId) {
        return Objects.requireNonNull(hotelRepository.findById(hotelId).orElse(null)).getReviews();
    }

    public List<Review> topRating(String hotelId) {
        List<Review> reviewList = allReviews(hotelId);
        reviewList.sort((o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));
        return reviewList.subList(0, 5);
    }

    public Review newReview(ReviewDto input, String hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow();
        hotel.updateRating(input.getRating());
        hotelRepository.save(hotel);
        Review review = new Review(input.getBookingId(), input.getFullName(), input.getRating(), input.getContent());
        review.setReviewDate(LocalDate.now());
        reviewRepository.insert(review);
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().push("reviews").value(review))
                .first();
        Objects.requireNonNull(mongoTemplate.findById(input.getBookingId(), Booking.class)).setIsRated(true);
        return review;
    }

    public Review updateReview(String id, Review review) {
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

    public void deleteReview(String hotelId, String reviewId) {
        mongoTemplate.update(Hotel.class)
                .matching(Criteria.where("id").is(hotelId))
                .apply(new Update().pull("reviews", reviewRepository.findById(reviewId).orElseThrow()))
                .first();
        reviewRepository.deleteById(reviewId);
    }
}
