package com.br.face.bancoDeDados.desktop;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.face.bancoDeDados.TreinamentoBancoDados;
import com.br.face.models.Documento;
import com.br.face.models.Usuario;
import com.br.face.service.DocumentoService;
import com.br.face.service.UsuarioService;

@Service
public class CapturaDesktop {

	@Autowired
	private DocumentoService documentoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TreinamentoBancoDados treinamentoBancoDados;

	public void capturar(Long idUsuario) throws Exception, InterruptedException {
		System.setProperty("java.awt.headless", "false");

		KeyEvent tecla = null;
		OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
		OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
		camera.start();

		CascadeClassifier detectorFace = new CascadeClassifier(
				"C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

		CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
		Frame frameCapturado = null;
		Mat imagemColorida = new Mat();
		int numeroAmostras = 50;
		int amostra = 1;

		Usuario usuario = usuarioService.buscarUsuarioPorId(idUsuario);

		while ((frameCapturado = camera.grab()) != null) {
			imagemColorida = converteMat.convert(frameCapturado);
			Mat imagemCinza = new Mat();
			System.out.println(imagemCinza);
			System.out.println(imagemColorida);
			cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
			RectVector facesDetectadas = new RectVector();
			detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150, 150),
					new Size(500, 500));
			if (tecla == null) {
				tecla = cFrame.waitKey(5);
			}
			for (int i = 0; i < facesDetectadas.size(); i++) {
				Rect dadosFace = facesDetectadas.get(0);
				rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));
				Mat faceCapturada = new Mat(imagemCinza, dadosFace);
				resize(faceCapturada, faceCapturada, new Size(160, 160));
				if (tecla == null) {
					tecla = cFrame.waitKey(5);
				}
				if (tecla != null) {
					if (tecla.getKeyChar() == 'q') {
						if (amostra <= numeroAmostras) {
							salvarImagem(faceCapturada, usuario);
							System.out.println("Foto " + amostra + " capturada\n");
							amostra++;
						}
					}
					tecla = null;
				}
			}
			if (tecla == null) {
				tecla = cFrame.waitKey(20);
			}
			if (cFrame.isVisible()) {
				cFrame.showImage(frameCapturado);
			}

			if (amostra > numeroAmostras) {
				break;
			}
		}
		cFrame.dispose();
		camera.stop();
		treinamentoBancoDados.treinarModelo();
	}

	public void salvarImagem(Mat imagem, Usuario usuario) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			BufferedImage bufferedImage = Java2DFrameUtils.toBufferedImage(imagem);
			ImageIO.write(bufferedImage, "jpg", stream);
			byte[] dadosImagem = stream.toByteArray();

			Documento novaImagem = new Documento(usuario.getNome(), "image/jpeg", dadosImagem, usuario);

			documentoService.salvar(novaImagem);

		} catch (IOException e) {
			// Trate exceções de IO, se necessário
		}
	}
}
