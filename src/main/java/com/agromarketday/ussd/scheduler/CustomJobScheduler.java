package com.agromarketday.ussd.scheduler;

import com.agromarketday.ussd.config.JobsConfig;
import com.agromarketday.ussd.connect.HttpClientPool;
import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.database.CustomHibernate;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.util.GeneralUtils;
import java.io.Serializable;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.JobListener;
import org.quartz.JobKey;
import org.quartz.JobDataMap;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerKey.triggerKey;

/**
 *
 * @author smallgod
 */
public final class CustomJobScheduler implements Serializable {

    private static final LoggerUtil logger = new LoggerUtil(CustomJobScheduler.class);
    private static final long serialVersionUID = 1L;

    private final Scheduler scheduler;
    private final CustomSharedScheduler customSharedScheduler;
    private final HttpClientPool clientPool;
    private final CustomHibernate internalDbAccess;

    /**
     *
     * @param clientPool
     * @param internalDbAccess
     */
    public CustomJobScheduler(HttpClientPool clientPool, CustomHibernate internalDbAccess) {

        this.customSharedScheduler = CustomSharedScheduler.getInstance();
        this.scheduler = customSharedScheduler.getScheduler();

        this.clientPool = clientPool;
        this.internalDbAccess = internalDbAccess;
    }

    /**
     * Schedule a JOB, start the JOB and add a JobListener that monitors for
     * events like when the job is finally executed. Before this job is
     * scheduled the method checks to see if this job was previously scheduled
     * and hence exists. If so, the previous will be deleted first before
     * re-scheduling it.
     *
     * @param jobsData
     * @param jobClass
     * @param jobListener
     * @param scheduleDelay Starts the Scheduler's threads that fire Triggers.
     * When a scheduler is first created it is in "stand-by" mode, and will not
     * fire triggers for this delay
     * @return the Trigger created for this Job
     */
    public Trigger scheduleARepeatJob(JobsConfig jobsData, Class<? extends Job> jobClass, JobListener jobListener, int scheduleDelay) {

        String triggerName = jobsData.getJobTriggerName();
        String jobName = jobsData.getJobName();
        String groupName = jobsData.getJobGroupName();
        int repeatInterval = jobsData.getRepeatInterval();

        Trigger triggerToFire = createRepeatTrigger(triggerName, groupName, repeatInterval);
        JobDetail jobTodo = prepareJob(jobName, groupName, jobsData, jobClass);

        //JobKey jobKey = jobTodo.getKey();
        try {

            deleteAJob(jobName, groupName);

            scheduler.scheduleJob(jobTodo, triggerToFire);
            scheduler.startDelayed(scheduleDelay);
            scheduler.getListenerManager().addJobListener(jobListener);

            logger.debug("Job Trigger: " + triggerName + ", successfuly added");

        } catch (SchedulerException ex) {
            logger.error("Error linking job to trigger and starting scheduler: " + ex.getMessage());
        }
        return triggerToFire;

    }

    /**
     * Schedule a JOB, start the JOB and add a JobListener that monitors for
     * events like when the job is finally executed. Before this job is
     * scheduled the method checks to see if this job was previously scheduled
     * and hence exists. If so, the previous will be deleted first before
     * re-scheduling it.
     *
     * This job also has the ability to interact with another job - the
     * secondJob
     *
     * @param thisJobsData
     * @param secondJobsData
     * @param jobClass
     * @param jobListener
     * @param scheduleDelay Starts the Scheduler's threads that fire Triggers.
     * When a scheduler is first created it is in "stand-by" mode, and will not
     * fire triggers for this delay
     * @return the Trigger created for this Job
     */
    public Trigger scheduleARepeatJob(JobsConfig thisJobsData, JobsConfig secondJobsData, Class<? extends Job> jobClass, JobListener jobListener, int scheduleDelay) {

        String triggerName = thisJobsData.getJobTriggerName();
        String thisJobName = thisJobsData.getJobName();
        String groupName = thisJobsData.getJobGroupName();
        int repeatInterval = thisJobsData.getRepeatInterval();

        Trigger triggerToFire = createRepeatTrigger(triggerName, groupName, repeatInterval);
        JobDetail jobTodo = prepareJob(thisJobsData, secondJobsData, groupName, jobClass);

        //JobKey jobKey = jobTodo.getKey();
        try {

            deleteAJob(thisJobName, groupName);

            scheduler.scheduleJob(jobTodo, triggerToFire);
            scheduler.startDelayed(scheduleDelay);
            scheduler.getListenerManager().addJobListener(jobListener);

        } catch (SchedulerException ex) {
            logger.error("Error linking job to trigger and starting scheduler: " + ex.getMessage());
        }
        return triggerToFire;

    }

