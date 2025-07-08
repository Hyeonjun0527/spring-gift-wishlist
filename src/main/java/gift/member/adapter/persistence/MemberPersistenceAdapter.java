package gift.member.adapter.persistence;

import gift.member.application.port.out.MemberPersistencePort;
import gift.member.domain.model.Member;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Repository
public class MemberPersistenceAdapter implements MemberPersistencePort {

    private static final RowMapper<Member> MEMBER_ROW_MAPPER =
            (rs, rowNum) -> new Member(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getObject("created_at", LocalDateTime.class)
            );
    private final JdbcClient jdbcClient;

    public MemberPersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Member save(Member member) {
        Long id = member.getId();

        if (id == null) {
            id = insertMember(member);
            return new Member(id, member.getEmail(), member.getPassword(), member.getCreatedAt());
        }

        updateMember(member);


        return new Member(id, member.getEmail(), member.getPassword(), member.getCreatedAt());
    }

    private Long insertMember(Member member) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(
                        """
                                    INSERT INTO MEMBER (email, password, created_at) VALUES (:email, :password, :createdAt)
                                """
                ).param("email", member.getEmail())
                .param("password", member.getPassword())
                .param("createdAt", member.getCreatedAt())
                .update(keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void updateMember(Member member) {
        jdbcClient.sql(
                        """
                                    UPDATE MEMBER
                                    SET email = :email,
                                        password = :password,
                                        created_at = :createdAt
                                    WHERE id = :id
                                """
                ).param("id", member.getId())
                .param("email", member.getEmail())
                .param("password", member.getPassword())
                .param("createdAt", member.getCreatedAt())
                .update();
    }

    public Optional<Member> findByEmail(String email) {

        return jdbcClient.sql(
                        """
                                    SELECT
                                        id, email, password, created_at
                                    FROM
                                        MEMBER
                                    WHERE
                                        email = :email
                                """
                ).param("email", email)
                .query(MEMBER_ROW_MAPPER).optional();
    }

    public boolean existsByEmail(String email) {

        return jdbcClient.sql(
                        """
                                    SELECT
                                        1
                                    FROM
                                        MEMBER
                                    WHERE
                                        email = :email
                                """
                ).param("email", email)
                .query(Integer.class)
                .optional()
                .isPresent();
    }

} 