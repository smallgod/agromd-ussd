/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.scheduler;

import com.agromarketday.ussd.logger.LoggerUtil;
import java.io.Serializable;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author smallgod
 */
public final class CustomSharedScheduler implements Serializable {

    private static final LoggerUtil logger = new LoggerUtil(CustomSharedScheduler.class);

    private static final long serialVersionUID = -7536502918237471101L;

    private Scheduler sharedScheduler;

    private CustomSharedScheduler() {
        buildScheduler();
    }

    private static class SharedSchedulerSingletonHolder {

        private static final CustomSharedScheduler INSTANCE = new CustomSharedScheduler();
    }

    public static CustomSharedScheduler getInstance() {
        return SharedSchedulerSingletonHolder.INSTANCE;
    }

    protected Object readResolve() {
        return getInstance();
    }

    public Scheduler getScheduler() {
        return this.sharedScheduler;
    }

    private void buildScheduler() {

        synchronized (this) {

            SchedulerFactory sf = new StdSchedulerFactory();

            try {

                if (sharedScheduler == null || sharedScheduler.isShutdown()) {
                    sharedScheduler = sf.getScheduler();
                }

            } catch (SchedulerException ex) {
                logger.error("Error creating scheduler factory: " + ex.getMessage());
            }
        }
    }

    /**
     * Halts the Scheduler's firing of Triggers, and cleans up all resources
     * associated with the Scheduler. Equivalent to shutdown(false). The
     * scheduler cannot be re-started.
     *
     * @throws SchedulerException
     */
    protected void destroyScheduler() throws SchedulerException {

        logger.debug("shutting down sheduler and unscheduling all job...");
        //unScheduleJob(); unschedule all jobs
        //unScheduleJob();
        if (sharedScheduler != null) {
            sharedScheduler.clear();
            sharedScheduler.shutdown();
            //schedulerFactory.shutdown(false);
            logger.debug("scheduler not null - shutting down");
        }
        this.sharedScheduler = null;
    }
}
