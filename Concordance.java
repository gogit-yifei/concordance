import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.text.BreakIterator;
import java.util.Locale;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
* The Concordance problem solution
*
* @author  Yifei
* @version 1.0
* @since   2015-09-04 
*/
public class Concordance {
	/**
	 * Class constructor
	 */
	// public Concordance() {
	// }

	/**
	 * Return a String which is the entire content of the file opened. 
	 * 
	 * @param   fileName   a String giving the name and path of the file
	 * @return             the String containing the entire content
	 */
	private String readFile(String fileName) {
		String result = "";
		try (BufferedReader br = 
				new BufferedReader(new FileReader(fileName))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			result = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Return a List of Strings got from parsing the contents.
	 * 
	 * @param   str   a String giving the content 
	 * @return        a list containing all the sentences
	 */
	private List<String> parse(String str) {
		List<String> list = new ArrayList<String>();
		
		// using a sentence instance to parse the paragraph
		// to sentences
		BreakIterator iterator = 
			BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(str);
		int start = iterator.first();
		for (int end = iterator.next();
				end != BreakIterator.DONE;
				start = end, end = iterator.next()) {
			list.add(str.substring(start,end));
		}
		return list;
	}

	/**
	 * Return a Map whose key is the token in the sentence and 
	 * the value is a List of Integer which is the occurances 
	 * of the corresponding key.
	 * 
	 * @param   sentences  a List of Strings which are the sentences
	 * @return             a map with the tokens and their occurances' places
	 */
	private Map<String, List<Integer>> tokenize(List<String> sentences) {
		Map<String, List<Integer>> map = 
			new HashMap<String, List<Integer>>();
		int index = 1;
		for (String str: sentences) {
			// eliminate all punctuations and other non-related characters
			// only numbers, letters, points and spaces are left
			str = str.replaceAll("[^0-9a-zA-Z. ]", "");
			BreakIterator wordIterator = 
				BreakIterator.getWordInstance(Locale.US);
			wordIterator.setText(str);
			int start = wordIterator.first();
			for (int end = wordIterator.next();
					end != BreakIterator.DONE;
					start = end, end = wordIterator.next()) {
				String token = str.substring(start, end).toLowerCase();
				if (!Character.isLetterOrDigit(token.charAt(0))) {
					// a valid word may only begin with numbers or letters
		 			continue;
		 		}
				if(map.containsKey(token)) {
					map.get(token).add(index);
				} else {
					List<Integer> sentenceIndex = new ArrayList<Integer>();
					sentenceIndex.add(index);
					map.put(token, sentenceIndex);
				}
			}
			index++;
		}
		return map;
	}

	/**
	 * Print the output onto the screen with a format. 
	 * 
	 * @param   map   a map containing the result to print
	 */
	private void printOutput(Map<String, List<Integer>> map) {
		int index = 0;
		for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
			List<Integer> list = entry.getValue();
			String key = entry.getKey();
			String serial = getSerial(index);
			String occurance = " {" + entry.getValue().size() + ":";
			for(int i = 0; i < list.size(); i++) {
				occurance += list.get(i);
				if(i == list.size()-1) {
					occurance += "}";
				} else {
					occurance += ",";
				}
			}
			System.out.println(serial + key + occurance);
			index++;
		}
	}

	/**
	 * Write the output into a file. 
	 * 
	 * @param   map   a map containing the result to print
	 */
	private void writeToFile(Map<String, List<Integer>> map) {
		try {
			File file = new File("output.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			int index = 0;
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
				List<Integer> list = entry.getValue();
				String key = entry.getKey();
				String serial = getSerial(index);
				String occurance = " {" + entry.getValue().size() + ":";
				for(int i = 0; i < list.size(); i++) {
					occurance += list.get(i);
					if(i == list.size()-1) {
						occurance += "}";
					} else {
						occurance += ",";
					}
				}
				String content = serial + key + occurance + "\n";
				bw.write(content);
				index++;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Return a String which is the serial 
	 * of the corresponding token
	 * 
	 * @param   index   a number representing the order of the key
	 * @return 					the serial
	 */
	private String getSerial(int index) {
		// the serial must be duplicates of a certain letter
		char baseChar = (char) (index % 26 + 'a');
		StringBuilder sb = new StringBuilder();
		sb.append(baseChar);
		// duplicate several times
		while (index - 26 >= 0) {
			sb.append(baseChar);
			index -= 26;
		}
		return sb.toString() + ". ";
	}


	/**
	 * Main function 
	 * 
	 * @param   args   could be a string indicating the file name
	 */
	public static void main(String[] args) {
		String fileName = "test.txt";
		
		Concordance concordance = new Concordance();
		
		if (args.length > 0) {
			fileName = args[0];
		}
		
		String content = concordance.readFile(fileName);
		
		List<String> sentences = concordance.parse(content);
		
		// using a treeMap to sort the result
		Map<String, List<Integer>> treeMap = 
				new TreeMap<String, List<Integer>>(
				concordance.tokenize(sentences));
		
		concordance.printOutput(treeMap);
		concordance.writeToFile(treeMap);
	}
}

