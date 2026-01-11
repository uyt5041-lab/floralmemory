package com.bit.floralmemory.service;

import com.bit.floralmemory.dto.forecast.ForecastRunRequest;

public interface ForecastService {
    Long runForecast(ForecastRunRequest req);
}
