package neuralnet;

import java.io.Serializable;

public class NeuralNet implements Serializable {

	private static final long serialVersionUID = -7986774133112691622L;
	
	private Neuron[][] structure;

	public NeuralNet(int[] layersAndNeurons, int featuresCount) {
		int totalLayers = layersAndNeurons.length;
		this.structure = new Neuron[totalLayers][];

		for (int layerIndex = 0; layerIndex < totalLayers; layerIndex++) {
			int neuronsCount = layersAndNeurons[layerIndex];
			this.structure[layerIndex] = new Neuron[neuronsCount];

			for (int neuronIndex = 0; neuronIndex < this.structure[layerIndex].length; neuronIndex++) {
				if (layerIndex == 0) {
					this.structure[layerIndex][neuronIndex] = new Neuron(featuresCount);
				} else {
					this.structure[layerIndex][neuronIndex] = new Neuron(layersAndNeurons[layerIndex - 1]);
				}
			}
		}
	}

	public double[] feedForward(double[] input) {
		double[] inputForCurrentLayer = input;
		double[] inputForNextLayer = new double[this.structure[0].length];

		for (int layerIndex = 0; layerIndex < this.structure.length; layerIndex++) {
			inputForNextLayer = new double[this.structure[layerIndex].length];

			for (int neuronIndex = 0; neuronIndex < this.structure[layerIndex].length; neuronIndex++) {
				try {
					inputForNextLayer[neuronIndex] = this.structure[layerIndex][neuronIndex]
							.fireNeuron(inputForCurrentLayer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			inputForCurrentLayer = inputForNextLayer;
		}

		return inputForNextLayer;
	}

	public double loss(double[][] inputs, double[] outputs) {
		double squaredSumOfLoss = 0;
		for (int i = 0; i < inputs.length; i++) {
			// refactor for multiclass classification
			// refactor done could contain some error
			double tmpSum = 0;
			for (int j = 0; j < inputs[i].length; j++) {
				tmpSum += Math.pow(outputs[i] - feedForward(inputs[i])[i], 2);
			}
			tmpSum = tmpSum / inputs[i].length;
			squaredSumOfLoss += tmpSum;
		}
		return (squaredSumOfLoss / inputs.length);
	}

	public void train(double[][] inputs, double[][] outputs) {
		final double LEARNIG_RATE = 1;
		final int EPOCHS = 100000;

		for (int epoch = 0; epoch < EPOCHS; epoch++) {

			for (int i = 0; i < inputs.length; i++) {
				try {
					feedForward(inputs[i]);
					double e = 0;
					for (int layerIndex = this.structure.length - 1; layerIndex >= 0; layerIndex--) {
						for (int neuronIndex = 0; neuronIndex < this.structure[layerIndex].length; neuronIndex++) {

//							if (layerIndex == this.structure.length - 1) {
//								// neuron is in output layer
//								Neuron neuron = this.structure[layerIndex][neuronIndex];
//
//								double derivativeOfPrediction = (1 - neuron.getActivationValue()) * deriveSigmoid(neuron.getActivationValue());
//
//								for (int weightIndex = 0; weightIndex < neuron.getWeightCount(); weightIndex++) {
//									
//									double derivationOfPreviousLayersActivison = neuron.getWeight(weightIndex)
//											* deriveSigmoid(neuron.getActivationValue());
//
//									Neuron nodeInPreviousLayer = this.structure[layerIndex - 1][weightIndex];
//									nodeInPreviousLayer.setDeivativeOfActivation(derivationOfPreviousLayersActivison);
//
//									double derivativeOfWeight = neuron.getInputFromPreviousLayer(weightIndex)
//											* deriveSigmoid(neuron.getActivationValue());
//									neuron.updateWeight(LEARNIG_RATE * derivativeOfPrediction * derivativeOfWeight,
//											weightIndex);
//								}
//								double derivativeOfBias = deriveSigmoid(neuron.getActivationValue());
//								neuron.updateBias(LEARNIG_RATE * derivativeOfPrediction * derivativeOfBias);
//								
//								feedForward(inputs[i]);
//								int a = 0;
//							} else {
//								// neuron in hidden layer
//								Neuron neuron = this.structure[layerIndex][neuronIndex];
//
//								for (int weightIndex = 0; weightIndex < neuron.getWeightCount(); weightIndex++) {
//									double derivationOfPreviousLayersActivison = neuron.getWeight(weightIndex)
//											* deriveSigmoid(neuron.getActivationValue());
//
//									if (layerIndex != 0) {
//										Neuron nodeInPreviousLayer = this.structure[layerIndex - 1][weightIndex];
//										nodeInPreviousLayer
//												.setDeivativeOfActivation(derivationOfPreviousLayersActivison);
//									}
//
//									double derivativeOfWeight = neuron.getInputFromPreviousLayer(weightIndex)
//											* deriveSigmoid(neuron.getActivationValue());
//									neuron.updateWeight(
//											LEARNIG_RATE * neuron.getDeivativeOfActivation() * derivativeOfWeight,
//											weightIndex);
//								}
//								double derivativeOfBias = deriveSigmoid(neuron.getActivationValue());
//								neuron.updateBias(LEARNIG_RATE * neuron.getDeivativeOfActivation() * derivativeOfBias);
//							}
							double delta = 0.000001;
							Neuron neuron = this.structure[layerIndex][neuronIndex];
							
							for (int weightIndex=0; weightIndex < neuron.getWeightCount(); weightIndex++) {
								double weightValue = neuron.getWeight(weightIndex);
								double averageCost = calculateAverageCost(inputs, outputs);
								neuron.setWeight(weightValue + delta, weightIndex);
								double newAverageCost = calculateAverageCost(inputs, outputs);
								double gradient = (newAverageCost - averageCost) / delta;
								
								double updatedWeight = weightValue - (LEARNIG_RATE * gradient);
								neuron.setWeight(updatedWeight, weightIndex);
							}
							
							double biasValue = neuron.getBias();
							double averageCost = calculateAverageCost(inputs, outputs);
							neuron.setBias(biasValue + delta);
							double newAverageCost = calculateAverageCost(inputs, outputs);
							double gradient = (newAverageCost - averageCost) / delta;
							
							double updatedBias = biasValue - (LEARNIG_RATE * gradient);
							neuron.setBias(updatedBias);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			double averageError = calculateAverageCost(inputs, outputs);
			System.out.println("Average error: " + averageError);
			if (averageError <= 0.04) {
				break;
			}
		}
	}

	private double calculateAverageCost(double[][] inputs, double[][] outputs) {
		double sumOfWights = 0;
		for (int i=0; i < inputs.length; i++) {
			if (inputs[i] == null) continue;
			
			feedForward(inputs[i]);
			
			Neuron[] outputLayer = this.structure[this.structure.length-1];
			double tmpError = 0;
			for (int neuronIndex=0; neuronIndex < outputLayer.length; neuronIndex++) {
				tmpError += Math.pow((outputs[i][neuronIndex] - outputLayer[neuronIndex].getActivationValue()) ,2);
			}
			sumOfWights += tmpError / outputLayer.length;
		}
		return sumOfWights / inputs.length;
	}

	private double deriveSigmoid(double value) {
		double v = value * (1 - value);
//		if (v == Double.POSITIVE_INFINITY) {
//			return Double.MAX_VALUE - 10000;
//		}
//		if (v == Double.NEGATIVE_INFINITY) {
//			return Double.MIN_VALUE + 10000;
//		}
		return v;
	}

	@Override
	public String toString() {
		String selfStructure = "";
		for (int layer = 0; layer < this.structure.length; layer++) {
			for (int neuron = 0; neuron < this.structure[layer].length; neuron++) {
				selfStructure += this.structure[layer][neuron];
				selfStructure += " ";
			}
			selfStructure += "\n";
		}
		return selfStructure;
	}

}
