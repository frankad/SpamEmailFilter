import java.io.*; 
import java.util.*;  
import java.util.HashSet; 
import java.util.Scanner;

public class NaiveBayes {
 		
	//private static File [] ListOfTestFiles;  
	//private static File [] spamFileFromTest;   
	   
	//	This function reads in a file and returns a 
	//	set of all the tokens. It ignores the subject line
	//
	//	If the email had the following content:
	//
	//	Subject: Get rid of your student loans
	//	Hi there ,
	//	If you work for us, we will give you money 
	//	to repay your student loans . You will be 
	//	debt free !
	//	FakePerson_22393
	//
	//	This function would return to you
	//	[hi, be, student, for, your, rid, we, get, of, free, if, you, us, give, !, repay, will, loans, work, fakeperson_22393, ,, ., money, there, to, debt]
	public static HashSet<String> tokenSet(File filename) throws IOException {
		HashSet<String> tokens = new HashSet<String>();
		Scanner filescan = new Scanner(filename);
		filescan.next(); //Ignoring "Subject"
		while(filescan.hasNextLine() && filescan.hasNext()) {
			tokens.add(filescan.next());
		}
		filescan.close();
		return tokens;
	}
	/**
	 * Method that process a file and store in a map. So, each word maps to its count or frequency
	 * @param filename the name of the file 
	 * @throws IOException
	 */
	public static void WordMapToCount(Map<String, Integer> mapWordFrequncy, File filename) throws IOException{  
		
		//tokenizing the file or change the file in to a set of list of words  
		HashSet<String> setOfwords = tokenSet(filename);   
        for(String word: setOfwords){
        	if(mapWordFrequncy.containsKey(word)){ 
        		mapWordFrequncy.put(word, mapWordFrequncy.get(word)+ 1);  
        		}else{
        			mapWordFrequncy.put(word, 1); 
        		}	
        	} 
           	
	}
	/**
	 * This method generate a map that contain a word key and probabilty value
	 * @param directoryPath the directory path
	 * @return a map the maps word to its probability
	 * @throws IOException
	 */
	public static Map<String, Double> wordProbability (File directoryPath) throws IOException{
		// a map that holds word and its frequency
	    Map<String, Integer> mapWordFrequncy = new HashMap<String, Integer>(); 
	    //the number of email is equal to the number of file in the given directory.
	    
        int numEmail = directoryPath.listFiles().length;
		
        //System.out.println(arrOfFiles.length);
        //iterate through each file and pass in to WordMapToCount to get word to count map
        for (File eachFileName: directoryPath.listFiles()){         	
        		WordMapToCount(mapWordFrequncy, eachFileName);    
        }
        
       //Make each word maps to its corresponding probability
        Map<String, Double> wordMapProba = new HashMap<String, Double>();
        for(String key : mapWordFrequncy.keySet()){
        	
        	//calculate the probability of each word
        	double probaWord = 1.0*(mapWordFrequncy.get(key) + 1) /(1.0*numEmail + 2.0);  
        	//the store the word or key with the corresponding value(probability of that word);
        	wordMapProba.put(key, probaWord);     	    	    
    	}
        //return word to probability map
        return wordMapProba; 
        
	}
//	public static int sizeOfHamEmail(File pathHam){
//		return pathHam.listFiles().length;
//	}
//	public static int sizeOfSpmamEmail(File pathSpam){
//		System.out.println(pathSpam.listFiles().length);
//		return pathSpam.listFiles().length;  
//	}
	/**
	 * This method classify the email inside the test file as ham or spam based the probability value  
	 * @param directoryTestPath test folder path
	 * @param pathSpam the spam folder path in the train folder 
	 * @param pathHam the ham folder path in the train folder
	 * @throws IOException
	 */
	public static void emailClassification(File directoryTestPath, File pathSpam, File pathHam) throws IOException{ 
		//each word in the spam file maps to its probability
		Map<String, Double> spamWordProba = wordProbability(pathSpam); 
		//each word in the ham file maps to its probability
		Map<String, Double> hamWordProba = wordProbability(pathHam); 
		//the number of spam and ham email
		
		//number of spam and ham email's   
		int numHam =  pathHam.listFiles().length;		
		int numSpam = pathSpam.listFiles().length;
		//System.out.println(numSpam);
		 
		//get the probability of ham and spam emails from its count or size.
		double prHam = (1.0*numHam)/ (1.0*numHam + 1.0*numSpam ); 
		double prSpam = (1.0*numSpam)/ (1.0*numHam + 1.0*numSpam );
		
		//to make the probability of ham and spam email not under flow use log
		prHam = Math.log(prHam); 		
		prSpam = Math.log(prSpam);   
		
		//iterate through the test file and classify as ham or spam 
		//File [] testFiles = directoryTestPath.listFiles();  
		for (File eachFile: directoryTestPath.listFiles()){  
			double hamProbab = prHam;
			double spamProbab = prSpam;	 
			// process the word in each file using scanner   
			Scanner scan = new Scanner(eachFile);   
			scan.next();
			while(scan.hasNextLine() && scan.hasNext()) { 
				//get each word
				String newWord = scan.next();
				
				//check if this word is found in the ham/spam email. Other wise ignore the word if we haven't
				//seen in the labeled training data. if we find calculate its corresponding probability.
					if(spamWordProba.containsKey(newWord)){
						spamProbab +=spamWordProba.get(newWord);
					}
			        else{
			        	spamProbab += Math.log(1.0/(1.0*numSpam+2.0));   
					}
					if(hamWordProba.containsKey(newWord)){
						hamProbab+=hamWordProba.get(newWord);  
					}
					else{
						hamProbab+=Math.log(1.0/(1.0*numHam+ 2.0));
					}
								
			 } 
			 scan.close(); 
			 // replace this path 
			 String emailName = eachFile.toString().replace("src\\data\\test\\", ""); 
			 // if the probability of ham is greater than probability of ham write the email name and its classification ham 
			 //in the out put text file (result). 
			 //PrintStream output = new PrintStream(new File("result.txt")); 
			 if (hamProbab > spamProbab) {     
				 System.out.println(emailName + " ham");
				 
			 }else{   //other wise print spam 
				 System.out.println(emailName + " spam");
			 }
		 }    
	} 
	
	/** 
	 *  
	 * @return
	 * @throws FileNotFoundException
	 */
	
	public static void main(String[] args) throws IOException {
		
		//TODO: Implement the Naive Bayes Classifier "src/data/train/ham"
		File hamPath = new File("src/data/train/ham");
		File spamPath = new File("src/data/train/spam");
		File testPath = new File("src/data/test");
		//"C:/Users/New/NewEclipse/CSE312Program/src/data/train/ham"		
		emailClassification(testPath, spamPath, hamPath);
         
	}
}
