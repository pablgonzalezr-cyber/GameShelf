package GameShelf.ms_autorizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsAutorizacionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAutorizacionApplication.class, args);
    }

                
}
