package gift.wish.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WishAddRequest(
        @NotNull
        Long productId,

        @Min(1)
        int quantity
) {
} 