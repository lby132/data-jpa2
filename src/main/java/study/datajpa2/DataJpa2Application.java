package study.datajpa2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class DataJpa2Application {

	public static void main(String[] args) {
		SpringApplication.run(DataJpa2Application.class, args);
	}

}
