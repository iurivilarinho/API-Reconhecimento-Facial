package com.br.face.bancoDeDados;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.io.File;
import java.nio.IntBuffer;
import java.util.List;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
/*import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.face.models.Documento;
import com.br.face.service.DocumentoService;

@Service
public class TreinamentoBancoDados {

	@Autowired
	private DocumentoService documentoService;

	public void treinarModelo() {

		List<Documento> arquivos = documentoService.buscar();

		MatVector fotos = new MatVector(arquivos.size());
		Mat rotulos = new Mat(arquivos.size(), 1, CV_32SC1);
		IntBuffer rotulosBuffer = rotulos.createBuffer();
		int contador = 0;
		for (Documento imagem : arquivos) {

			Mat imagemEscalaCinza = imdecode(new Mat(imagem.getDocumento()), IMREAD_GRAYSCALE);

			Integer classe = Integer.parseInt(imagem.getUsuario().getId().toString());
			System.out.println(imagem.getNome());
			resize(imagemEscalaCinza, imagemEscalaCinza, new Size(160, 160));
			fotos.put(contador, imagemEscalaCinza);
			rotulosBuffer.put(contador, classe);
			contador++;
		}

		// Excluir arquivos existentes
		File classificadorEigenFaces = new File("src/main/java/recursos/classificadorEigenFaces.yml");
		if (classificadorEigenFaces.exists())
			classificadorEigenFaces.delete();

//		File classificadorFisherFaces = new File("src/main/java/recursos/classificadorFisherFaces.yml");
//		if (classificadorFisherFaces.exists())
//			classificadorFisherFaces.delete();
//
//		File classificadorLBPH = new File("src/main/java/recursos/classificadorLBPH.yml");
//		if (classificadorLBPH.exists())
//			classificadorLBPH.delete();
		FaceRecognizer eigenfaces = EigenFaceRecognizer.create();
		//FaceRecognizer fisherfaces = FisherFaceRecognizer.create();
		//FaceRecognizer lbph = LBPHFaceRecognizer.create(15, 13, 13, 18, 1);

		eigenfaces.train(fotos, rotulos);
		eigenfaces.save("src/main/java/recursos/classificadorEigenFaces.yml");
//		fisherfaces.train(fotos, rotulos);
//		fisherfaces.save("src/main/java/recursos/classificadorFisherFaces.yml");
//		lbph.train(fotos, rotulos);
//		lbph.save("src/main/java/recursos/classificadorLBPH.yml");
	}
}
