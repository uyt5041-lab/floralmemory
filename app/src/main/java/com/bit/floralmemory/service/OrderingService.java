package com.bit.floralmemory.service;

import com.bit.floralmemory.dto.ordering.OrderingRunRequest;
import com.bit.floralmemory.dto.ordering.PolicySweepRequest;
import com.bit.floralmemory.dto.ordering.PolicySweepResponse;

public interface OrderingService {
    Long runOrdering(OrderingRunRequest req);

    PolicySweepResponse runPolicySweep(PolicySweepRequest req);
}
