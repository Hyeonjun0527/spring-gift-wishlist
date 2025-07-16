package gift.wish.application.usecase;

import gift.common.exception.UnauthorizedException;
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
                            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
                    Product product = productPersistencePort.findById(request.productId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                    Wish newWish = Wish.of(null, member, product, request.quantity());
                    return WishMapper.toResponse(wishPersistencePort.save(newWish));
                });
    }

    @Override
    public WishResponse updateWishQuantity(Long wishId, int quantity, Long memberId) {
        Wish wish = wishPersistencePort.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("위시리스트를 찾을 수 없습니다."));
        if (!wish.getMember().id().equals(memberId)) {
            throw new UnauthorizedException("위시리스트를 수정할 수 없습니다.");
        }
        wish.updateQuantity(quantity);
        return WishMapper.toResponse(wishPersistencePort.save(wish));
    }

    @Override
    public void deleteWish(Long wishId, Long memberId) {
        Wish wish = wishPersistencePort.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("위시리스트를 찾을 수 없습니다."));
        if (!wish.getMember().id().equals(memberId)) {
            throw new UnauthorizedException("위시리스트를 삭제할 수 없습니다.");
        }
        wishPersistencePort.deleteById(wishId);
    }
}
