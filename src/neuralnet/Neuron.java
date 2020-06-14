package neuralnet;

import java.io.Serializable;
import java.util.Random;

public class Neuron implements Serializable {
	
	private static final long serialVersionUID = -1654373962002109373L;
	
	private double[] weights;
	private double bias;
	
	private double activationValue = 0;
	
	private double[] inputFromPreviousLayer;
	
	// receives from next layer as input in backpropagation phase
	private double derivativeOfActivation = 0;
	
	public Neuron(int weightsCount) {
		Random random = new Random();
		
		this.bias = 0;
		this.weights = new double[weightsCount];
		// setting all weights to random value between 0 and 1
		for (int weightIndex=0; weightIndex < this.weights.length; weightIndex++) {
			this.weights[weightIndex] = random.nextDouble();
		}
	}
	
	public double fireNeuron(double[] inputs) throws Exception {
		if (inputs.length != this.weights.length) {
			throw new Exception("Invalid input dimension");
		} else {
			inputFromPreviousLayer = new double[this.weights.length];
			double sumOfWeights = 0;
			// multiplying all input features with weights and summing them up
			for (int i=0; i < inputs.length; i++) {
				this.inputFromPreviousLayer[i] = inputs[i]; // necessary for backpropagtion derivation
				sumOfWeights += inputs[i] * weights[i];
			}
			// firing neuron with sigmoid function
			this.activationValue =  sigmoid(sumOfWeights + this.bias);
			return this.activationValue;
		}
	}
	
	public double getSumOfweights(double[] inputs)  throws Exception {
		if (inputs.length != this.weights.length) {
			throw new Exception("Invalid input dimension");
		} else {
			double sumOfWeights = 0;
			// multiplying all input features with weights and summing them up
			for (int i=0; i < inputs.length; i++) {
				sumOfWeights += inputs[i] * weights[i];
			}
			// firing neuron with sigmoid function
			return sumOfWeights + this.bias;
		}
	}

	private double sigmoid(double value) {
		// converts any value between 0 and 1
		double v = 1 / (1 + Math.exp(-value));
		if (v == Double.POSITIVE_INFINITY) {
			return Double.MAX_VALUE;
		}
		if (v == Double.NEGATIVE_INFINITY) {
			return Double.MIN_VALUE;
		}
		return v;
	}

	public double getWeight(int i) {
		return this.weights[i];
	}

	public void updateWeight(double nudgeBy, int weightIndex) {
		this.weights[weightIndex] -= nudgeBy;
	}

	public void updateBias(double nudgeBy) {
		this.bias -= nudgeBy;
	}
	
	public void setDeivativeOfActivation(double value) {
		if (this.derivativeOfActivation != 0) {
			this.derivativeOfActivation += value;
		} else {
			this.derivativeOfActivation = value;
		}
	}
	
	public double getDeivativeOfActivation() {
		return this.derivativeOfActivation;
	}
	
	@Override
	public String toString() {
		return this.weights.length + "";
	}

	public double getActivationValue() {
		return this.activationValue;
	}

	public int getWeightCount() {
		return this.weights.length;
	}

	public double getInputFromPreviousLayer(int index) {
		return inputFromPreviousLayer[index];
	}

	public void setInputFromPreviousLayer(double inputFromPreviousLayer, int index) {
		this.inputFromPreviousLayer[index] = inputFromPreviousLayer;
	}

	public void setWeight(double d, int weightIndex) {
		this.weights[weightIndex] = d;
	}

	public double getBias() {
		return this.bias;
	}

	public void setBias(double d) {
		this.bias = d;
	}
}
