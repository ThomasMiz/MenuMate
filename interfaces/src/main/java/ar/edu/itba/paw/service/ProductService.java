package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> getById(long productId);

    Product getByIdChecked(long restaurantId, long categoryId, long productId, boolean allowDeleted);

    Promotion getPromotionById(long restaurantId, long promotionId);

    Product create(long restaurantId, long categoryId, String name, String description, Long imageId, BigDecimal price);

    Product update(long restaurantId, long categoryId, long productId, String name, BigDecimal price, String description, Long imageId);

    void delete(long restaurantId, long categoryId, long productId);

    Promotion createPromotion(long restaurantId, long sourceProductId, LocalDateTime startDate, LocalDateTime endDate, BigDecimal discountPercentage);

    /**
     * Gets whether a product has any promotions whose active time range intersects with the specified time range.
     *
     * @return An empty optional if no promotions were found, or one (any) promotion if at least one was found.
     */
    Optional<Promotion> hasPromotionInRange(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate);

    void stopPromotion(long restaurantId, long promotionId);

    boolean areAllProductsFromRestaurant(long restaurantId, List<Long> productIds);
}
