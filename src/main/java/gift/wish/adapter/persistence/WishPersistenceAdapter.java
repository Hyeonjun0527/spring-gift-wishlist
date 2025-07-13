package gift.wish.adapter.persistence;

import gift.common.annotation.Adapter;
import gift.common.pagination.Page;
import gift.common.pagination.PageImpl;
import gift.common.pagination.Pageable;
import gift.member.application.port.out.MemberPersistencePort;
import gift.member.domain.model.Member;
import gift.product.application.port.out.ProductPersistencePort;
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
    private final MemberPersistencePort memberPersistencePort;
    private final ProductPersistencePort productPersistencePort;
    private final RowMapper<Wish> WISH_ROW_MAPPER;

    public WishPersistenceAdapter(JdbcClient jdbcClient, MemberPersistencePort memberPersistencePort, ProductPersistencePort productPersistencePort) {
        this.jdbcClient = jdbcClient;
        this.memberPersistencePort = memberPersistencePort;
        this.productPersistencePort = productPersistencePort;
        this.WISH_ROW_MAPPER = (rs, rowNum) -> {
            Long memberId = rs.getLong("member_id");
            Long productId = rs.getLong("product_id");

            Member member = memberPersistencePort.findById(memberId)
                    .orElseThrow(() -> new IllegalStateException("Member not found with id: " + memberId));
            Product product = productPersistencePort.findById(productId)
                    .orElseThrow(() -> new IllegalStateException("Product not found with id: " + productId));
            return Wish.of(
                    rs.getLong("id"),
                    member,
                    product,
                    rs.getInt("quantity")
            );
        };
    }

    @Override
    public Page<Wish> findByMemberId(Long memberId, Pageable pageable) {
        int totalRow = getWishTotalRowByMemberId(memberId);
        int start = pageable.getOffset();
        if (start > totalRow) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalRow);
        }
        List<Wish> wishes = jdbcClient.sql("""
                SELECT id, member_id, product_id, quantity
                FROM WISH
                WHERE member_id = :memberId
                LIMIT :limit OFFSET :offset
            """)
                .param("memberId", memberId)
                .param("limit", pageable.getSize())
                .param("offset", start)
                .query(WISH_ROW_MAPPER)
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
        return jdbcClient.sql("SELECT id, member_id, product_id, quantity FROM WISH WHERE id = :id")
                .param("id", id)
                .query(WISH_ROW_MAPPER)
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
        return jdbcClient.sql("SELECT id, member_id, product_id, quantity FROM WISH WHERE member_id = :memberId AND product_id = :productId")
                .param("memberId", memberId)
                .param("productId", productId)
                .query(WISH_ROW_MAPPER)
                .optional();
    }
}
