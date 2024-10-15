package fern.nail.art.nailscheduler.telegram.client;

import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateRequestDto;
import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateResponseDto;
import fern.nail.art.nailscheduler.telegram.mapper.WorkdayMapper;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.telegram.sequrity.JwtUtil;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WorkdayClient {
    private final WebClient webClient;
    private final WorkdayMapper workdayMapper;
    private final JwtUtil jwtUtil;

    public Set<WorkdayTemplate> getDefaultWorkdays() {
        String token = jwtUtil.getToken();

        return webClient.get()
                        .uri("/workdays/defaults")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()

                        .onStatus(HttpStatusCode::isError,
                                response -> Mono.error(new RuntimeException("Can't get templates")))
                        .bodyToFlux(WorkdayTemplateResponseDto.class)
                        .map(workdayMapper::toTemplate)
                        .collect(Collectors.toSet())
                        .block();
    }

    public Set<WorkdayTemplate> setDefaultWorkdays(WorkdayTemplateRequestDto requestDto) {
        String token = jwtUtil.getToken();

        return webClient.put()
                        .uri("/workdays/defaults")
                        .header("Authorization", "Bearer " + token)
                        .body(Mono.just(requestDto), WorkdayTemplateRequestDto.class)
                        .retrieve()

                        .onStatus(HttpStatusCode::isError,
                                response -> Mono.error(new RuntimeException("Can't put templates")))
                        .bodyToFlux(WorkdayTemplateResponseDto.class)
                        .map(workdayMapper::toTemplate)
                        .collect(Collectors.toSet())
                        .block();
    }
}
