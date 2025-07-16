package gift.wish.adapter.persistence.adapter;

import gift.common.annotation.Adapter;
import gift.common.pagination.Page;
import gift.common.pagination.PageImpl;
import gift.common.pagination.Pageable;
import gift.member.domain.model.Member;
import gift.member.domain.model.Role;
import gift.product.domain.model.Product;
import gift.wish.application.port.out.WishPersistencePort;
import gift.wish.domain.model.Wish;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Adapter
public class WishPersistenceAdapter implements WishPersistencePort {

    private final JdbcClient jdbcClient;

    private static final String WISH_WITH_DETAILS_BASE_SQL = """
            SELECT w.id         AS wish_id,
                   w.quantity,
                   m.id         AS member_id,
                   m.email,
                   m.password,
                   m.role,
                   m.created_at,
                   p.id         AS product_id,
                   p.name,
                   p.price,
                   p.image_url
            FROM WISH w
                     JOIN MEMBER m ON w.member_id = m.id
                     JOIN PRODUCT p ON w.product_id = p.id
            """;

    private final RowMapper<Wish> WISH_WITH_DETAILS_ROW_MAPPER = (rs, rowNum) -> {
        Member member = Member.of(
                rs.getLong("member_id"),
                rs.getString("email"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        Product product = Product.of(
                rs.getLong("product_id"),
                rs.getString("name"),
                rs.getInt("price"),
                rs.getString("image_url")
        );

        return Wish.of(
                rs.getLong("wish_id"),
                member,
                product,
                rs.getInt("quantity")
        );
    };

    public WishPersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Page<Wish> findByMemberId(Long memberId, Pageable pageable) {
        int totalRow = getWishTotalRowByMemberId(memberId);
        int offset = pageable.getOffset();
        if (offset >= totalRow) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalRow);
        }
        String sql = WISH_WITH_DETAILS_BASE_SQL + """
                WHERE w.member_id = :memberId
                ORDER BY w.id DESC
                LIMIT :limit OFFSET :offset
                """;
        List<Wish> wishes = jdbcClient.sql(sql)
                .param("memberId", memberId)
                .param("limit", pageable.getSize())
                .param("offset", offset)
                .query(WISH_WITH_DETAILS_ROW_MAPPER)
                .list();
        return new PageImpl<>(wishes, pageable, totalRow);
    }

    private int getWishTotalRowByMemberId(Long memberId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM WISH WHERE member_id = :memberId")
                .param("memberId", memberId)
                .query(Integer.class)
                .single();
    }

    @Override
    public Wish save(Wish wish) {
        if (wish.getId() == null) {
            Long id = insertWish(wish);
            return findById(id).orElseThrow(() -> new IllegalStateException("Failed to save wish"));
        } else {
            updateWish(wish);
            return wish;
        }
    }

    private Long insertWish(Wish wish) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                INSERT INTO WISH (member_id, product_id, quantity)
                VALUES (:memberId, :productId, :quantity)
            """)
                .param("memberId", wish.getMember().id())
                .param("productId", wish.getProduct().getId())
                .param("quantity", wish.getQuantity())
                .update(keyHolder);
        Number key = (Number) Objects.requireNonNull(keyHolder.getKeys()).get("ID");
        return key.longValue();
    }

    private void updateWish(Wish wish) {
        jdbcClient.sql("""
                UPDATE WISH
                SET member_id = :memberId,
                    product_id = :productId,
                    quantity = :quantity
                WHERE id = :id
            """)
                .param("id", wish.getId())
                .param("memberId", wish.getMember().id())
                .param("productId", wish.getProduct().getId())
                .param("quantity", wish.getQuantity())
                .update();
    }

    @Override
    public Optional<Wish> findById(Long id) {
        String sql = WISH_WITH_DETAILS_BASE_SQL + " WHERE w.id = :id";
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(WISH_WITH_DETAILS_ROW_MAPPER)
                .optional();
    }

    @Override
    public void deleteById(Long id) {
        jdbcClient.sql("DELETE FROM WISH WHERE id = :id")
                .param("id", id)
                .update();
    }

    @Override
    public Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId) {
        String sql = WISH_WITH_DETAILS_BASE_SQL + " WHERE w.member_id = :memberId AND w.product_id = :productId";
        return jdbcClient.sql(sql)
                .param("memberId", memberId)
                .param("productId", productId)
                .query(WISH_WITH_DETAILS_ROW_MAPPER)
                .optional();
    }
}
