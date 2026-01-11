package com.bit.floralmemory.service;

import com.bit.floralmemory.dto.context.RebuildContextRequest;
import com.bit.floralmemory.dto.context.UpsertEventsRequest;

public interface ContextService {
    int upsertEvents(UpsertEventsRequest req);

    int rebuildContext(RebuildContextRequest req);
}
