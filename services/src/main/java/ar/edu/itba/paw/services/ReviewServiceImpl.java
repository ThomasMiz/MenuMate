package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.util.AverageCountPair;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.ReviewDao;
import ar.edu.itba.paw.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Override
    public boolean createOrUpdate(int orderId, int rating, String comment) {
        return reviewDao.createOrUpdate(orderId, rating, comment);
    }

    @Override
    public Optional<Review> getByOrder(int orderId) {
        return reviewDao.getByOrder(orderId);
    }

    @Override
    public AverageCountPair getRestaurantAverage(int restaurantId) {
        return reviewDao.getRestaurantAverage(restaurantId);
    }

    @Override
    public AverageCountPair getRestaurantAverageSince(int restaurantId, LocalDateTime datetime) {
        return reviewDao.getRestaurantAverageSince(restaurantId, datetime);
    }

    @Override
    public PaginatedResult<Review> getByRestaurant(int restaurantId) {
        return reviewDao.getByRestaurant(restaurantId);
    }

    @Override
    public PaginatedResult<Review> getByUser(int userId) {
        return reviewDao.getByUser(userId);
    }
}
