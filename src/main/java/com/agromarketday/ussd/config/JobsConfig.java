package com.agromarketday.ussd.config;

/**
 * We will use this Jobs COnfig class for all Jobs, not only the ad Fetcher Job
 * Tweek, add a constructor if necessary to suit other jobs
 *
 * @author smallgod
 */
public class JobsConfig {

    private final String jobTriggerName;
    private final String jobName;
    private final String jobGroupName;
    private final int repeatInterval;

    private final RemoteUnitConfig remoteUnitConfig;

    /**
     *
     * @param jobTriggerName
     * @param jobName
     * @param jobGroupName
     * @param repeatInterval
     */
    public JobsConfig(String jobTriggerName, String jobName, String jobGroupName, int repeatInterval) {

        this(jobTriggerName, jobName, jobGroupName, repeatInterval, null);
    }

    /**
     *
     * @param jobTriggerName
     * @param jobName
     * @param jobGroupName
     * @param repeatInterval
     * @param remoteUnitConfig
     */
    public JobsConfig(String jobTriggerName, String jobName, String jobGroupName, int repeatInterval, RemoteUnitConfig remoteUnitConfig) {

        this.jobTriggerName = jobTriggerName;
        this.jobName = jobName;
        this.jobGroupName = jobGroupName;
        this.repeatInterval = repeatInterval;

        this.remoteUnitConfig = remoteUnitConfig;
    }

    public String getJobTriggerName() {
        return jobTriggerName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public RemoteUnitConfig getRemoteUnitConfig() {
        return remoteUnitConfig;
    }

}
