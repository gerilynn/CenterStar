package HW5;
//using Global Alignment from HW2, modified.
//NOTE alpha and beta must be <100
//alpha is cost of delete(mismatch), beta is gap cost.

//GERI, if you come back to this, see if you can figure out alpha and beta, maybe! Then see if you can get the bonus. 
//Oh, wait, you still have to do the alignment by hand :(
//ALSO, doesn't print out final letter of the string. And, I think it might not be in the matrix either!!!  

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CenterStar {	
	public static void main(String[] args) throws FileNotFoundException{
		int alpha = Integer.parseInt(args[0]);
		int beta = Integer.parseInt(args[1]);
		String fileName = "fasta5.txt";
		
		//getting num of strings and making them into an array of strings
		int numStrings = getNumOfStringsFromFile(fileName);
		String[] allStringsFromFile = new String[numStrings];
		allStringsFromFile = getStringArrayFromFile(fileName, numStrings);
		
		//each cell of the distance matris contains: [0]&[1]optimal alignment AND [2]distance score
		String[][][] alignmentMatrix = new String[numStrings][numStrings][3];
				
		//filling in global alignment matrix.
		//pairwise distances between all strings
		makeDistanceMatrix(alpha, beta, numStrings, alignmentMatrix, allStringsFromFile);
		int centerString = findCenterSequence(allStringsFromFile, alignmentMatrix); //equal distances chooses the lower indexed string.
		String[] finalAlignment = weaveAlignments(allStringsFromFile, alignmentMatrix, centerString);
	}

	//global alignment from HW2, but modified to use DISTANCE rather than alignment score

	public static void makeDistanceMatrix(int alpha, int beta, int numStrings, String[][][] alignmentMatrix, String[] allStrings){
		String bothAlignments = "";
		for(int x = 0; x < numStrings; x++) {
			for(int y = x+1; y < numStrings; y++) {
				int[][] V = fillV(x, y, alpha, beta, allStrings);
				bothAlignments = traceback(V, x, y, alpha, beta, allStrings);
				System.out.println("for S" + x + ", S" + y);
				//geri, TODO if time, find out why there are empty boxes in my alignment strings, and re-index accordingly.
				alignmentMatrix[x][y][0] = bothAlignments.substring(1, bothAlignments.indexOf('$')-1);
				alignmentMatrix[x][y][1] = bothAlignments.substring(bothAlignments.indexOf('$')+2, bothAlignments.length()-1);
				alignmentMatrix[x][y][2] = V[allStrings[x].length()][allStrings[y].length()] + "";
			
				System.out.println(alignmentMatrix[x][y][0] + '\n' + alignmentMatrix[x][y][1]);
				System.out.println("distance = " + alignmentMatrix[x][y][2]);	
			}
		}
	}
	
	public static int findCenterSequence(String[] allStrings, String[][][]alignmentMatrix){
		//Calculate distance from string x to all other strings:
		//adding row and col values
		int numStrings = allStrings.length;
		int[] sumOfDistances = new int[numStrings];
		int total = 0;
		int minSum = numStrings*100;
		int indexOfMinSum = numStrings*2;
		
		for(int x = 0; x < numStrings; x++) {
			total = 0;
			for(int y = 0; y < numStrings; y++) {
				if(alignmentMatrix[x][y][2] != null) {//add the row
					total += Integer.parseInt(alignmentMatrix[x][y][2]);
				}
				if(alignmentMatrix[y][x][2] != null) {//add the column
					total += Integer.parseInt(alignmentMatrix[y][x][2]);					
				}
			}
			
			sumOfDistances[x] = total;
			if(total < minSum) {
				minSum = total;
				indexOfMinSum = x;
			}
		System.out.println("Sum of distances to S" +x + "= " + total);
		}
		System.out.println("Center string: S" + indexOfMinSum);
		return indexOfMinSum;
	}
	
	public static String[] weaveAlignments(String[] allStrings, String[][][] alignmentMatrix, int centerString) {
		String[] finalAlignment = new String[allStrings.length];
		
		
		
		return(finalAlignment);
	}
	
	public static int[][] fillV(int x, int y, int alpha, int beta, String[] allStrings) {
		int n = allStrings[x].length()+1;// +1 to leave room for empty string
		int m = allStrings[y].length()+1;
		int distance = 0;
		int[][]V = new int[n][m];
		
		//filling the matrix
		//initializing first row and column
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) { 
				distance = 0;
				if (i == 0) {
					V[i][j]= j*beta;
					System.out.print(j*beta + " ");
					continue;
				}
				if (j == 0) {
					V[i][j] = i*beta;
					System.out.print(i*beta + " ");
					continue;
				}
				else if (allStrings[x].charAt(i-1) != allStrings[y].charAt(j-1)) {
					distance = alpha;
				}
				V[i][j] = minOf3((V[i - 1][j - 1] + distance), (V[i - 1][j] + alpha), (V[i][j - 1]) + alpha);
				System.out.print(V[i][j] + " ");
			}
			System.out.println();
		}
		return V;
	}

	public static String traceback(int[][] V, int x, int y, int alpha, int beta, String[]allStrings) {
		String seq1 = allStrings[x];
		String seq2 = allStrings[y];
		int maxLen = V.length + V[0].length;
		char[] S1 = new char[maxLen];
		char[] S2 = new char[maxLen];
		int i = V.length -1;
		int j = V[0].length -1;
		int index = 0;
		String bothStrings = "";
		while(i != 0 && j !=0){  //if i or j == 0 I need to step into a different calculation mode!
			if((V[i][j] == V[i-1][j-1]) && (seq1.charAt(i-1)==seq2.charAt(j-1))){  //first see if the letters match!, then do the math to trace!
				S1[index]= seq1.charAt(i-1);
				S2[index] = seq2.charAt(j-1);
				j--;
				i--;
			}
			else if(V[i][j]- alpha == V[i-1][j-1]) {//(the second cases of diag arrow)
				S1[index]= seq1.charAt(i-1);
				S2[index] = seq2.charAt(j-1);
				j--;
				i--;
			}
			else if(V[i][j] -beta == V[i-1][j]) {//(up arrow)
				S1[index] = seq1.charAt(i-1);
				S2[index] = '_';//i-1 because matrix and string indices are off by 1
				i--;
			}
			else if(V[i][j]-beta == V[i][j-1]){//(left arrow)
				S1[index] = '_';
				S2[index] = seq2.charAt(j-1);
				j--;
			}
			else {
				System.out.println("Some of the possibilities evaded me!");
			}
			index++;
		}
		
		
		//here's where I traceback along the columns or rows.
		while(i==0 ^ j==0) {
			if(i==0) {
				S1[index] = '_';
				S2[index] = seq2.charAt(j-1);
				j--;
			}
			else if(j==0) {
				S1[index] = seq1.charAt(i-1);
				S2[index] = '_';
				i--;
			}	
			if (i==0 && j==0) {
				break;
			}
			index++;
		}
		System.out.print("optimal alignment:");
		//I built my array from the bottom right, so I need to print it in reverse:
		for(int character = index; character >= 0; character--) {
			bothStrings += S1[character];	
		}
		bothStrings += '$';
		for(int character = index; character >= 0; character--) {
			bothStrings += S2[character];		
		}
		return(bothStrings);
	}

	private static String[] getStringArrayFromFile(String fileName, int numStrings) throws FileNotFoundException{
		
		String[]allStrings = new String[numStrings];
		File fasta = new File(fileName);
		Scanner scanner = new Scanner(fasta);
		String line = "";
		int arrayIndex = 0;
		
		while(scanner.hasNext()) { 
			line = scanner.nextLine();
			if (line == "" || line.contains(">")) {
				line = scanner.nextLine();
			}
			allStrings[arrayIndex] = line;
			arrayIndex++;
		}
		scanner.close();
		return (allStrings);
	}
	
	private static int getNumOfStringsFromFile(String fileName)throws FileNotFoundException{
		File fasta = new File(fileName);
		Scanner scanner = new Scanner(fasta);
		int count = 0;
		String line = "";
		while(scanner.hasNext()) {
			line = scanner.nextLine();
			if(line.contains(">")) {
				count++;
			}
		}
		scanner.close();
		return count;
	}
	
	public static int minOf3(int a, int b, int c) {
		if(a<=b && a<=c) {
			return a;
		}
		else if(b<=c) {
			return b;
		}
		else {
			return c;
		}
	}
}