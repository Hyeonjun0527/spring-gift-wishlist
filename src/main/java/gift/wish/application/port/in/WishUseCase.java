package gift.wish.application.port.in;

import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.wish.application.port.in.dto.WishAddRequest;
import gift.wish.application.port.in.dto.WishResponse;


public interface WishUseCase {
    Page<WishResponse> getWishes(Long memberId, Pageable pageable);

    WishResponse addWish(WishAddRequest request, Long memberId);

    WishResponse updateWishQuantity(Long wishId, int quantity, Long memberId);

    void deleteWish(Long wishId, Long memberId);
} 