    /**
     * Schedule a JOB, start the JOB and add a JobListener that monitors for
     * events like when the job is finally executed. Before this job is
     * scheduled the method checks to see if this job was previously scheduled
     * and hence exists. If so, the previous will be deleted first before
     * re-scheduling it.
     *
     * This job also has the ability to interact with 2 other jobs - the
     * secondJob & thirdJob
     *
     * @param thisJobsData
     * @param secondJobsData
     * @param thirdJobsData
     * @param jobClass
     * @param jobListener
     * @param scheduleDelay Starts the Scheduler's threads that fire Triggers.
     * When a scheduler is first created it is in "stand-by" mode, and will not
     * fire triggers for this delay
     * @return the Trigger created for this Job
     */
    public Trigger scheduleARepeatJob(JobsConfig thisJobsData, JobsConfig secondJobsData, JobsConfig thirdJobsData, Class<? extends Job> jobClass, JobListener jobListener, int scheduleDelay) {

        String triggerName = thisJobsData.getJobTriggerName();
        String thisJobName = thisJobsData.getJobName();
        String groupName = thisJobsData.getJobGroupName();
        int repeatInterval = thisJobsData.getRepeatInterval();

        Trigger triggerToFire = createRepeatTrigger(triggerName, groupName, repeatInterval);
        JobDetail jobTodo = prepareJob(thisJobsData, secondJobsData, thirdJobsData, groupName, jobClass);

        //JobKey jobKey = jobTodo.getKey();
        try {

            deleteAJob(thisJobName, groupName);

            scheduler.scheduleJob(jobTodo, triggerToFire);
            scheduler.startDelayed(scheduleDelay);
            scheduler.getListenerManager().addJobListener(jobListener);

        } catch (SchedulerException ex) {
            logger.error("Error linking job to trigger and starting scheduler: " + ex.getMessage());
        }
        return triggerToFire;

    }

    /**
     *
     * @param jobsData
     * @param jobClass
     * @param jobListener
     * @param scheduleDelay
     * @return
     */
    public Trigger scheduleAOneTimeJob(JobsConfig jobsData, Class<? extends Job> jobClass, JobListener jobListener, int scheduleDelay) {

        String triggerName = jobsData.getJobTriggerName();
        String jobName = jobsData.getJobName();
        String groupName = jobsData.getJobGroupName();

        Trigger triggerToFire = createNonRepeatTrigger(triggerName, groupName);
        JobDetail jobTodo = prepareJob(jobName, groupName, jobsData, jobClass);

        //JobKey jobKey = jobTodo.getKey();
        try {

            deleteAJob(jobName, groupName);

            scheduler.scheduleJob(jobTodo, triggerToFire);
            scheduler.startDelayed(scheduleDelay);
            scheduler.getListenerManager().addJobListener(jobListener);

            logger.debug("Job Trigger: " + triggerName + ", successfuly added");

        } catch (SchedulerException ex) {
            logger.error("Error linking job to trigger and starting scheduler: " + ex.getMessage());
        }
        return triggerToFire;

    }

    /**
     * Pause a given job
     *
     * @param jobName
     * @param groupName
     * @return
     */
    public boolean pauseAJob(String jobName, String groupName) {

        JobKey jobKey = getJobKey(jobName, groupName);

        boolean isJobPaused = Boolean.TRUE;

        try {

            scheduler.pauseJob(jobKey);

        } catch (SchedulerException ex) {
            logger.error("Error pausing a job: " + ex.getMessage());
            isJobPaused = Boolean.FALSE;
        }
        return isJobPaused;

    }

    public boolean resumeAJob(String jobName, String groupName) {

        JobKey jobKey = getJobKey(jobName, groupName);

        boolean isJobResumed = Boolean.TRUE;

        try {

            scheduler.resumeJob(jobKey);

        } catch (SchedulerException ex) {
            logger.error("Error resuming a job: " + ex.getMessage());
            isJobResumed = Boolean.FALSE;
        }
        return isJobResumed;

    }

