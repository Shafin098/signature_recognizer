import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;

import imageprocessor.ImageProcessor;
import neuralnet.NeuralNet;

public class SignatureRecognizer {

	
	private final static String MODEL_PATH = "prediction_model/neuralnet.model";
	
	public static void main(String[] args) {
		File modelFile = new File(MODEL_PATH);
		ObjectInputStream modelInputStream = null;
		try {
			modelInputStream = new ObjectInputStream(new FileInputStream(modelFile));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		NeuralNet neuralNet = null;
		try {
			neuralNet = (NeuralNet) modelInputStream.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		double[] imageInput = getImageInput();
		
		double[] predictions = neuralNet.feedForward(imageInput);
		
		for (double p: predictions) {
			System.out.print(p + ", ");
		}
		System.out.println();
	}

	private static double[] getImageInput() {
		System.out.print("Image path: ");
		Scanner in = new Scanner(System.in);
		String imgPath = in.nextLine();
		
		File imgFile = new File(imgPath);
		BufferedImage imgBuffer = null;
		
		try {
			imgBuffer = ImageIO.read(imgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage resizedImgBuffer =  ImageProcessor.getProcessedImage(imgBuffer);
		String imgCSV = ImageProcessor
				.convertImageToCSV(resizedImgBuffer, ImageProcessor.IMAGE_RES * ImageProcessor.IMAGE_RES);
		
		PrintWriter csvOutputWriter = null;
		try {
			csvOutputWriter = new PrintWriter(new File("prediction_model/input_img.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		csvOutputWriter.println(imgCSV);
		csvOutputWriter.close();
		
		double[] imgArray =  ImageProcessor.csvToArray("prediction_model/input_img.txt")[0];
		return imgArray;
	}

}
