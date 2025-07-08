package gift.member.application.port.in;

import gift.member.application.port.in.dto.AuthResponse;
import gift.member.application.port.in.dto.SignUpRequest;

public interface SignUpMemberUseCase {
    AuthResponse signUp(SignUpRequest request);
} 