    /**
     * Check if this job is paused or running
     *
     * @param triggerName
     * @return
     */
    public boolean isJobTriggerPaused(String triggerName) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName);
        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);

        boolean isPaused = Boolean.FALSE;

        switch (triggerState) {

            case PAUSED:
                isPaused = Boolean.TRUE;
                break;

            case NORMAL:
                isPaused = Boolean.FALSE;
                break;

            case BLOCKED:
                break;

            case COMPLETE:
                break;

            case ERROR:
                break;

            case NONE:
                break;

            default:
                throw new SchedulerException("Trigger is neither in PAUSED nor NORMAL state: " + triggerState.toString());
        }

        return isPaused;
    }

    /**
     * Delete a job using the given thisJobName and the groupName for this job
     *
     * @param jobName
     * @param groupName
     * @return
     */
    public boolean deleteAJob(String jobName, String groupName) {

        JobKey jobKey = getJobKey(jobName, groupName);

        boolean isJobDeleted = Boolean.TRUE;

        try {

            //scheduler.deleteJob(new JobKey("Job1", "Group1"));
            //check first if this job exits in the scheduler - unschedule the trigger/and job if necessary/redundant
            if (scheduler.checkExists(jobKey)) {
                scheduler.interrupt(jobKey); //interrupt job if running so that we are able to delete it

                //loop through all the triggers having a reference to this job, to unschedule them removes the job from the jobstore
                isJobDeleted = scheduler.deleteJob(jobKey);
                logger.debug("jobKey: " + jobKey + " found and deleted: " + isJobDeleted + ", so that it can be rescheduled");
            }

        } catch (SchedulerException ex) {
            logger.error("Error deleting a job: " + ex.getMessage());
        }
        return isJobDeleted;

    }

    public void scheduleAOneTimeJob(String triggerName, String jobName, String groupName) {

    }

