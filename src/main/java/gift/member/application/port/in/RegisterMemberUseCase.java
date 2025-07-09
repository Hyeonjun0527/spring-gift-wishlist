package gift.member.application.port.in;

import gift.member.application.port.in.dto.AuthResponse;
import gift.member.application.port.in.dto.RegisterRequest;

public interface RegisterMemberUseCase {
    AuthResponse register(RegisterRequest request);
} 