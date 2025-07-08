package gift.member.application.port.out;

import gift.member.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberPersistencePort {
    Member save(Member member);
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(Long id);
    boolean existsByEmail(String email);
    boolean existsById(Long id);
    List<Member> findAll();
    void deleteById(Long id);
}