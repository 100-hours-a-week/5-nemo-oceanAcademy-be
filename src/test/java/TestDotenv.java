import io.github.cdimascio.dotenv.Dotenv;

public class TestDotenv {
    public static void main(String[] args) {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.configure().load();

        // .env 파일 값을 시스템 환경 변수로 설정
        System.setProperty("KAKAO_CLIENT_ID", dotenv.get("KAKAO_CLIENT_ID"));
        System.setProperty("KAKAO_CLIENT_SECRET", dotenv.get("KAKAO_CLIENT_SECRET"));
        System.setProperty("KAKAO_REDIRECT_URI", dotenv.get("KAKAO_REDIRECT_URI"));

        // getenv()로 시스템 환경 변수 출력
        String clientId = System.getenv("KAKAO_CLIENT_ID");
        String clientSecret = System.getenv("KAKAO_CLIENT_SECRET");
        String redirectUri = System.getenv("KAKAO_REDIRECT_URI");

        System.out.println("KAKAO_CLIENT_ID: " + clientId);
        System.out.println("KAKAO_CLIENT_SECRET: " + clientSecret);
        System.out.println("KAKAO_REDIRECT_URI: " + redirectUri);
    }
}
