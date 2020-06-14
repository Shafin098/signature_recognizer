package imageprocessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class ImageProcessor {
	
	private final static String SUFIAN_INPUT_FOLDER = "images/sufian_signs";
	private final static String SUFIAN_OUTPUT_FOLDER = "images/resized_images2/";
	private final static String SUFIAN_OUTPUT_FILE = "images/sufian_signs.txt";
	
	private final static String ASIF_INPUT_FOLDER = "images/2020-03";
	private final static String ASIF_OUTPUT_FOLDER = "images/resized_images/";
	private final static String ASIF_OUTPUT_FILE = "images/asif_signs.txt";
	
	private final static String INPUT_FOLDER = ASIF_INPUT_FOLDER;
	private final static String OUTPUT_FOLDER = ASIF_OUTPUT_FOLDER;
	private final static String OUTPUT_FILE = ASIF_OUTPUT_FILE;

	public final static int IMAGE_RES = 10;
	
	public static void main(String[] args) {
		
		List<String> imgFileNames = getFileNames(INPUT_FOLDER);
		
		int id = 1;
		PrintWriter signsDataOutStream = null;
		try {
			signsDataOutStream = new PrintWriter(OUTPUT_FILE);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		for (String imgFileName: imgFileNames) {
			File imgFile = new File(INPUT_FOLDER + "/" + imgFileName);
			BufferedImage imgBuffer = null;
			
			try {
				imgBuffer = ImageIO.read(imgFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			BufferedImage resizedImgBuffer =  getProcessedImage(imgBuffer);
			String imgCSV = convertImageToCSV(resizedImgBuffer, IMAGE_RES*IMAGE_RES);
			
			signsDataOutStream.println(imgCSV);
////			System.out.println(imgCSV);
//			
			try {
				ImageIO.write(resizedImgBuffer, "jpg", 
						new File(OUTPUT_FOLDER + "r" + id++ + ".jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		signsDataOutStream.close();
	}

	public static String convertImageToCSV(BufferedImage resizedImgBuffer, int length) {	
		StringBuilder imgCSV = new StringBuilder();
		
		for (int w=0; w < resizedImgBuffer.getWidth(); w++) {
			for (int h=0; h < resizedImgBuffer.getHeight(); h++) {
				if (resizedImgBuffer.getRGB(w, h) == 0xFFFFFFFF) {
					imgCSV.append(0 + ",");
				} else {
					imgCSV.append(1 + ",");
				}
			}
		}
		
		if (imgCSV.length() < length) {
			int remainingLength = length - imgCSV.length();
			for (int iter=0; iter < remainingLength; iter++) {
				imgCSV.append(0 + ",");
			}
		}

		return imgCSV.substring(0, length-1);
	}

	public static BufferedImage getProcessedImage(BufferedImage imgBuffer) {
		BufferedImage resizedImgBuffer =  Scalr.resize(imgBuffer, IMAGE_RES, Scalr.OP_GRAYSCALE);
		
		int sumOfPixels = 0;
		for (int w=0; w < resizedImgBuffer.getWidth(); w++) {
			for (int h=0; h < resizedImgBuffer.getHeight(); h++) {
				int pixelValue = resizedImgBuffer.getRGB(w, h) & 0x000000FF;
//				System.out.println(pixelValue);
				sumOfPixels += pixelValue;
			}
		}
		int averagePixelSum = sumOfPixels / (resizedImgBuffer.getHeight() * resizedImgBuffer.getWidth());
//		System.out.println(averagePixelSum);
		
		for (int w=0; w < resizedImgBuffer.getWidth(); w++) {
			for (int h=0; h < resizedImgBuffer.getHeight(); h++) {
				if ((resizedImgBuffer.getRGB(w, h) & 0x000000FF) > averagePixelSum) {
					resizedImgBuffer.setRGB(w, h, 0xFFFFFFFF);
				} else {
					resizedImgBuffer.setRGB(w, h, 0x00000000);
				}
			}
		}
		return resizedImgBuffer;
	}

	public static double[][] csvToArray(String fileName) {
		File csvFile = new File(fileName);
		Scanner csvInput = null;
		try {
			csvInput = new Scanner(csvFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		double[][] inputs;
		if (getLineCount(csvFile) > 1) {
			inputs = new double[getLineCount(csvFile) - 1][];
		} else {
			inputs = new double[getLineCount(csvFile)][];
		}

		int index = 0;
		
		do {
			String[] values = csvInput.next().split(",");

			double[] imgArray = new double[IMAGE_RES * IMAGE_RES];
			for (int i = 0; i < values.length; i++) {
				imgArray[i] = Double.parseDouble(values[i]);
			}
			inputs[index++] = imgArray;
		
		} while (csvInput.hasNext());
		
		
		return inputs;
	}
	
	private static int getLineCount(File csvFile) {
		Scanner csvInput = null;

		try {
			csvInput = new Scanner(csvFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int lineNumber = 0;
		while (csvInput.hasNextLine()) {
			csvInput.nextLine();
			lineNumber++;
		}
		return lineNumber + 1;
	}
	
	private static List<String> getFileNames(String folderName) {
		File file = new File(folderName);
		File[] allFiles = file.listFiles();
		
		List<String> imgFileName = new ArrayList<>();
		for (int i=0; i < allFiles.length; i++) {
			if (allFiles[i].isFile()) {
				imgFileName.add(allFiles[i].getName());
			}
		}
		return imgFileName;
	}

}
