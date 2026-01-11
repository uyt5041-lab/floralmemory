package com.bit.floralmemory.dto.ordering;

import java.util.List;
import java.util.Map;

public class OrderingRunRequest {
    private String runId;
    private Policy policy;

    public OrderingRunRequest() {
    }

    public OrderingRunRequest(String runId, Policy policy) {
        this.runId = runId;
        this.policy = policy;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public static class Policy {
        private java.util.Map<String, Object> serviceLevel;
        private java.util.Map<String, Object> safetyRules;
        private java.util.Map<String, Object> inventory;

        public Policy() {
        }

        public Policy(java.util.Map<String, Object> serviceLevel, java.util.Map<String, Object> safetyRules,
                java.util.Map<String, Object> inventory) {
            this.serviceLevel = serviceLevel;
            this.safetyRules = safetyRules;
            this.inventory = inventory;
        }

        public java.util.Map<String, Object> getServiceLevel() {
            return serviceLevel;
        }

        public void setServiceLevel(java.util.Map<String, Object> serviceLevel) {
            this.serviceLevel = serviceLevel;
        }

        public java.util.Map<String, Object> getSafetyRules() {
            return safetyRules;
        }

        public void setSafetyRules(java.util.Map<String, Object> safetyRules) {
            this.safetyRules = safetyRules;
        }

        public java.util.Map<String, Object> getInventory() {
            return inventory;
        }

        public void setInventory(java.util.Map<String, Object> inventory) {
            this.inventory = inventory;
        }
    }
}
