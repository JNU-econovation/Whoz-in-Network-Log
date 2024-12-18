package com.whoz_in.network_log.infra.monitor;

import com.whoz_in.network_log.persistence.MonitorLogDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonitorLogWriter {
    private final MonitorLogProcess process;
    private final MonitorLogParser parser;
    private final MonitorLogDAO repo;
    private final MonitorConfig config;

    public MonitorLogWriter(MonitorLogParser parser, MonitorLogDAO repo, MonitorConfig config) {
        this.parser = parser;
        this.repo = repo;
        this.config = config;
        this.process = new MonitorLogProcess(config.getCommand(), config.getSudoPassword());
    }
    @Scheduled(fixedRate = 10000)
    private void saveLogs(){
        System.out.println(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Set<String> macs = new HashSet<>();
        String line;
        for(;;) {
            try {
                line = process.readLine();
                if (line == null) break;
                macs.addAll(parser.parse(line));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println("[monitor] 저장할 mac 개수: " + macs.size());
        macs.remove("");
        repo.upsertAll(macs);
        macs.clear();
    }

}