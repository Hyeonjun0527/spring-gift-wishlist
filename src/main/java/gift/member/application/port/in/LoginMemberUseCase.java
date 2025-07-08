package gift.member.application.port.in;

import gift.member.application.port.in.dto.AuthResponse;
import gift.member.application.port.in.dto.LoginRequest;

public interface LoginMemberUseCase {
    AuthResponse login(LoginRequest request);
} 