package com.bit.floralmemory.dto.context;

import com.bit.floralmemory.dto.forecast.ForecastRunRequest;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RebuildContextRequest {
    private ForecastRunRequest.Scope scope;
    private LocalDate fromMonth;
    private LocalDate toMonth;
}
