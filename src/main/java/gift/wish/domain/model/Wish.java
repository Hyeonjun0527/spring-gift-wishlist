package gift.wish.domain.model;

import java.time.LocalDateTime;

public class Wish {
    private final long id;
    private final long memberId;
    private final long productId;
    private final LocalDateTime createdAt;

    private Wish(long id, long memberId, long productId, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    public static Wish of(long id, long memberId, long productId, LocalDateTime createdAt) {
        return new Wish(id, memberId, productId, createdAt);
    }

    public long getId() {
        return id;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getProductId() {
        return productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
