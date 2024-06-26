package com.br.face.bancoDeDados.desktop;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.util.List;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.face.models.Usuario;
import com.br.face.service.UsuarioService;

@Service
public class ReconhecimentoDesktop {

	@Autowired
	private UsuarioService usuarioService;

	public void reconhecer() throws Exception {

		System.setProperty("java.awt.headless", "false");
		double thresholdConfianca = 6094.0310704250915; // Defina o threshold de confiança adequado para o seu caso
		// double thresholdConfianca = 9051.612605355683; // Defina o threshold de
		// confiança adequado para o seu caso

		OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
		OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);

		List<Usuario> usuarios = usuarioService.buscar();

		camera.start();

		CascadeClassifier detectorFace = new CascadeClassifier(
				"src/main/java/recursos/haarcascade_frontalface_alt.xml");

		FaceRecognizer reconhecedor = EigenFaceRecognizer.create(); // *antes: createEigenFaceRecognizer();
		reconhecedor.read("src/main/java/recursos/classificadorEigenFaces.yml"); // *antes: load()
		// reconhecedor.setThreshold(0);

//		 FaceRecognizer reconhecedor = FisherFaceRecognizer.create();
//		 reconhecedor.read("src/main/java/recursos/classificadorFisherFaces.yml");

//		 FaceRecognizer reconhecedor = LBPHFaceRecognizer.create();
//		 reconhecedor.read("src/main/java/recursos/classificadorLBPH.yml");

		CanvasFrame cFrame = new CanvasFrame("Reconhecimento", CanvasFrame.getDefaultGamma() / camera.getGamma());
		Frame frameCapturado = null;
		Mat imagemColorida = new Mat();

		while ((frameCapturado = camera.grab()) != null) {
			imagemColorida = converteMat.convert(frameCapturado);
			Mat imagemCinza = new Mat();
			cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
			RectVector facesDetectadas = new RectVector();
			detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 2, 0, new Size(100, 100),
					new Size(500, 500));
			for (int i = 0; i < facesDetectadas.size(); i++) {
				Rect dadosFace = facesDetectadas.get(i);
				rectangle(imagemColorida, dadosFace, new Scalar(0, 255, 0, 0));
				Mat faceCapturada = new Mat(imagemCinza, dadosFace);

				IntPointer rotulo = new IntPointer(1);
				DoublePointer confianca = new DoublePointer(1);

				System.out.println("w=" + faceCapturada.size(0) + "  /  h=" + faceCapturada.size(1));
				if ((faceCapturada.size(0) == 160) || (faceCapturada.size(1) == 160)) {
					continue;
				}
				resize(faceCapturada, faceCapturada, new Size(160, 160));
				// Size tamanho = new Size(faceCapturada);
				reconhecedor.predict(faceCapturada, rotulo, confianca);
				Integer predicao = rotulo.get(0);
				System.out.println("rotulo: " + rotulo.get(0));
				String nome;
				if (predicao == -1 || confianca.get(0) > thresholdConfianca) {
					nome = "Desconhecido";
				} else {

					nome = usuarios.stream().filter(u -> u.getId().equals(Long.parseLong(predicao.toString())))
							.map(Usuario::getNome).findAny().orElseThrow();

				}

				System.out.println("confiança:" + confianca.get(0));

				int x = Math.max(dadosFace.tl().x() - 10, 0);
				int y = Math.max(dadosFace.tl().y() - 10, 0);
				putText(imagemColorida, nome, new Point(x, y), FONT_HERSHEY_PLAIN, 1.4, new Scalar(0, 255, 0, 0));
			}
			if (cFrame.isVisible()) {
				cFrame.showImage(frameCapturado);
			}
		}
		cFrame.dispose();
		camera.stop();
	}

}
