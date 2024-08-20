package com.nemo.oceanAcademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class OceanAcademyApplication {

	public static void main(String[] args) {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure().load();

		// Kakao 환경 변수 설정
		setSystemProperty("KAKAO_CLIENT_ID", dotenv);
		setSystemProperty("KAKAO_CLIENT_SECRET", dotenv);
		setSystemProperty("KAKAO_REDIRECT_URI", dotenv);

		// AWS S3 환경 변수 설정
//		setSystemProperty("S3_ACCESS_KEY", dotenv);
//		setSystemProperty("S3_SECRET_KEY", dotenv);
//		setSystemProperty("S3_BUCKET_NAME", dotenv);
//		setSystemProperty("S3_REGION", dotenv);

		SpringApplication.run(OceanAcademyApplication.class, args);
	}

	// 시스템 프로퍼티 설정 메서드, null 체크
	private static void setSystemProperty(String key, Dotenv dotenv) {
		String value = dotenv.get(key);
		if (value != null) {
			System.setProperty(key, value);
		} else {
			System.out.println("Warning: Environment variable " + key + " is not set.");
		}
	}
}
