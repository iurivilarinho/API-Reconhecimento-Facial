package com.br.face.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ConfereExistenciaFace {

	public Boolean detectaFace(List<MultipartFile> files) throws IOException {
		// Carregar o classificador Haar Cascade para detecção de faces
		CascadeClassifier faceCascade = new CascadeClassifier();
		faceCascade.load("src/main/java/recursos/haarcascade_frontalface_alt.xml");

		// Carregar uma imagem para teste
		List<String> errorMessage = new ArrayList<>();

		for (MultipartFile file : files) {
			byte[] bytes = file.getBytes();
			// Criar um ByteArrayInputStream a partir dos bytes
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

			// Ler a imagem a partir do ByteArrayInputStream
			Mat image = opencv_imgcodecs.imdecode(new Mat(inputStream.readAllBytes()),
					opencv_imgcodecs.IMREAD_UNCHANGED);
			inputStream.close();
			// Converter a imagem para tons de cinza
			Mat grayImage = new Mat();
			opencv_imgproc.cvtColor(image, grayImage, opencv_imgproc.COLOR_BGR2GRAY);

			// Detectar faces na imagem
			RectVector faces = new RectVector();
			faceCascade.detectMultiScale(grayImage, faces);

			if (faces.size() == 0) {
				errorMessage.add(file.getOriginalFilename());
			}
		}

		if (errorMessage.isEmpty()) {
			return true;
		} else {
			throw new BadRequestException(
					"Nenhum rosto foi detectado nas seguintes imagens:" + errorMessage.toString());
		}

	}

}
