package lotecs.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("lotecs.auth.infrastructure.persistence.**.mapper")
@SpringBootApplication
public class LotecsAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(LotecsAuthApplication.class, args);
    }
}
