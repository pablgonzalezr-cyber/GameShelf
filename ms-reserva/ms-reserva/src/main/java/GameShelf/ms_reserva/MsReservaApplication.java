package GameShelf.ms_reserva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsReservaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReservaApplication.class, args);
    }
}
