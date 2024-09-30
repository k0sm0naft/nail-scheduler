package fern.nail.art.nailscheduler.telegram.client;

import fern.nail.art.nailscheduler.telegram.dto.ErrorDto;
import fern.nail.art.nailscheduler.telegram.dto.LoginResponseDto;
import fern.nail.art.nailscheduler.telegram.dto.UserTelegramDto;
import fern.nail.art.nailscheduler.telegram.mapper.UserMapper;
import fern.nail.art.nailscheduler.telegram.model.LoginUser;
import fern.nail.art.nailscheduler.telegram.model.RegisterUser;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.sequrity.JwtUtil;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserClient {
    private final MessageService messageService;
    private final WebClient webClient;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    //todo notify about errors (get localized messages)
    public Optional<User> findUserByTelegramId(Long telegramId) {
        String token = jwtUtil.getToken();

        return webClient.get()
                        .uri("/users/telegram/{id}", telegramId)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, response -> {
                            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.empty();
                            }
                            return Mono.error(new RuntimeException("Client error"));
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, response ->
                                Mono.error(new RuntimeException("Server error")))
                        .bodyToMono(UserTelegramDto.class)
                        .flatMap(dto -> Mono.justOrEmpty(userMapper.dtoToUser(dto)))
                        .blockOptional();
    }

    public Optional<Long> findUserId(LoginUser user) {
        ResponseEntity<LoginResponseDto> response =
                webClient.post()
                         .uri("/auth/login")
                         .body(Mono.just(user), LoginUser.class)
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

    public void setTelegramId(LoginUser user) {
        String token = jwtUtil.getToken();

        webClient.patch()
                 .uri(uriBuilder -> uriBuilder
                         .path("/users/{id}/telegram")
                         .queryParam("telegramId", user.getTelegramId())
                         .build(user.getUserId()))
                 .header("Authorization", "Bearer " + token)
                 .retrieve()
                 .onStatus(HttpStatusCode::is4xxClientError, response ->
                         Mono.error(new RuntimeException("Client error")))
                 .onStatus(HttpStatusCode::is5xxServerError, response ->
                         Mono.error(new RuntimeException("Server error")))
                 .bodyToMono(UserTelegramDto.class)
                 .flatMap(dto -> Mono.justOrEmpty(user))
                 .block();
    }

    public Optional<Long> registerUser(RegisterUser user) {
        return webClient.post()
                        .uri("/auth/registration")
                        .body(Mono.just(user), RegisterUser.class)
                        .retrieve()

                        .onStatus(HttpStatusCode::is4xxClientError, response ->
                                response.bodyToMono(ErrorDto.class)
                                        .flatMap(errorResponse -> {
                                            Object error = errorResponse.error();
                                            if (error instanceof List) {
                                                List<String> errors = (List<String>) error;
                                                errors.forEach(
                                                        e -> messageService.sendText(user, e));
                                            } else if (error instanceof String singleError) {
                                                messageService.sendText(user, singleError);
                                            }
                                            return Mono.empty();
                                        })
                        )
                        .onStatus(HttpStatusCode::is5xxServerError, response ->
                                Mono.error(new RuntimeException("Server error")))
                        .bodyToMono(UserTelegramDto.class)
                        .flatMap(dto -> Mono.justOrEmpty(dto.id()))
                        .blockOptional();
    }
}
