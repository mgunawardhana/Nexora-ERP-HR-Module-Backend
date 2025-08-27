//package com.nexora.backend.authentication.service.impl;
//
//import com.nexora.backend.domain.entity.Token;
//import com.nexora.backend.domain.enums.TokenType;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Service
//public class TokenService {
//    @NonNull
//    private final JdbcTemplate readJdbcTemplate;
//
//    public TokenService(@NonNull JdbcTemplate readJdbcTemplate) {
//        this.readJdbcTemplate = readJdbcTemplate;
//    }
//
//    public List<Token> findAllValidTokenByUser(Integer id) {
//        String sql = """
//                SELECT id, token, token_type, revoked, expired, user_id
//                FROM token
//                WHERE user_id = ? AND expired = false AND revoked = false
//                """;
//
//        return readJdbcTemplate.query(sql, this::mapTokenRow, id);
//    }
//
//    public Optional<Token> findByToken(String token) {
//        String sql = """
//                SELECT token.id, token.token, token.token_type, token.revoked, token.expired, token.user_id FROM token
//                 WHERE token.token = ?
//                """;
//
//        try {
//            Token tokenObj = readJdbcTemplate.queryForObject(sql, this::mapTokenRow, token);
//            return Optional.ofNullable(tokenObj);
//        } catch (EmptyResultDataAccessException e) {
//            log.debug("Token not found: {}", token);
//            return Optional.empty();
//        }
//    }
//
//    public boolean isTokenValid(String token) {
//        return findByToken(token).map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);
//    }
//
//    private Token mapTokenRow(ResultSet rs, int rowNum) throws SQLException {
//        return Token.builder().id(rs.getInt("id")).token(rs.getString("token")).tokenType(TokenType.valueOf(rs.getString("token_type"))).revoked(rs.getBoolean("revoked")).expired(rs.getBoolean("expired")).user_Id(rs.getInt("user_id")).build();
//    }
//}
