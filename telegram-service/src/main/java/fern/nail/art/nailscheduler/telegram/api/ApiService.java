package fern.nail.art.nailscheduler.telegram.api;

import org.telegram.telegrambots.meta.api.objects.ApiResponse;

public interface ApiService {
    ApiResponse<Object> callApiEndpoint(String endpoint, Object... params);
}