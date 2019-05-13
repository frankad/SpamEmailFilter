import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class MLEstimator {

	public static void main(String[] args) throws IOException {
		File file = new File("src/d4.txt");
		ArrayList<Double> dataSet = tokenSet(file);
		ArrayList<Double> copy = new ArrayList<Double>();
		copy.addAll(dataSet);
		Collections.sort(copy);
		
		double[] thetas = {copy.get(0), copy.get(copy.size()/2-1), copy.get(copy.size()-1)};
		int index=1;
		double oldlikeVal = 0.0, difference=0.001,
				newLikeVal = loglikeHoodVal(thetas[0], thetas[1], thetas[2], dataSet);
	
		System.out.println("           mu_1     mu_2     mu_3 loglihood");
		while(Math.abs(newLikeVal-oldlikeVal)>=difference){
			System.out.print("["+index+",] "+thetas[0]+" "+thetas[1]+" "+thetas[2]+" "+newLikeVal);
			index++;
			thetas[0] = MStep(thetas[0], thetas[0], thetas[1], thetas[2], dataSet);
			thetas[1] = MStep(thetas[1], thetas[0], thetas[1], thetas[2], dataSet);
			thetas[2] = MStep(thetas[2], thetas[0], thetas[1], thetas[2], dataSet);
			oldlikeVal = newLikeVal;
			newLikeVal = loglikeHoodVal(thetas[0], thetas[1], thetas[2], dataSet);
			System.out.println();
		}
		System.out.print("["+index+",] "+thetas[0]+" "+thetas[1]+" "+thetas[2]+" "+newLikeVal);
		System.out.println();
		System.out.println();
		
		
		System.out.println("         x_i         P(cls 1 | x_i)   P(cls 2 | x_i)   P(cls 3 | x_i)");
		for(int i=0;i<copy.size();i++){
			double temp = dataSet.get(i);
			System.out.print("["+(i+1)+",] "+temp+" ");
			for(int j=0;j<3;j++){
				System.out.print(EStep(temp, thetas[j], thetas[0], thetas[1], thetas[2])+"  ");
			}
			System.out.println();
		}

	}	
	
	public static ArrayList<Double> tokenSet(File filename) throws IOException {
		ArrayList<Double> dataSet = new ArrayList<Double>();
		Scanner filescan = new Scanner(filename);
		while(filescan.hasNextLine() && filescan.hasNext()) {
			double temp = filescan.nextDouble();
			dataSet.add(temp);
		}
		filescan.close();
		return dataSet;
	}	
	
	public static double EStep(double val, double theta, double theta1, double theta2, double theta3){		
		double result = Math.pow(Math.E, -1.0/2*Math.pow(val-theta, 2))/
				(Math.pow(Math.E,-1.0/2*Math.pow(val-theta1, 2))+Math.pow(Math.E,-1.0/2*Math.pow(val-theta2, 2))+Math.pow(Math.E,-1.0/2*Math.pow(val-theta3, 2)));
		return result;
	}		
	
	public static double MStep(double theta, double theta1, 
			           double theta2, double theta3, ArrayList<Double> arr){	
		double sumT=0.0, sumD=0.0;
		for(int i =0;i<arr.size();i++){
			sumT+=EStep(arr.get(i), theta, theta1, theta2, theta3)*arr.get(i);
			sumD+=EStep(arr.get(i), theta, theta1, theta2, theta3);
		}
		return sumT/sumD;
	}	

	public static double loglikeHoodVal(double theta1, 
	           double theta2, double theta3, ArrayList<Double> arr){
		double sum=0.0;
		int n = arr.size();
		for(int i =0;i < n;i++){
			double tem = arr.get(i);
			double e1 =EStep(tem, theta1, theta1, theta2, theta3);
			double e2 =EStep(tem, theta2, theta1, theta2, theta3);
			double e3 =EStep(tem, theta3, theta1, theta2, theta3);
			sum+=-e1*Math.pow(tem-theta1, 2)/2-e2*Math.pow(tem-theta2, 2)/2-e3*Math.pow(tem-theta3, 2)/2;
		}
		sum+=n*Math.log(1.0/3)-n/2.0*Math.log(2*Math.PI);
		return sum;
	}
}
