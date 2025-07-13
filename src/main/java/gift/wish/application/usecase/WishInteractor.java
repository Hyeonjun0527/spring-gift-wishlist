package gift.wish.application.usecase;

import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.member.application.port.out.MemberPersistencePort;
import gift.member.domain.model.Member;
import gift.product.application.port.out.ProductPersistencePort;
import gift.product.domain.model.Product;
import gift.wish.adapter.web.mapper.WishMapper;
import gift.wish.application.port.in.WishUseCase;
import gift.wish.application.port.in.dto.WishAddRequest;
import gift.wish.application.port.in.dto.WishResponse;
import gift.wish.application.port.out.WishPersistencePort;
import gift.wish.domain.model.Wish;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WishInteractor implements WishUseCase {

    private final WishPersistencePort wishPersistencePort;
    private final MemberPersistencePort memberPersistencePort;
    private final ProductPersistencePort productPersistencePort;

    public WishInteractor(WishPersistencePort wishPersistencePort, MemberPersistencePort memberPersistencePort, ProductPersistencePort productPersistencePort) {
        this.wishPersistencePort = wishPersistencePort;
        this.memberPersistencePort = memberPersistencePort;
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishResponse> getWishes(Long memberId, Pageable pageable) {
        Page<Wish> wishes = wishPersistencePort.findByMemberId(memberId, pageable);
        return wishes.map(WishMapper::toResponse);
    }

    @Override
    public WishResponse addWish(WishAddRequest request, Long memberId) {
        return wishPersistencePort.findByMemberIdAndProductId(memberId, request.productId())
                .map(existingWish -> {
                    existingWish.updateQuantity(existingWish.getQuantity() + request.quantity());
                    return WishMapper.toResponse(wishPersistencePort.save(existingWish));
                })
                .orElseGet(() -> {
                    Member member = memberPersistencePort.findById(memberId)
                            .orElseThrow(() -> new IllegalArgumentException("Member not found"));
                    Product product = productPersistencePort.findById(request.productId())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                    Wish newWish = Wish.of(null, member, product, request.quantity());
                    return WishMapper.toResponse(wishPersistencePort.save(newWish));
                });
    }

    @Override
    public WishResponse updateWishQuantity(Long wishId, int quantity, Long memberId) {
        Wish wish = wishPersistencePort.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("Wish not found"));
        if (!wish.getMember().id().equals(memberId)) {
            throw new SecurityException("Not authorized to update this wish");
        }
        wish.updateQuantity(quantity);
        return WishMapper.toResponse(wishPersistencePort.save(wish));
    }

    @Override
    public void deleteWish(Long wishId, Long memberId) {
        Wish wish = wishPersistencePort.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("Wish not found"));
        if (!wish.getMember().id().equals(memberId)) {
            throw new SecurityException("Not authorized to delete this wish");
        }
        wishPersistencePort.deleteById(wishId);
    }
}
