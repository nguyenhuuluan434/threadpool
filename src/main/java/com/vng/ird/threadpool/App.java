package com.vng.ird.threadpool;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Hello world!
 *
 */

@Configuration
@EnableScheduling
public class App {

	private AtomicLong couter = new AtomicLong();

	@Scheduled(fixedRate = 2000)
	private void fixedRateJob() {
		long jobId = couter.incrementAndGet();
		System.out.println("Job @ fix rate " + new Date() + " job " + jobId);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			// TODO: handle finally clause
			System.out.println("Job " + jobId + " done");
		}
	}

	@Bean
	public TaskScheduler poolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("poolScheduler");
		scheduler.setPoolSize(10);
		return scheduler;
	}

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			context.close();
		}
	}

	@Configuration
	static class RegisterTaskSchedulerViaSchedulingConfigurer implements SchedulingConfigurer {

		public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
			taskRegistrar.setTaskScheduler(poolScheduler());

			taskRegistrar.addFixedRateTask(new IntervalTask(new Runnable() {
				public void run() {
					System.out.println(
							"Job @ fixed rate " + new Date() + ", Thread name is " + Thread.currentThread().getName());
				}
			}, 1000, 0));
		}

		@Bean
		public TaskScheduler poolScheduler() {
			ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
			scheduler.setThreadNamePrefix("poolScheduler");
			scheduler.setPoolSize(10);
			return scheduler;
		}
	}
}
