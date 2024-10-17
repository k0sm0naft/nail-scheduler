package fern.nail.art.nailscheduler.telegram.client;

import fern.nail.art.nailscheduler.telegram.dto.WorkdayDto;
import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateRequestDto;
import fern.nail.art.nailscheduler.telegram.dto.WorkdayTemplateResponseDto;
import fern.nail.art.nailscheduler.telegram.mapper.WorkdayMapper;
import fern.nail.art.nailscheduler.telegram.model.PeriodType;
import fern.nail.art.nailscheduler.telegram.model.Workday;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.telegram.sequrity.JwtUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

    public List<Workday> getWorkdayByPeriod(PeriodType periodType, int offset) {
        String token = jwtUtil.getToken();

        return webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/workdays")
                                .queryParam("period", periodType.name())
                                .queryParam("offset", offset)
                                .build())
                        .header("Authorization", "Bearer " + token)
                        .retrieve()

                        .onStatus(HttpStatusCode::isError,
                                response -> Mono.error(new RuntimeException("Can't get workdays")))
                        .bodyToFlux(WorkdayDto.class)
                        .map(workdayMapper::toWorkday)
                        .collect(Collectors.toList())
                        .block();
    }

    public boolean setDefaultWorkday(LocalDate date) {
        String token = jwtUtil.getToken();

        Boolean result = webClient.delete()
                                  .uri("/workdays/{date}", date)
                                  .header("Authorization", "Bearer " + token)
                                  .retrieve()

                                  .toBodilessEntity()
                                  .map(ResponseEntity::getStatusCode)
                                  .map(HttpStatusCode::is2xxSuccessful)
                                  .onErrorReturn(false)
                                  .block();
        return Boolean.TRUE.equals(result);
    }

    public boolean setWorkday(WorkdayDto dto) {String token = jwtUtil.getToken();

        Boolean result = webClient.post()
                                  .uri("/workdays")
                                  .header("Authorization", "Bearer " + token)
                                  .body(Mono.just(dto), WorkdayDto.class)
                                  .retrieve()

                                  .toBodilessEntity()
                                  .map(ResponseEntity::getStatusCode)
                                  .map(HttpStatusCode::is2xxSuccessful)
                                  .onErrorReturn(false)
                                  .block();
        return Boolean.TRUE.equals(result);
    }
}
