package GameShelf.ms_prestamo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsPrestamoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPrestamoApplication.class, args);


        
    }


}