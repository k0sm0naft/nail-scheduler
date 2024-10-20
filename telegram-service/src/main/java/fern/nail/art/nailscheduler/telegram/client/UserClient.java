package fern.nail.art.nailscheduler.telegram.client;

import fern.nail.art.nailscheduler.telegram.dto.ErrorDto;
import fern.nail.art.nailscheduler.telegram.dto.LoginResponseDto;
import fern.nail.art.nailscheduler.telegram.dto.UserTelegramDto;
import fern.nail.art.nailscheduler.telegram.mapper.UserMapper;
import fern.nail.art.nailscheduler.telegram.model.AuthUser;
import fern.nail.art.nailscheduler.telegram.model.RegistrationResult;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.sequrity.JwtUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserClient {
    private final WebClient webClient;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public Optional<User> findUserByTelegramId(Long telegramId) {
        String token = jwtUtil.getToken();

        return webClient.get()
                        .uri("/users/telegram/{id}", telegramId)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, response -> {
                            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.empty();
                            }
                            return getThrowableMono(response);
                        })
                        .bodyToMono(UserTelegramDto.class)
                        .flatMap(dto -> Mono.justOrEmpty(userMapper.dtoToUser(dto)))
                        .blockOptional();
    }

    public Optional<Long> findUserId(AuthUser user) {
        ResponseEntity<LoginResponseDto> response =
                webClient.post()
                         .uri("/auth/login")
                         .body(Mono.just(user), AuthUser.class)
                         .retrieve()
                         .toEntity(LoginResponseDto.class)
                         .onErrorReturn(ResponseEntity.badRequest().build())
                         .block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            LoginResponseDto body = response.getBody();
            if (body != null) {
                String token = body.token();
                return Optional.ofNullable(jwtUtil.getUserId(token));
            }
        }

        return Optional.empty();
    }

    public void setTelegramId(AuthUser user) {
        String token = jwtUtil.getToken();

        webClient.patch()
                 .uri(uriBuilder -> uriBuilder
                         .path("/users/{id}/telegram")
                         .queryParam("telegramId", user.getTelegramId())
                         .build(user.getUserId()))
                 .header("Authorization", "Bearer " + token)
                 .retrieve()

                 .onStatus(HttpStatusCode::isError, this::getThrowableMono)
                 .bodyToMono(UserTelegramDto.class)
                 .flatMap(dto -> Mono.justOrEmpty(user))
                 .block();
    }

    public RegistrationResult registerUser(AuthUser user) {
        return webClient.post()
                        .uri("/auth/registration")
                        .body(Mono.just(user), AuthUser.class)
                        .retrieve()

                        .onStatus(HttpStatusCode::isError, this::getThrowableMono)
                        .bodyToMono(UserTelegramDto.class)
                        .map(dto -> new RegistrationResult(dto.id(), null))
                        .onErrorResume(e -> Mono.just(new RegistrationResult(null, e.getMessage())))
                        .block();
    }

    private Mono<Throwable> getThrowableMono(ClientResponse response) {
        return response.bodyToMono(ErrorDto.class)
                       .flatMap(errorResponse ->
                               Mono.error(new RuntimeException(getErrorMessage(errorResponse))));
    }

    private String getErrorMessage(ErrorDto errorResponse) {
        Object error = errorResponse.error();
        if (error instanceof List<?> list) {
            return list.stream()
                       .filter(item -> item instanceof String)
                       .map(item -> (String) item)
                       .collect(Collectors.joining("\n"));
        } else if (error instanceof String errorMessage) {
            return errorMessage;
        }
        return "Unknown error";
    }
}
