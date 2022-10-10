package restapi.beerorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import restapi.beerorder.pojos.beer.Beer;

import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication
public class BeerOrderApplication {

	public static void main(String... args) {

		SpringApplication.run(BeerOrderApplication.class, args);
	}

	@Bean
	public Executor taskExecutor(){
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(5);
		taskExecutor.setKeepAliveSeconds(1);
		taskExecutor.setQueueCapacity(100);

		taskExecutor.setThreadNamePrefix("TaskExecutor");
		taskExecutor.initialize();

		return taskExecutor;
	}
}
