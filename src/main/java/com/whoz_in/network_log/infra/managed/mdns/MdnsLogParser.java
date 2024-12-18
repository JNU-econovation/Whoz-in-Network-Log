package com.whoz_in.network_log.infra.managed.mdns;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class MdnsLogParser {
    private static final String deviceRegex = "[가-힣\\w\\s'.()\\-]+(\\._[a-zA-Z0-9\\-]+)+\\.local";

    /**
     * @param log
     * 한 줄 데이터만 들어와야 한다. 들어온 데이터중 \t가 2개가 존재하는 것만 처리한다.
     */
    public Optional<MdnsLog> parse(String log) {
        String[] logParts = log.split("\t");
        if(logParts.length < 2 || logParts[0].isEmpty() || logParts[1].isEmpty()) return Optional.empty();

        String mac = logParts[0];
        String ip = logParts[1];
        String deviceName = (logParts.length == 3) ? parseDeviceName(logParts[2]) : null;

        return Optional.of(new MdnsLog(mac, ip, deviceName));
    }

    //기기 이름 형식에 맞지 않을 때 null을 반환. 내부적으로 사용하는 메서드기 때문에 null을 반환할 수 있도록 했음
    private String parseDeviceName(String devicePart){
        if (devicePart.contains(",") && !devicePart.matches(deviceRegex))
            return null;
        return devicePart.replaceAll("\\._[a-zA-Z0-9\\-]+._tcp\\.local", "");
    }

}
