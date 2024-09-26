package com.nemo.oceanAcademy.common.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class SubdomainRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // RequestContextHolder를 통해 현재 HTTP 요청 컨텍스트가 있는지 확인
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String host = request.getServerName();  // 서버 네임을 감지

            if (host == null || host.isEmpty()) {
                throw new IllegalStateException("서버 호스트 정보를 찾을 수 없습니다.");
            }

            System.out.println("도메인: " + host);

            // dev 서브 도메인이거나 로컬 환경이면 dev 데이터베이스 사용
            if (host.startsWith("dev.") || "localhost".equals(host)) {
                return "dev";
            }

            // www.nemooceanacademy.com일 때 prod 데이터베이스 사용
            if ("www.nemooceanacademy.com".equals(host)) {
                return "prod";
            }

            // 호스트가 예상 범위 내에 없는 경우 기본 prod로 설정
            return null;
        }

        // HTTP 요청 컨텍스트가 없으면 기본 데이터베이스 사용 (기본 datasource 아래 설정 사용)
        return null;
    }
}
