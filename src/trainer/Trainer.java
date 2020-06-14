package trainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Scanner;

import imageprocessor.ImageProcessor;
import neuralnet.NeuralNet;

public class Trainer {

	private final static int IMAGE_RES = 10;
	private final static int FEATURES = IMAGE_RES * IMAGE_RES;
	private static final String MODEL_PATH = "prediction_model/neuralnet.model";

	public static void main(String[] args) {
		NeuralNet neuralNet = new NeuralNet(new int[] { 6, 1 }, FEATURES);
		System.out.println(neuralNet);

		double[][] asifInputs = ImageProcessor.csvToArray("images/asif_signs.txt");
		double[][] asifOutputs = new double[asifInputs.length][FEATURES];
		// setting output value for asif signature
		for (int row = 0; row < asifInputs.length; row++) {
			asifOutputs[row] = new double[] { 1 };
		}

		double[][] sufianInputs = ImageProcessor.csvToArray("images/sufian_signs.txt");
		double[][] sufianOutputs = new double[sufianInputs.length][FEATURES];
		// setting output value for sufian signature
		for (int row = 0; row < sufianInputs.length; row++) {
			sufianOutputs[row] = new double[] { 0 };
		}

		double[][] inputs = join2dArray(asifInputs, sufianInputs);
		double[][] outputs = join2dArray(asifOutputs, sufianOutputs);
		

		neuralNet.train(inputs, outputs);
		File modelFile = new File(MODEL_PATH);
		try {
			ObjectOutputStream modelOutputStream = new ObjectOutputStream(new FileOutputStream(modelFile));
			modelOutputStream.writeObject(neuralNet);
			System.out.println("Saved model to prediction/neuralnet.model");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// testing asif signs
		for (int i = 0; i < 4; i++) {
			double[] output = neuralNet.feedForward(asifInputs[i]);

			System.out.print("Input: ");
			for (double input : asifInputs[i]) {
				System.out.print(input + ", ");
			}
			System.out.println();
			for (double d : output) {
				System.out.println(d);
			}
		}

		// testing sufian signs
		for (int i = 0; i < 4; i++) {
			double[] output = neuralNet.feedForward(sufianInputs[i]);

			System.out.print("Input: ");
			for (double input : sufianInputs[i]) {
				System.out.print(input + ", ");
			}
			System.out.println();
			for (double d : output) {
				System.out.println(d);
			}
		}
		
		

		// ----------test-for-and-logic-gate------------
//		int totalInputs = 100;
//		
//		double[][] inputs = new double[totalInputs][];
//		double[][] outputs = new double[totalInputs][];
//		
//		for (int i=0; i < totalInputs; i += 4) {
//			inputs[i]  = new double[] {0, 0};
//			inputs[i+1]  = new double[] {0, 1};
//			inputs[i+2]  = new double[] {1, 0};
//			inputs[i+3]  = new double[] {1, 1};
//			
//			outputs[i] = new double[] {0};
//			outputs[i+1] = new double[] {0};
//			outputs[i+2] = new double[] {0};
//			outputs[i+3] = new double[] {1};
//		}
//		
//		nn.train(inputs, outputs);
//		
//		for (int i = 0; i < 4; i++) {
//			double[] output = nn.feedForward(inputs[i]);
//
//			System.out.print("Input: ");
//			for (double input : inputs[i]) {
//				System.out.print(input + ", ");
//			}
//			System.out.println();
//			for (double d : output) {
//				System.out.println(d);
//			}
//		}
		// --------------------------------------------
	}

	private static double[][] join2dArray(double[][] firstArray, double[][] secondArray) {
		double[][] combinedArray = new double[firstArray.length + secondArray.length][];

		int lastInsertedIndex = 0;
		for (int i = 0; i < firstArray.length; i++) {
			combinedArray[i] = firstArray[i];
			lastInsertedIndex++;
		}

		for (int i = 0; i < secondArray.length; i++) {
			combinedArray[lastInsertedIndex++] = secondArray[i];
		}

		return combinedArray;
	}

}
