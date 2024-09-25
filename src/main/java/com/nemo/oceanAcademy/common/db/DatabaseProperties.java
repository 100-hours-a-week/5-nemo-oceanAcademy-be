package com.nemo.oceanAcademy.common.db;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseProperties {
    private DbConfig dev;
    private DbConfig prod;

    // getters and setters

    public static class DbConfig {
        private String url;
        private String username;
        private String password;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

    }

    public DbConfig getDev() {
        return dev;
    }
    public void setDev(DbConfig dev) {
        this.dev = dev;
    }
    public DbConfig getProd() {
        return prod;
    }
    public void setProd(DbConfig prod) {
        this.prod = prod;
    }
}
