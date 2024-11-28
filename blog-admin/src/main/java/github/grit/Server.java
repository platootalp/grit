package github.grit;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("github.grit.mapper")
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
