package gift.common.security;

import gift.common.annotation.RequireAdmin;
import gift.common.exception.ForbiddenException;
import gift.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@ConditionalOnProperty(
    name = "jwt.enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
public class AuthorizationAspect implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
            
            if (requireAdmin != null) {
                checkAdminPermission(request);
            }
        }
        return true;
    }
    
    private void checkAdminPermission(HttpServletRequest request) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        if (authenticated == null || !authenticated) {
            throw new UnauthorizedException("유효한 인증 자격 증명이 필요합니다.");
        }
        
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            throw new ForbiddenException("관리자 권한이 필요합니다. 접근이 거부되었습니다.");
        }
    }
}
