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

            // Referer와 Origin 헤더를 가져옴
            String referer = request.getHeader("Referer");
            String origin = request.getHeader("Origin");

            // referer나 origin이 localhost일 때 dev 데이터베이스 사용
            if ((referer != null && referer.contains("localhost")) || (origin != null && origin.contains("localhost"))) {
                System.out.println("로컬 환경이 감지되었습니다. dev 데이터베이스를 사용합니다.");
                return "dev";
            }

            // referer나 origin이 dev.nemooceanacademy.com일 때 dev 데이터베이스 사용
            if ((referer != null && referer.contains("dev.nemooceanacademy.com")) ||
                    (origin != null && origin.contains("dev.nemooceanacademy.com"))) {
                System.out.println("개발 환경이 감지되었습니다. dev 데이터베이스를 사용합니다.");
                return "dev";
            }

            // referer나 origin이 www.nemooceanacademy.com일 때 prod 데이터베이스 사용
            if ((referer != null && referer.contains("www.nemooceanacademy.com")) ||
                    (origin != null && origin.contains("www.nemooceanacademy.com"))) {
                System.out.println("프로덕션 환경이 감지되었습니다. prod 데이터베이스를 사용합니다.");
                return "prod";
            }

            // 호스트가 예상 범위 내에 없는 경우 기본 데이터베이스 사용
            return null;  // 기본 데이터베이스 사용
        }

        // HTTP 요청 컨텍스트가 없으면 기본 데이터베이스 사용
        return null;  // 기본 데이터베이스 사용
    }
}
