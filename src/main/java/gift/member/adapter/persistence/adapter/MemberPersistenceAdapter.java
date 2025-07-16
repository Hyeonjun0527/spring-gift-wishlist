package gift.member.adapter.persistence.adapter;

import gift.member.application.port.out.MemberPersistencePort;
import gift.member.domain.model.Member;
import gift.member.domain.model.Role;
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
                    Role.valueOf(rs.getString("role")),
                    rs.getObject("created_at", LocalDateTime.class)
            );
    private final JdbcClient jdbcClient;

    public MemberPersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Member save(Member member) {
        Long id = member.id();

        if (id == null) {
            id = insertMember(member);
            return new Member(id, member.email(), member.password(), member.role(), member.createdAt());
        }

        updateMember(member);
        return new Member(id, member.email(), member.password(), member.role(), member.createdAt());
    }

    private Long insertMember(Member member) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(
                        """
                                INSERT INTO MEMBER (email, password, role, created_at) VALUES (:email, :password, :role, :createdAt)
                            """
                ).param("email", member.email())
                .param("password", member.password())
                .param("role", member.role().name())
                .param("createdAt", member.createdAt())
                .update(keyHolder);

        Number key = (Number) keyHolder.getKeys().get("ID");
        return Objects.requireNonNull(key).longValue();
    }

    private void updateMember(Member member) {
        jdbcClient.sql(
                        """
                                UPDATE MEMBER
                                SET email = :email,
                                    password = :password,
                                    role = :role,
                                    created_at = :createdAt
                                WHERE id = :id
                            """
                ).param("id", member.id())
                .param("email", member.email())
                .param("password", member.password())
                .param("role", member.role().name())
                .param("createdAt", member.createdAt())
                .update();
    }

    public Optional<Member> findByEmail(String email) {
        return jdbcClient.sql(
                        """
                                SELECT
                                    id, email, password, role, created_at
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

    public Optional<Member> findById(Long id) {
        return jdbcClient.sql(
                """
                    SELECT id, email, password, role, created_at
                    FROM MEMBER
                    WHERE id = :id
                """
        ).param("id", id)
        .query(MEMBER_ROW_MAPPER).optional();
    }

    public boolean existsById(Long id) {
        return jdbcClient.sql(
                """
                    SELECT 1 FROM MEMBER WHERE id = :id
                """
        ).param("id", id)
        .query(Integer.class)
        .optional()
        .isPresent();
    }

    public java.util.List<Member> findAll() {
        return jdbcClient.sql(
                """
                    SELECT id, email, password, role, created_at FROM MEMBER
                """
        ).query(MEMBER_ROW_MAPPER).list();
    }

    public void deleteById(Long id) {
        jdbcClient.sql(
                """
                    DELETE FROM MEMBER WHERE id = :id
                """
        ).param("id", id)
        .update();
    }
} 