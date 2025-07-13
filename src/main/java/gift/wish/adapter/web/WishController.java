package gift.wish.adapter.web;

import gift.common.annotation.LoginMember;
import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.member.application.port.in.dto.MemberResponse;
import gift.wish.application.port.in.WishUseCase;
import gift.wish.application.port.in.dto.WishAddRequest;
import gift.wish.application.port.in.dto.WishResponse;
import gift.wish.application.port.in.dto.WishUpdateQuantityRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishUseCase wishUseCase;

    public WishController(WishUseCase wishUseCase) {
        this.wishUseCase = wishUseCase;
    }

    @GetMapping
    public ResponseEntity<Page<WishResponse>> getWishes(Pageable pageable, @LoginMember MemberResponse member) {
        Page<WishResponse> wishes = wishUseCase.getWishes(member.id(), pageable);
        return ResponseEntity.ok(wishes);
    }

    @PostMapping
    public ResponseEntity<WishResponse> addWish(@RequestBody @Valid WishAddRequest request, @LoginMember MemberResponse member) {
        WishResponse response = wishUseCase.addWish(request, member.id());
        return ResponseEntity.created(URI.create("/api/wishes/" + response.wishId())).body(response);
    }

    @PutMapping("/{wishId}/quantity")
    public ResponseEntity<WishResponse> updateWishQuantity(@PathVariable Long wishId,
                                                           @RequestBody @Valid WishUpdateQuantityRequest request,
                                                           @LoginMember MemberResponse member) {
        WishResponse response = wishUseCase.updateWishQuantity(wishId, request.quantity(), member.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWish(@PathVariable Long wishId, @LoginMember MemberResponse member) {
        wishUseCase.deleteWish(wishId, member.id());
        return ResponseEntity.noContent().build();
    }
} 