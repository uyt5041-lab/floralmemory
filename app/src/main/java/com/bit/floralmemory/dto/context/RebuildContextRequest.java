package com.bit.floralmemory.dto.context;

import com.bit.floralmemory.dto.forecast.ForecastRunRequest;
import java.time.LocalDate;

public class RebuildContextRequest {
    private ForecastRunRequest.Scope scope;
    private LocalDate fromMonth;
    private LocalDate toMonth;
    private Boolean force;

    public RebuildContextRequest() {
    }

    public RebuildContextRequest(ForecastRunRequest.Scope scope, LocalDate fromMonth, LocalDate toMonth,
            Boolean force) {
        this.scope = scope;
        this.fromMonth = fromMonth;
        this.toMonth = toMonth;
        this.force = force;
    }

    public ForecastRunRequest.Scope getScope() {
        return scope;
    }

    public void setScope(ForecastRunRequest.Scope scope) {
        this.scope = scope;
    }

    public LocalDate getFromMonth() {
        return fromMonth;
    }

    public void setFromMonth(LocalDate fromMonth) {
        this.fromMonth = fromMonth;
    }

    public LocalDate getToMonth() {
        return toMonth;
    }

    public void setToMonth(LocalDate toMonth) {
        this.toMonth = toMonth;
    }

    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }
}
