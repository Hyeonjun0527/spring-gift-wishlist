package gift.wish.application.port.out;

import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.wish.domain.model.Wish;

import java.util.Optional;

public interface WishPersistencePort {
    Page<Wish> findByMemberId(Long memberId, Pageable pageable);

    Wish save(Wish wish);

    Optional<Wish> findById(Long id);

    void deleteById(Long id);

    Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId);
}
