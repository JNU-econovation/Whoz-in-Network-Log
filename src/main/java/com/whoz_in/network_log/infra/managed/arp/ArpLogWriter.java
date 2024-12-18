package com.whoz_in.network_log.infra.managed.arp;

import com.whoz_in.network_log.persistence.ManagedLog;
import com.whoz_in.network_log.persistence.ManagedLogDAO;
import com.whoz_in.network_log.persistence.ManagedLogRepository;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ArpLogWriter {

    private final ManagedLogRepository repository;
    private final ManagedLogDAO managedLogDAO;
    private final ArpLogParser arpLogParser;
    private final ArpLogProcess arpLogProcess;

    private final Set<String> logs = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ArpLogWriter(ManagedLogRepository repository,
                        ManagedLogDAO managedLogDAO,
                        ArpLogParser arpLogParser,
                        ArpLogProcess arpLogProcess) {
        this.repository = repository;
        this.managedLogDAO = managedLogDAO;
        this.arpLogParser = arpLogParser;
        this.arpLogProcess = arpLogProcess;
    }

    @Scheduled(fixedRate = 5000)
    private void scan() {
        CompletableFuture<Set<String>> logCollect =
                CompletableFuture.supplyAsync(arpLogProcess::start);

        Set<String> result = logCollect.join();
        logs.addAll(result);

        save();
    }

    //TODO: LogEntity들 저장
    private void save(){
        Set<ManagedLog> managedLogs = logs.stream()
                .filter(arpLogParser::validate)
                .map(arpLogParser::parse)
                .map(ManagedLog::create)
                .collect(Collectors.toSet());

        System.out.println(String.format("[arp] 저장할 로그 개수 : %d", managedLogs.size()));

        managedLogDAO.bulkInsert(managedLogs);
    }

}
