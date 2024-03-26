package com.br.face;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FaceApplication {

	public static void main(String[] args) throws Exception, InterruptedException {
		SpringApplication.run(FaceApplication.class, args);

		// Captura.capturar();
		// Treinamento.treinarModelo();
		// Reconhecimento.reconhecer();
	}

}
