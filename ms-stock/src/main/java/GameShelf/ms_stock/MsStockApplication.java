package GameShelf.ms_stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsStockApplication.class, args);
    }
}