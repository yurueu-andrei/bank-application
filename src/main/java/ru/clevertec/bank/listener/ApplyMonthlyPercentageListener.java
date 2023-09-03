package ru.clevertec.bank.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.clevertec.bank.config.ApplicationConfig;
import ru.clevertec.bank.service.AccountService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class ApplyMonthlyPercentageListener implements ServletContextListener {

    private final AccountService accountService = ApplicationConfig.getAccountService();;

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(accountService::applyPercentage, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
