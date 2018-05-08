package com.code.platform.task.util;

/**
 * Trigger state enum
 */
public enum TriggerStatusEnum {

    ACQUIRED("ACQUIRED") {
        public String getStatusCN() {
            return "运行中";
        }

        ;
    },
    PAUSED("PAUSED") {
        public String getStatusCN() {
            return "暂停中";
        }

        ;
    },
    WAITING("WAITING") {
        public String getStatusCN() {
            return "等待中";
        }

        ;
    };

    /**
     * @param stateEN
     */
    private TriggerStatusEnum(String statusEN) {
        this.statusEN = statusEN;
    }

    private String statusEN;

    /**
     * @return the stateEN
     */
    public String getStatusEN() {
        return statusEN;
    }

    /**
     * @param stateEN the stateEN to set
     */
    public void setStatusEN(String statusEN) {
        this.statusEN = statusEN;
    }

    /**
     * @return the stateCN
     */
    public abstract String getStatusCN();

}
