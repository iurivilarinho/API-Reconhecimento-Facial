package com.br.face.bancoDeDados.api;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.br.face.bancoDeDados.TreinamentoBancoDados;
import com.br.face.models.Documento;
import com.br.face.models.Usuario;
import com.br.face.service.ConfereExistenciaFace;
import com.br.face.service.DocumentoService;
import com.br.face.service.UsuarioService;

@Service
public class CapturaApi {

	@Autowired
	private DocumentoService documentoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TreinamentoBancoDados treinamentoBancoDados;

	@Autowired
	private ConfereExistenciaFace confereExistenciaFace;

	@Transactional
	public void capturar(List<MultipartFile> files, Long idUsuario) throws InterruptedException, IOException {

		System.setProperty("java.awt.headless", "false");
		OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();

		confereExistenciaFace.detectaFace(files);
		documentoService.deletar(idUsuario);
		
		
		for (MultipartFile file : files) {

			InputStream inputStream = file.getInputStream();

			// Inicializa o grabber para obter quadros a partir do InputStream
			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
			grabber.start();

			// Obtém o primeiro quadro do grabber
			Frame frame = grabber.grab();

			CascadeClassifier detectorFace = new CascadeClassifier(
					"C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

			Mat imagemColorida = new Mat();
			int amostra = 1;

			Usuario usuario = usuarioService.buscarUsuarioPorId(idUsuario);

			imagemColorida = converteMat.convert(frame);
			Mat imagemCinza = new Mat();
			System.out.println(imagemCinza);
			System.out.println(imagemColorida);
			cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
			RectVector facesDetectadas = new RectVector();
			detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150, 150),
					new Size(500, 500));

			for (int i = 0; i < facesDetectadas.size(); i++) {
				Rect dadosFace = facesDetectadas.get(0);
				rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));
				Mat faceCapturada = new Mat(imagemCinza, dadosFace);
				resize(faceCapturada, faceCapturada, new Size(160, 160));

				salvarImagem(faceCapturada, usuario);
				System.out.println("Foto " + amostra + " capturada\n");

			}
		}
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
