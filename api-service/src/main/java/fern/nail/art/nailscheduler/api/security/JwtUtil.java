package fern.nail.art.nailscheduler.api.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fern.nail.art.nailscheduler.api.model.Role;
import fern.nail.art.nailscheduler.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    private JwtUtil(@Value("${jwt.secret}") String secretString) {
        this.secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                                 .map(Role::getAuthority)
                                 .toList();
        return Jwts.builder()
                   .subject(user.getUsername())
                   .claim("id", user.getId())
                   .claim("roles", roles)
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + expiration))
                   .signWith(secret)
                   .compact();
    }

    public boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        final Jws<Claims> claims = getParser().parseSignedClaims(token);
        return claims.getPayload().getExpiration().after(new Date());
    }

    public User getUser(String token) {
        final Claims claims = getParser().parseSignedClaims(token).getPayload();
        User user = new User();
        user.setId(claims.get("id", Long.class));
        user.setUsername(claims.getSubject());

        ObjectMapper mapper = new ObjectMapper();
        List<String> rolesAsStrings =
                mapper.convertValue(claims.get("roles"), new TypeReference<>() {});
        Set<Role> roles = rolesAsStrings.stream()
                                        .map(roleName -> new Role(Role.RoleName.valueOf(roleName)))
                                        .collect(Collectors.toSet());
        user.setRoles(roles);
        return user;
    }

    private JwtParser getParser() {
        return Jwts.parser().verifyWith(secret).build();
    }
}
