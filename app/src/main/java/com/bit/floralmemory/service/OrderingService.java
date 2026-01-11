package com.bit.floralmemory.service;

import com.bit.floralmemory.dto.ordering.OrderingRunRequest;

public interface OrderingService {
    Long runOrdering(OrderingRunRequest req);
}
