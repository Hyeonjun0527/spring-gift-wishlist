package gift.member.application.usecase;

import gift.common.exception.ForbiddenException;
import gift.common.jwt.JwtTokenPort;
import gift.common.security.PasswordEncoder;
import gift.member.adapter.web.mapper.MemberMapper;
import gift.member.application.port.in.MemberUseCase;
import gift.member.application.port.in.dto.*;
import gift.member.application.port.out.MemberPersistencePort;
import gift.member.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberInteractor implements MemberUseCase {

    private final MemberPersistencePort memberPersistencePort;
    private final JwtTokenPort jwtTokenPort;
    private final PasswordEncoder passwordEncoder;

    public MemberInteractor(
            MemberPersistencePort memberPersistencePort,
            JwtTokenPort jwtTokenPort,
            PasswordEncoder passwordEncoder) {
        this.memberPersistencePort = memberPersistencePort;
        this.jwtTokenPort = jwtTokenPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (memberPersistencePort.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = Member.create(request.email(), encodedPassword);
        Member savedMember = memberPersistencePort.save(member);

        return createAuthResponse(savedMember.id(), savedMember.email(), savedMember.role());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Member member = memberPersistencePort.findByEmail(request.email())
                .orElseThrow(() -> new ForbiddenException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.password())) {
            throw new ForbiddenException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return createAuthResponse(member.id(), member.email(), member.role());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberResponse> getAllMembers() {
        List<Member> members = memberPersistencePort.findAll();
        return members.stream()
                .map(MemberMapper::toResponse)
                .toList();
    }

    @Override
    public MemberResponse createMember(CreateMemberRequest request) {
        if (memberPersistencePort.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = Member.create(request.email(), encodedPassword);
        Member savedMember = memberPersistencePort.save(member);

        return MemberMapper.toResponse(savedMember);
    }

    @Override
    public MemberResponse updateMember(Long id, UpdateMemberRequest request) {
        Member existingMember = memberPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String encodedPassword = request.password() != null ? 
                passwordEncoder.encode(request.password()) : existingMember.password();

        Member updatedMember = Member.of(
                existingMember.id(),
                request.email() != null ? request.email() : existingMember.email(),
                encodedPassword,
                request.role() != null ? request.role() : existingMember.role(),
                existingMember.createdAt()
        );

        Member savedMember = memberPersistencePort.save(updatedMember);
        return MemberMapper.toResponse(savedMember);
    }

    @Override
    public void deleteMember(Long id) {
        if (!memberPersistencePort.existsById(id)) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }
        memberPersistencePort.deleteById(id);
    }

    @Override
    public MemberResponse getMemberById(Long memberId) {
        Member member = memberPersistencePort.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id: " + memberId));
        return MemberMapper.toResponse(member);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberPersistencePort.findByEmail(email).orElse(null);
    }

    private AuthResponse createAuthResponse(Long memberId, String email, gift.member.domain.model.Role role) {
        String accessToken = jwtTokenPort.createAccessToken(memberId, email, role);
        return new AuthResponse(accessToken);
    }
}