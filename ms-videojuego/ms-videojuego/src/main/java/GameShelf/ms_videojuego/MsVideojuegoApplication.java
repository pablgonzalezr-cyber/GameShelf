package GameShelf.ms_videojuego;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsVideojuegoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsVideojuegoApplication.class, args);
	}

}
