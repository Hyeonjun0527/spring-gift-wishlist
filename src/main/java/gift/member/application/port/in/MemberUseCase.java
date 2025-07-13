package gift.member.application.port.in;

import gift.member.application.port.in.dto.*;

import java.util.List;

public interface MemberUseCase {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    
    List<MemberResponse> getAllMembers();
    MemberResponse createMember(CreateMemberRequest request);
    MemberResponse updateMember(Long id, UpdateMemberRequest request);
    void deleteMember(Long id);
} 