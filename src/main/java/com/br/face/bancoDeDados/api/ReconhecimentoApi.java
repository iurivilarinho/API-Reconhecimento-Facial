package com.br.face.bancoDeDados.api;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.br.face.models.Usuario;
import com.br.face.models.UsuarioDTO;
import com.br.face.service.UsuarioService;

@Service
public class ReconhecimentoApi {

	@Autowired
	private UsuarioService usuarioService;

	@Value("${confianca.reconhecimento}")
	private int thresholdConfianca;

	@Transactional(readOnly = true)
	public UsuarioDTO reconhecer(List<MultipartFile> files) throws IOException {

		System.setProperty("java.awt.headless", "false");

		OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();

		List<Usuario> usuariosEncontrados = new ArrayList<>();
		Map<Usuario, Double> confiabilidade = new HashMap<>();

		for (MultipartFile file : files) {

			InputStream inputStream = file.getInputStream();

			// Inicializa o grabber para obter quadros a partir do InputStream
			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
			grabber.start();

			// Obtém o primeiro quadro do grabber
			Frame frame = grabber.grab();

			List<Usuario> usuarios = usuarioService.buscar();

			CascadeClassifier detectorFace = new CascadeClassifier(
					"src/main/java/recursos/haarcascade_frontalface_alt.xml");

//			FaceRecognizer reconhecedor = EigenFaceRecognizer.create(); // *antes: createEigenFaceRecognizer();
//			reconhecedor.read("src/main/java/recursos/classificadorEigenFaces.yml"); // *antes: load()

			FaceRecognizer reconhecedor = LBPHFaceRecognizer.create(1, 8, 8, 8, 100);
			reconhecedor.read("src/main/java/recursos/classificadorLBPH.yml");

			Mat imagemColorida = new Mat();

			imagemColorida = converteMat.convert(frame);
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

				if ((faceCapturada.size(0) == 160) || (faceCapturada.size(1) == 160)) {
					continue;
				}

				resize(faceCapturada, faceCapturada, new Size(160, 160));
				reconhecedor.predict(faceCapturada, rotulo, confianca);
				Integer predicao = rotulo.get(0);

				if (predicao == -1 || confianca.get(0) > thresholdConfianca) {

					System.out.println("Desconhecido");
					System.out.println("confiança:" + confianca.get(0));

				} else {

					String nome = usuarios.stream().filter(u -> u.getId().equals(Long.parseLong(predicao.toString())))
							.map(Usuario::getNome).findAny().orElseThrow();
					;

					System.out.println("ID Usuario: " + rotulo.get(0));
					System.out.println("nome: " + nome);
					System.out.println("confiança:" + confianca.get(0));

					Usuario usuario = usuarioService.buscarUsuarioPorId(Long.parseLong(predicao.toString()));
					confiabilidade.put(usuario, confianca.get(0));

					usuariosEncontrados.add(usuario);

				}
			}
		}

		Map<Usuario, Long> contagemUsuarios = usuariosEncontrados.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		// Encontrando o usuário com a maior contagem
		// Ordenar os usuários por contagem e, em seguida, por confiabilidade (em caso
		// de empate)
		// Verificar se há mais de um item com a mesma contagem
		long maiorContagem = contagemUsuarios.values().stream().max(Long::compare).orElse(0L);
		boolean haEmpate = contagemUsuarios.values().stream().filter(contagem -> contagem == maiorContagem).count() > 1;

		// Ordenar os usuários por contagem e, em seguida, por confiabilidade (em caso
		// de empate)
		Optional<Map.Entry<Usuario, Long>> entry = contagemUsuarios.entrySet().stream()
				.sorted(Map.Entry.<Usuario, Long>comparingByValue().reversed()) // Ordenar por contagem (maior para
																				// menor)
				.sorted((e1, e2) -> {
					if (haEmpate) {
						// Comparar por confiabilidade em caso de contagem igual
						double confiabilidade1 = confiabilidade.getOrDefault(e1.getKey(), Double.MAX_VALUE);
						double confiabilidade2 = confiabilidade.getOrDefault(e2.getKey(), Double.MAX_VALUE);
						return Double.compare(confiabilidade1, confiabilidade2); // Ordenar por confiabilidade (menor
																					// para maior)
					}
					return 0; // Não há empate, então não há necessidade de comparação de confiabilidade
				}).findFirst();

		Optional<Usuario> usuarioSelecionado = entry.map(Map.Entry::getKey);
		// Retornando o usuário mais frequente, ou null se a lista estiver vazia

		if (usuarioSelecionado.isPresent()) {
			return new UsuarioDTO(usuarioSelecionado.get(), confiabilidade.get(usuarioSelecionado.get()).intValue(),
					null);
		} else {
			return null;
		}
	}
}