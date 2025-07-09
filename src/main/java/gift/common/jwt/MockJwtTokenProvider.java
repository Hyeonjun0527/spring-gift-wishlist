package gift.common.jwt;

import gift.member.domain.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class MockJwtTokenProvider implements JwtTokenPort {

    @Override
    public String createAccessToken(Long memberId, String email, Role role) {
        return "test-token";
    }

    @Override
    public JwtTokenProvider.TokenValidationResult validateToken(String token) {
        return JwtTokenProvider.TokenValidationResult.valid(null);
    }

    @Override
    public String getEmailFromToken(String token) {
        return "test@example.com";
    }

    @Override
    public Long getMemberIdFromToken(String token) {
        return 1L;
    }

    @Override
    public String getTokenTypeFromToken(String token) {
        return "access";
    }

    @Override
    public String getRoleFromToken(String token) {
        return "USER";
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        return null;
    }
} 