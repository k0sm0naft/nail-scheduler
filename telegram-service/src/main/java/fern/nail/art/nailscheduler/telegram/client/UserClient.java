package fern.nail.art.nailscheduler.telegram.client;

import fern.nail.art.nailscheduler.telegram.dto.UserTelegramRequestDto;
import fern.nail.art.nailscheduler.telegram.mapper.UserMapper;
import fern.nail.art.nailscheduler.telegram.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserClient {
    private final WebClient webClient;
    private final UserMapper userMapper;

    public Optional<User> findUserByTelegramId(Long telegramId) {
        return webClient.get()
                        .uri("http://localhost:8080/api/users/telegram/{id}", telegramId)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(
                                new RuntimeException("User not found")))
                        .onStatus(HttpStatusCode::is5xxServerError,
                                clientResponse -> Mono.error(new RuntimeException("Server error")))
                        .bodyToMono(UserTelegramRequestDto.class)
                        .map(userMapper::dtoToUser)
                        .block();
    }
}
