package hr.kanezi.postgres.fts;

import hr.kanezi.postgres.fts.quotes.QuotesViewRepository;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Value
@NonFinal
@Log4j2
public class FtsApplication {

	QuotesViewRepository quotesViewRepository;

	public static void main(String[] args) {
		SpringApplication.run(FtsApplication.class, args);
	}


	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
			log.info("number of quotes: {}", quotesViewRepository.count());
		};
	}

}
