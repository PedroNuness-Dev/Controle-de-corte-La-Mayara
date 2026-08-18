package com.PedroNunesDev.Controle_de_Corte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ControleDeCorteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControleDeCorteApplication.class, args);
	}

}
