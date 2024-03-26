//package com.br.face.service;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.bytedeco.javacpp.Loader;
//import org.bytedeco.opencv.global.opencv_imgcodecs;
//import org.bytedeco.opencv.global.opencv_imgproc;
//import org.bytedeco.opencv.opencv_core.Mat;
//import org.bytedeco.opencv.opencv_core.RectVector;
//import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//public class ConfereExistenciaFace {
//
//	@RestController
//	public class FaceRecognitionController {
//
//		 @PostMapping("/recognize")
//	        public String recognizeFace(@RequestParam("file") MultipartFile file) throws IOException {
//	            // Carregar o classificador Haar Cascade para detecção de faces
//	            CascadeClassifier faceCascade = new CascadeClassifier();
//	            faceCascade.load("C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");
//
//	            // Carregar uma imagem para teste
//	            File imageFile = File.createTempFile("temp", null);
//	            file.transferTo(imageFile);
//	            Mat image = opencv_imgcodecs.imread(imageFile.getAbsolutePath());
//
//	            // Converter a imagem para tons de cinza
//	            Mat grayImage = new Mat();
//	            opencv_imgproc.cvtColor(image, grayImage, opencv_imgproc.COLOR_BGR2GRAY);
//
//	            // Detectar faces na imagem
//	            RectVector faces = new RectVector();
//	            faceCascade.detectMultiScale(grayImage, faces);
//
//	            // Se uma face for detectada, retornar sucesso
//	            if (faces.size() > 0) {
//	                return "Face detectada!";
//	            } else {
//	                return "Nenhuma face detectada.";
//	            }
//	        }
//	    }
//}
