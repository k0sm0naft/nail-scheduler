package fern.nail.art.nailscheduler.telegram.sequrity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey secret;
    @Getter
    private String token;

    private JwtUtil(@Value("${jwt.secret}") String secretString) {
        this.secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    @Scheduled(initialDelayString = "PT1S", fixedRateString = "PT59M")
    protected void generateToken() {
        token = Jwts.builder()
                    .claim("roles", List.of("ROLE_MASTER"))
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                    .signWith(secret)
                    .compact();
    }

    public Long getUserId(String token) {
        return Jwts.parser()
                   .verifyWith(secret)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .get("id", Long.class);
    }
}
