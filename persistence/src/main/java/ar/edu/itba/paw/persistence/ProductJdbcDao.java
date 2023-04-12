package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductJdbcDao implements ProductDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<Product> productRowMapper = (ResultSet rs, int rowNum) -> new Product(
            rs.getLong("product_id"),
            rs.getLong("category_id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getString("description"),
            rs.getLong("image_id"),
            rs.getBoolean("available")
    );

    @Autowired
    public ProductJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("products")
                .usingGeneratedKeyColumns("product_id");
    }
    @Override
    public Product createProduct(long categoryId, String name, double price) {
        final Map<String, Object> productData = new HashMap<>();
        productData.put("category_id", categoryId);
        productData.put("name", name);
        productData.put("price", price);

        final int productId = jdbcInsert.execute(productData);
        return new Product(productId, categoryId, name, price);
    }

    @Override
    public Optional<Product> findProductById(long productId) {
        return jdbcTemplate.query("SELECT * FROM products WHERE product_id = ?", productRowMapper, productId).stream().findFirst();
    }

    @Override
    public List<Product> findProductsByCategory(long categoryId) {
        return jdbcTemplate.query("SELECT * FROM products WHERE category_id = ?", productRowMapper, categoryId);
    }

    @Override
    public void updateProductPrice(long productId, double price) {
        jdbcTemplate.update("UPDATE products SET price = ? WHERE product_id = ?", price, productId);
    }

    @Override
    public void updateProductName(long productId, String name) {
        jdbcTemplate.update("UPDATE products SET name = ? WHERE product_id = ?", name, productId);
    }

    @Override
    public void deleteProduct(long productId) {
        jdbcTemplate.update("DELETE FROM products WHERE product_id = ?", productId);
    }

}