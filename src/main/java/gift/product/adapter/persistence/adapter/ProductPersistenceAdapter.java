package gift.product.adapter.persistence.adapter;

import gift.common.annotation.Adapter;
import gift.common.pagination.Page;
import gift.common.pagination.PageImpl;
import gift.common.pagination.Pageable;
import gift.product.application.port.out.ProductPersistencePort;
import gift.product.domain.model.Product;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Adapter
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private static final RowMapper<Product> PRODUCT_ROW_MAPPER = (rs, rowNum) -> Product.of(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("price"),
            rs.getString("image_url")
    );
    private final JdbcClient jdbcClient;

    public ProductPersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Page<Product> findPage(Pageable pageable) {

        int totalRow = getProductTotalRow();

        int start = pageable.getOffset();

        if (start > totalRow) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalRow);
        }

        List<Product> content = jdbcClient.sql("""
                            SELECT id, name, price, image_url
                            FROM PRODUCT
                            LIMIT :limit OFFSET :offset
                        """).param("limit", pageable.getSize())
                .param("offset", start)
                .query(PRODUCT_ROW_MAPPER)
                .list();

        return new PageImpl<>(content, pageable, totalRow);
    }//TODO : 추후 id 기준 방식으로 구현해보는 것도 고려 가능

    private int getProductTotalRow() {
        return jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM Product;
                """).query(Integer.class).single();
    }

    public Optional<Product> findById(Long id) {

        return id == null ? Optional.empty() : jdbcClient.sql(
                        """
                                SELECT id, name, price, image_url
                                FROM PRODUCT
                                WHERE id = :id
                                """).param("id", id)
                .query(PRODUCT_ROW_MAPPER)
                .optional();
    }

    public Product save(Product product) {
        Long id = product.getId();
        if (id == null) {
            id = insertProduct(product);
            return Product.of(id,
                    product.getName(),
                    product.getPrice(),
                    product.getImageUrl());
        }
        updateProduct(product);

        return Product.of(id,
                product.getName(),
                product.getPrice(),
                product.getImageUrl());
    }

    private Long insertProduct(Product product) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(
                        """
                                INSERT INTO PRODUCT (name, price, image_url)
                                VALUES (:name, :price, :imageUrl)
                                """
                ).param("name", product.getName())
                .param("price", product.getPrice())
                .param("imageUrl", product.getImageUrl())
                .update(keyHolder);

        Number key = (Number) keyHolder.getKeys().get("ID");
        return Objects.requireNonNull(key).longValue();
    }

    private void updateProduct(Product product) {
        jdbcClient.sql(
                        """
                                     UPDATE PRODUCT
                                     SET name = :name,
                                         price = :price,
                                         image_url = :imageUrl
                                     WHERE id = :id
                                """
                ).param("id", product.getId())
                .param("name", product.getName())
                .param("price", product.getPrice())
                .param("imageUrl", product.getImageUrl())
                .update();
    }

    public void deleteById(Long id) {
        jdbcClient.sql(
                        """
                                DELETE FROM PRODUCT
                                WHERE id = :id
                                """
                ).param("id", id)
                .update();
    }

    public boolean existsById(Long id) {
        return jdbcClient.sql("SELECT 1 FROM product WHERE id = :id")
                .param("id", id)
                .query(Integer.class)
                .optional()
                .isPresent();
    }
}
