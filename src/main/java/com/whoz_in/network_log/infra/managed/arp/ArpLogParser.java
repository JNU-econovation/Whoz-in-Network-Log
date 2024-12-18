package com.whoz_in.network_log.infra.managed.arp;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ArpLogParser {

    private final String macRegex = "^([0-9A-Fa-f]{2}([-:])(?=[0-9A-Fa-f]{2}([-:]))[0-9A-Fa-f]{2}([-:])){5}[0-9A-Fa-f]{2}$|^[0-9A-Fa-f]{4}(\\.[0-9A-Fa-f]{4}){2}$";
    private final String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    // 파라미터로 전달된 한 줄의 로그가, 유효한지 검사
    public boolean validate(String log){
        if(log.split("\t").length!=3) return false;
        if(log.split("\t")[0].matches(macRegex)) return false;
        return !log.split("\t")[1].matches(ipRegex);
    }

    public ArpLog parse(String log){
        String[] splited = log.split("\t");

        return new ArpLog(splited[0], splited[1], splited[2], "ECONO_5G");
    }
}