//    private void setupScheduler() {
//
//        Trigger trigger = createTrigger(props.getSchedulers().getRetryfailed().getTriggername().trim());
//        JobDetail job = createJob(props.getSchedulers().getRetryfailed().getJobname().trim());
//        scheduleJob(trigger, job);
//    }
    private SimpleTrigger createRepeatTrigger(String triggerName, String groupName, int repeatInterval) {

        SimpleTrigger trigger = newTrigger()
                .withIdentity(triggerKey(triggerName, groupName))
                //.startNow()
                //.startAt(futureDate(5, TimeUnit.SECONDS))
                .startAt(DateBuilder.futureDate(repeatInterval, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(simpleSchedule()
                        .withMisfireHandlingInstructionNextWithRemainingCount()
                        .withIntervalInSeconds(repeatInterval)
                        .repeatForever())
                .build();

        return trigger;
    }

    private SimpleTrigger createNonRepeatTrigger(String triggerName, String groupName) {

        SimpleTrigger trigger = newTrigger()
                .withIdentity(triggerKey(triggerName, groupName))
                .startNow()
                .withSchedule(simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();

        return trigger;
    }

    /**
     * uses the given thisJobName and job groupname to retrieve jobkey
     *
     * @param jobName
     * @param groupName
     * @return
     */
    private JobKey getJobKey(String jobName, String groupName) {
        return JobKey.jobKey(jobName, groupName);
    }

    /**
     * Uses the given thisJobName and default job groupname to retrieve jobkey
     *
     * @param jobName
     * @return jobkey
     */
    private JobKey getJobKey(String jobName) {
        return JobKey.jobKey(jobName);
    }

    private JobDetail prepareJob(String jobName, String jobGroupName, JobsConfig jobsData, Class<? extends Job> jobToExecute) { //add param for jobdatamap if needed

        JobKey jobKey = getJobKey(jobName, jobGroupName);
        JobDataMap jobData = createJobDataMap(jobName, jobsData);

        JobDetail job = newJob(jobToExecute)
                .withIdentity(jobKey)
                .setJobData(jobData)
                //.usingJobData("jobSays", "Hello World!")
                //.usingJobData("myFloatValue", 3.141f)
                .storeDurably(Boolean.FALSE) //deleted automatically when there are no longer active trigger associated to it
                .requestRecovery()
                .build();

        return job;

    }

    /**
     * Used by this job that might need to interact with another job
     *
     * @param thisJobName
     * @param thisJobData
     * @param secondJobName
     * @param secondJobData
     * @param thisJobGroupName
     * @param jobToExecute
     * @return
     */
    private JobDetail prepareJob(JobsConfig thisJobData, JobsConfig secondJobData, String thisJobGroupName, Class<? extends Job> jobToExecute) { //add param for jobdatamap if needed

        JobKey jobKey = getJobKey(thisJobData.getJobName(), thisJobGroupName);
        JobDataMap jobData = createJobDataMap(thisJobData, secondJobData);

        JobDetail job = newJob(jobToExecute)
                .withIdentity(jobKey)
                .setJobData(jobData)
                //.usingJobData("jobSays", "Hello World!")
                //.usingJobData("myFloatValue", 3.141f)
                .storeDurably(Boolean.FALSE) //deleted automatically when there are no longer active trigger associated to it
                .requestRecovery()
                .build();

        return job;

    }

    /**
     * Used by this job that might need to interact with another job
     *
     * @param thisJobName
     * @param thisJobData
     * @param secondJobName
     * @param secondJobData
     * @param thisJobGroupName
     * @param jobToExecute
     * @return
     */
    private JobDetail prepareJob(JobsConfig thisJobData, JobsConfig secondJobData, JobsConfig thirdJobData, String thisJobGroupName, Class<? extends Job> jobToExecute) { //add param for jobdatamap if needed

        JobKey jobKey = getJobKey(thisJobData.getJobName(), thisJobGroupName);
        JobDataMap jobData = createJobDataMap(thisJobData, secondJobData, thirdJobData);

        JobDetail job = newJob(jobToExecute)
                .withIdentity(jobKey)
                .setJobData(jobData)
                //.usingJobData("jobSays", "Hello World!")
                //.usingJobData("myFloatValue", 3.141f)
                .storeDurably(Boolean.FALSE) //deleted automatically when there are no longer active trigger associated to it
                .requestRecovery()
                .build();

        return job;

    }

    /**
     * Create a job datamap including 1 job
     *
     * @param jobName
     * @param data
     * @return
     */
    public JobDataMap createJobDataMap(String jobName, JobsConfig data) {

        JobDataMap dataMap = new JobDataMap();

        dataMap.put(jobName, data);
        dataMap.put(NamedConstants.CLIENT_POOL, clientPool);
        dataMap.put(NamedConstants.INTERNAL_DB_ACCESS, internalDbAccess);

        return dataMap;
    }

    /**
     * Create a job datamap including 2 separate jobs
     *
     * @param thisJobData
     * @param secondJobData
     * @return
     */
    public JobDataMap createJobDataMap(JobsConfig thisJobData, JobsConfig secondJobData) {

        JobDataMap dataMap = new JobDataMap();

        dataMap.put(thisJobData.getJobName(), thisJobData);
        dataMap.put(NamedConstants.SECOND_JOBSDATA, secondJobData);
        dataMap.put(NamedConstants.CLIENT_POOL, clientPool);
        dataMap.put(NamedConstants.INTERNAL_DB_ACCESS, internalDbAccess);

        return dataMap;
    }

    /**
     * Create a job datamap including 3 separate jobs
     *
     * @param thisJobData
     * @param secondJobData
     * @param thirdJobData
     * @return
     */
    public JobDataMap createJobDataMap(JobsConfig thisJobData, JobsConfig secondJobData, JobsConfig thirdJobData) {

        JobDataMap dataMap = new JobDataMap();

        dataMap.put(thisJobData.getJobName(), thisJobData);
        dataMap.put(NamedConstants.SECOND_JOBSDATA, secondJobData);
        dataMap.put(NamedConstants.THIRD_JOBSDATA, thirdJobData);
        dataMap.put(NamedConstants.CLIENT_POOL, clientPool);
        dataMap.put(NamedConstants.INTERNAL_DB_ACCESS, internalDbAccess);

        return dataMap;
    }

    private void scheduleJob(Trigger triggerToFire, JobDetail jobTodo, JobListener jobListener) {

        try {

            //check first if this job exits in the scheduler
            //unschedule the trigger/and job if necessary/redundant
            logger.debug("about to check if JOB with jobkey exists");
            if (scheduler.checkExists(jobTodo.getKey())) {
                //scheduler.interrupt(jobKey)
                boolean deleted = scheduler.deleteJob(jobTodo.getKey());
                logger.debug("jobKey: " + jobTodo.getKey() + " found and deleted: " + deleted + ", so that it can be rescheduled");
            }

            scheduler.scheduleJob(jobTodo, triggerToFire);
            scheduler.start();
            scheduler.getListenerManager().addJobListener(jobListener);
            logger.debug("trigger successfuly added");
        } catch (SchedulerException ex) {
            logger.error("error linking job to trigger and starting scheduler: " + ex.getMessage());
        }
    }

    /**
     * Method will remove this trigger from the given Job, but other triggers if
     * any associated if not removed will still fire this job use deleteJob() to
     * completely remove the entire job with all its associated triggers
     *
     * @param trigger returned when the job to be canceled was scheduled
     */
    public void deleteATrigger(Trigger trigger) {

        TriggerKey triggerKey = trigger.getKey();

        //unschedule the trigger/and job if necessary/redundant
        //TriggerKey keyTrigger = triggerKey(String.valueOf(triggerName, Scheduler.DEFAULT_GROUP);
        try {
            boolean unscheduled = scheduler.unscheduleJob(triggerKey);
            logger.debug("triggerName: " + triggerKey.getName() + " - unscheduled : " + unscheduled);
        } catch (SchedulerException ex) {
            logger.error("error trying to unschedule trigger: " + ex.getMessage());
        }
    }

    /**
     * Method will remove this trigger from the given Job, but other triggers if
     * any associated if not removed will still fire this job use deleteJob() to
     * completely remove the entire job with all its associated triggers
     *
     * @param triggerName of the trigger to be removed from the job
     */
    public void deleteATrigger(String triggerName) {

        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName);

        //TriggerKey triggerKey = trigger.getKey();
        //unschedule the trigger/and job if necessary/redundant
        //TriggerKey keyTrigger = triggerKey(String.valueOf(triggerName, Scheduler.DEFAULT_GROUP);
        try {
            boolean unscheduled = scheduler.unscheduleJob(triggerKey);
            logger.debug("triggerName: " + triggerKey.getName() + " - unscheduled : " + unscheduled);
        } catch (SchedulerException ex) {
            logger.error("error trying to unschedule trigger: " + ex.getMessage());
        }
    }

    /**
     * Trigger an existing job to fire now
     *
     * @param jobName
     * @param groupName
     * @return
     * @throws MyCustomException
     */
    public boolean triggerJobNow(String jobName, String groupName) throws MyCustomException {

        JobKey jobKey = getJobKey(jobName, groupName);

        boolean isJobTriggered = Boolean.FALSE;

        try {

            if (scheduler.checkExists(jobKey)) {

                scheduler.triggerJob(jobKey);
                isJobTriggered = Boolean.TRUE;
            }

        } catch (SchedulerException ex) {
            logger.error("Error triggering a job: " + ex.getMessage());
            String errorDetails = "SchedulerException occurred trying to trigger job: " + ex.toString();

            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }
        return isJobTriggered;

    }

    /**
     * Trigger an existing job to fire now
     *
     * @param jobName
     * @param groupName
     * @param jobsDataMap
     * @return
     * @throws MyCustomException
     */
    public boolean triggerJobNow(String jobName, String groupName, JobDataMap jobsDataMap) throws MyCustomException {

        JobKey jobKey = getJobKey(jobName, groupName);

        boolean isJobTriggered = Boolean.FALSE;

        try {

            if (scheduler.checkExists(jobKey)) {

                scheduler.triggerJob(jobKey, jobsDataMap);
                isJobTriggered = Boolean.TRUE;
            }

        } catch (SchedulerException ex) {
            logger.error("Error triggering a job: " + ex.getMessage());
            String errorDetails = "SchedulerException occurred trying to trigger job: " + ex.toString();

            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.PROCESSING_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }
        return isJobTriggered;

    }

    /**
     * Destroy all scheduled jobs, timers, triggers, etc as well as shutdown the
     * shared scheduler Call this method when shutting down the
     * application/daemon
     *
     * @throws SchedulerException
     */
    public void cancelAllJobs() throws SchedulerException {
        customSharedScheduler.destroyScheduler();
    }
}
