package com.nemo.oceanAcademy.common.db;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class SubdomainRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String host = request.getServerName();  // 서브 도메인을 감지

        if (host.startsWith("dev.") || "localhost".equals(host)) {
            // dev 서브 도메인이거나 로컬 환경이면 dev 데이터베이스 사용
            return "dev";
        } else {
            // 운영용 DB 사용
            return "prod";
        }
    }

}
