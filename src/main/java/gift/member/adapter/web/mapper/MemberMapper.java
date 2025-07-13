package gift.member.adapter.web.mapper;

import gift.member.application.port.in.dto.CreateMemberRequest;
import gift.member.application.port.in.dto.MemberResponse;
import gift.member.application.port.in.dto.UpdateMemberRequest;
import gift.member.domain.model.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(CreateMemberRequest request) {
        return Member.create(request.email(), request.password());
    }

    public Member toEntity(Member existingMember, UpdateMemberRequest request) {
        return Member.of(
                existingMember.id(),
                request.email() != null ? request.email() : existingMember.email(),
                request.password() != null ? request.password() : existingMember.password(),
                request.role() != null ? request.role() : existingMember.role(),
                existingMember.createdAt()
        );
    }

    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.id(),
                member.email(),
                member.role(),
                member.createdAt()
        );
    }
} 