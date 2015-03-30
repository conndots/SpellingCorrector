import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellingCorrector {
	private HashMap<String, Integer> nWords = new HashMap<String, Integer>();
	public SpellingCorrector(String path) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		Pattern p = Pattern.compile("\\w+");
		for(String temp = reader.readLine(); temp != null; temp = reader.readLine()){
			Matcher m = p.matcher(temp.toLowerCase());
			while(m.find()) 
				nWords.put((temp = m.group()), nWords.containsKey(temp) ? nWords.get(temp) + 1 : 2);
		}
		reader.close();
	}
	private static String alph = "abcdefghijklmnopqrstuvwxyz";
	private HashSet<String> knownEdit1(String word){
		HashSet<String> res = new HashSet<String>();
		ArrayList<Entry<String, String>> splits = new ArrayList<Entry<String, String>>(word.length() + 1);
		for(int i = 0; i < word.length() + 1; i++)//split
			splits.add(new SimpleEntry<String, String>(word.substring(0, i), word.substring(i)));
		for(Entry<String, String> e : splits){
			String a = e.getKey(), b = e.getValue(), temp = null;
			if(b.length() > 0){//deletes & replaces
				temp = a + b.substring(1);
				if(nWords.containsKey(temp))
					res.add(temp);
				for(int i = 0; i < 26; i++){
					temp = a + alph.charAt(i) + b.substring(1);
					if(nWords.containsKey(temp))
						res.add(temp);
				}
				if(b.length() > 1){//transpose
					temp = a + b.charAt(1) + b.charAt(0) + b.substring(2);
					if(nWords.containsKey(temp))
						res.add(temp);
				}
			}
			for(int i = 0; i < 26; i++){//insert
				temp = a + alph.charAt(i) + b;
				if(nWords.containsKey(temp))
					res.add(temp);
			}
		}
		return res;
	}
	private HashSet<String> knownEdits2(String word){
		HashSet<String> res = new HashSet<String>();
		HashSet<String> edit1Res = knownEdit1(word);
		for(String s : edit1Res)
			for(String ss : knownEdit1(s))
				res.add(ss);
		return res;
	}
	public String correct(String word){
		if(nWords.containsKey(word))
			return word;
		HashSet<String> candidate = knownEdit1(word);
		if(candidate.size() == 0)
			candidate = knownEdits2(word);
		if(candidate.size() == 0)
			return word;
		PriorityQueue<String> pq = new PriorityQueue<String>(candidate.size(), new Comparator<String>(){
			public int compare(String arg0, String arg1) {
				int a = nWords.get(arg0), b = nWords.get(arg1);
				if(a > b)	return -1;
				if(a < b)	return 1;
				return 0;
			}
		});
		for(String s : candidate)
			pq.add(s);
		return pq.peek();
	}
	public static void main(String[] args)throws IOException {
		SpellingCorrector sc = new SpellingCorrector("big.txt");
		while(true){
			System.out.println("Please input your word:");
			byte[] b = new byte[100];
			int n = System.in.read(b);
			String temp = new String(b, 0, n);
			temp = temp.substring(0, temp.length() - 2);
			if(temp == "!q")
				break;
			System.out.println("Do you mean " + sc.correct(temp) + "?");
		}
	}

}
