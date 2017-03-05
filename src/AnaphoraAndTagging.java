import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.IntPair;

public class AnaphoraAndTagging {
	//Regular Expression constants for End of Sentence detection
	static final String PREFIXES = "(Mt|Mr|St|Mrs|Ms|Dr|Col|Capt|Hon|Maj|Prof|Pres|Sr|Jr|Gen|Genl|Sgt|Lt|Maj|Ave|Jn)([.])";
	static final String WEBSITES = "([.])(edu|com|net|org|io|gov)";
	static final String CAPS = "([A-Z])";
	static final String SUFFIXES = "(etc|Inc|Ltd|Jr|Sr|Co)";
	static final String STARTERS = "(The|Mr|Mrs|Ms|Dr|He\\s|She\\s|It\\s|They\\s|Their\\s|Our\\s|We\\s|But\\s|However\\s|That\\s|This\\s|Wherever)";
	static final String ACRONYMS = "([A-Z][.][A-Z][.](?:[A-Z][.])?)";
	static final String DIGITS = "([0-9])";
	
	//Function to split the input string into sentences
	static String[] getSentences(String input) {
		//The non-EOS periods are replaced by a stopper <prd>
		input = input.replaceAll(PREFIXES, "$1<prd>");
		input = input.replaceAll(WEBSITES, "<prd>$2");
		input = input.replaceAll("\\s" + CAPS + "([.]) "," $1<prd> ");
		input = input.replaceAll(ACRONYMS+" "+STARTERS,"$1<stop> $2");
		input = input.replaceAll(CAPS + "[.]" + CAPS + "[.]" + CAPS + "[.]","$1<prd>$2<prd>$3<prd>");
		input = input.replaceAll(CAPS + "[.]" + CAPS + "([.])","$1<prd>$2<prd>");
		input = input.replaceAll(" "+SUFFIXES+"[.] "+STARTERS," $1<stop> $2");
		input = input.replaceAll(" "+SUFFIXES+"([.])"," $1<prd>");
		input = input.replaceAll(DIGITS + "[.]" + DIGITS,"$1<prd>$2");
		input = input.replaceAll(" " + CAPS + "[.]"," $1<prd>");
		//Paragraph splits are replaced by a stopper <para>
		input = input.replaceAll("\n","<para>");
		//Periods, Exclamation and Question marks inside quotes are moved outside
		input = input.replaceAll("[.]\"","\".");
		input = input.replaceAll("\\?\"","\"?");
		input = input.replaceAll("!\"","\"!");
		//All the Periods, Exclamation and Question marks are replaced by a stopper <stop>
		input = input.replaceAll("[.]",".<stop>");
		input = input.replaceAll("<para>","<para>");
		input = input.replaceAll("\\?","?<stop>");
		input = input.replaceAll("!","!<stop>");
		//The <prd> stoppers are replaced by periods
		input = input.replaceAll("<prd>",".");
		input = input.replaceAll("<para>","");
		//The text is split using <prd> as the delimiter
		String sentences[] = input.split("<stop>");
		for(int i=0;i<sentences.length;i++)
			sentences[i] = sentences[i].trim();
		return sentences;
	}
			
	public static void createAnaphoraFile(String fileContents, int aId) {
		Annotation document = new Annotation(fileContents);
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		pipeline.annotate(document);
		File  fileText = new File("C:\\Users\\User\\Desktop\\8th Semester\\Project\\NewsHtmlFiles\\"+aId+"-anaphora.txt");
		Boolean fileCreated = false;
		try {
			fileCreated = fileText.createNewFile();
			if(fileCreated) {
				PrintWriter writer = new PrintWriter(fileText, "UTF-8");
				for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
					writer.println("--start--");
					Map<IntPair, Set<CorefMention>> map = cc.getMentionMap();
					writer.println(cc.getRepresentativeMention());
					for (Entry<IntPair, Set<CorefMention>> entry : map.entrySet()) {
						writer.println(entry.getValue());
					}
					writer.println("--end--");
				}
				writer.close();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public static void resolvePronoun() throws Exception {
		String text = "";
		int i=0;
		for(i=1;i<600;i++) {
			text = "";
			File  fileText = new File("C:\\Users\\User\\Desktop\\8th Semester\\Project\\NewsHtmlFiles\\"+i+".txt");
			if(fileText.exists()) {
				FileInputStream fileInputStream = new FileInputStream(fileText);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8"); 
				BufferedReader in = new BufferedReader(inputStreamReader);
				String line;
				while((line = in.readLine()) != null) {
					text = text + line;
				}
				createAnaphoraFile(text,i);
				in.close();
			}
			String[] sentences = getSentences(text);
			fileText = new File("C:\\Users\\User\\Desktop\\8th Semester\\Project\\NewsHtmlFiles\\" + i + "-anaphora.txt");
			if(fileText.exists()) {
				FileInputStream fileInputStream = new FileInputStream(fileText);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8"); 
				BufferedReader in = new BufferedReader(inputStreamReader);
				String line;
				String rep = "";
				String placeholder = "";
				int lineNo;
				int lineLength;
				while((line = in.readLine()) != null) {
					if(line.equals("--start--")) {
						line = in.readLine();
						rep = line.substring(1,line.indexOf("\"", 1));
						while(!(line = in.readLine()).equals("--end--")) {
							placeholder = line.substring(2,line.indexOf("\"", 2));
							lineLength = line.length();
							lineNo = Integer.parseInt(line.substring(line.lastIndexOf(" ")+1,lineLength-1));
							if(lineNo>0 && lineNo<=sentences.length)
								sentences[lineNo-1] = sentences[lineNo-1].replaceAll("\\b"+Pattern.quote(placeholder)+"\\b", Matcher.quoteReplacement(rep));
					    }
					}
				}
				File outputFile = new File("C:\\Users\\User\\Desktop\\8th Semester\\Project\\NewsHtmlFiles\\" + i + "-resolved.txt");
				outputFile.createNewFile();
				PrintWriter printWriter = new PrintWriter(outputFile, "UTF-8");
				for(String s: sentences) {
					printWriter.print(s);
				}
				printWriter.close();
				in.close();
			}
		}
	}
	
	private boolean isNumber(final String str) {
        return str.matches("[0-9.]");
    }
    private List<String> loadStopWords(String filePath) throws FileNotFoundException, IOException {
        if (filePath == null || filePath.trim().length() == 0) {
            filePath = "FoxStoplist.txt";
        }
        final List<String> stopWords = new ArrayList<String>();
        final BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath)));
        try {
            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith("#")) { //add the line which is not a comment
                    stopWords.add(line);
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return stopWords;
    }

    private List<String> separateWords(final String text, final int minimumWordReturnSize) {
        final List<String> separateWords = new ArrayList<String>();
        final String[] words = text.split("[^a-zA-Z0-9_\\+\\-/]");
        if (words != null && words.length > 0) {
            for (final String word : words) {
                String wordLowerCase = word.trim().toLowerCase();
                if (wordLowerCase.length() > 0 && wordLowerCase.length() > minimumWordReturnSize &&
                        !isNumber(wordLowerCase)) {
                    separateWords.add(wordLowerCase);
                }
            }
        }
        return separateWords;
    }

    private List<String> splitSentences(final String text) {
        final String[] sentences = text.split("[.!?,;:\\t\\\\-\\\\\"\\\\(\\\\)\\\\\\'\\u2019\\u2013]");
        if (sentences != null) {
            return new ArrayList<String>(Arrays.asList(sentences));
        } 
        else {
            return new ArrayList<String>();
        }
    }

    private Pattern buildStopWordRegex(final String stopWordFilePath) throws IOException {
        final List<String> stopWords = loadStopWords(stopWordFilePath);
        final StringBuilder stopWordPatternBuilder = new StringBuilder();
        int count = 0;
        for(final String stopWord: stopWords) {
            if (count++ != 0) {
                stopWordPatternBuilder.append("|");
            }
            stopWordPatternBuilder.append("\\b").append(stopWord).append("\\b");
        }
        return Pattern.compile(stopWordPatternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    private List<String> generateCandidateKeywords(List<String> sentenceList, Pattern stopWordPattern) {
        final List<String> phraseList = new ArrayList<String>();
        for (final String sentence : sentenceList) {
            final String sentenceWithoutStopWord = stopWordPattern.matcher(sentence).replaceAll("|");
            final String[] phrases = sentenceWithoutStopWord.split("\\|");
            if (null != phrases && phrases.length > 0) {
                for(final String phrase : phrases) {
                    if (phrase.trim().toLowerCase().length() > 0) {
                        phraseList.add(phrase.trim().toLowerCase());
                    }
                }
            }
        }
        return phraseList;
    }

    private Map<String,Double> calculateWordScores(List<String> phraseList) {
        final Map<String, Integer> wordFrequency = new HashMap<String, Integer>();
        final Map<String, Integer> wordDegree = new HashMap<String, Integer>();
        final Map<String, Double> wordScore = new HashMap<String, Double>();
        for (final String phrase : phraseList) {
            final List<String> wordList = separateWords(phrase, 0);
            final int wordListLength = wordList.size();
            final int wordListDegree = wordListLength - 1;
            for (final String word : wordList) {
               if (!wordFrequency.containsKey(word)) {
                   wordFrequency.put(word, 0);
               }
               if (!wordDegree.containsKey(word)) {
                   wordDegree.put(word, 0);
               }
               wordFrequency.put(word, wordFrequency.get(word) + 1);
               wordDegree.put(word, wordDegree.get(word) + wordListDegree);
            }
        }
        final Iterator<String> wordIterator = wordFrequency.keySet().iterator();
        while (wordIterator.hasNext()) {
            final String word = wordIterator.next();
            wordDegree.put(word, wordDegree.get(word) + wordFrequency.get(word));
            if (!wordScore.containsKey(word)) {
                wordScore.put(word, 0.0);
            }
            wordScore.put(word, wordDegree.get(word) / (wordFrequency.get(word) * 1.0));
        }
        return wordScore;
    }

    public Map<String, Double> generateCandidateKeywordScores(List<String> phraseList,
                                                               Map<String, Double> wordScore) {
        final Map<String, Double> keyWordCandidates = new HashMap<String, Double>();
        for (String phrase : phraseList) {
            final List<String> wordList = separateWords(phrase, 0);
            double candidateScore = 0;
            for (final String word : wordList) {
                candidateScore += wordScore.get(word);
            }
            keyWordCandidates.put(phrase, candidateScore);
        }
        return keyWordCandidates;
    }

    private LinkedHashMap<String, Double> sortKeyWordCandidates
            (Map<String,Double> keywordCandidates) {
        final LinkedHashMap<String, Double> sortedKeyWordCandidates = new LinkedHashMap<String, Double>();
        int totaKeyWordCandidates = keywordCandidates.size();
        final List<Map.Entry<String, Double>> keyWordCandidatesAsList =
                new LinkedList<Map.Entry<String, Double>>(keywordCandidates.entrySet());
        Collections.sort(keyWordCandidatesAsList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Double>)o2).getValue()
                        .compareTo(((Map.Entry<String, Double>)o1).getValue());
            }
        });
        totaKeyWordCandidates = totaKeyWordCandidates / 3;
        for(final Map.Entry<String, Double> entry : keyWordCandidatesAsList) {
            sortedKeyWordCandidates.put(entry.getKey(), entry.getValue());
            if (--totaKeyWordCandidates == 0) {
                break;
            }
        }
        return sortedKeyWordCandidates;
    }
	
    public static void tagArticles() throws IOException {
        String text="";
        final String stopPath = "Stoplist.txt";
        final AnaphoraAndTagging rakeInstance = new AnaphoraAndTagging();
        final Pattern stopWordPattern = rakeInstance.buildStopWordRegex(stopPath);
        BufferedReader in = null;
        String tagText = "";
        DbConnector connector = new DbConnector();
        for(int i=1;i<600;i++) {
        	text="";
        	File file = new File("C:\\Users\\User\\Desktop\\8th Semester\\Project\\NewsHtmlFiles\\" + i + "-resolved.txt");
			if(file.exists()) {
		        in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		        String line;
		        while((line = in.readLine()) != null) {
		            text = text + line;
		        }
		        in.close();
		        final List<String> sentenceList = rakeInstance.splitSentences(text);
		        final List<String> phraseList = rakeInstance.generateCandidateKeywords(sentenceList, stopWordPattern);
		        final Map<String, Double> wordScore = rakeInstance.calculateWordScores(phraseList);
		        final Map<String, Double> keywordCandidates = rakeInstance.generateCandidateKeywordScores(phraseList, wordScore);
		        Map<String, Double> sortedCandidates = rakeInstance.sortKeyWordCandidates(keywordCandidates);
		        int j=0;
		        tagText = "";
		        for (Map.Entry<String, Double> entry : sortedCandidates.entrySet())
		        {
		        	++j;
		        	if(j<3) {
		        		tagText = tagText + entry.getKey() + ", ";
		        	}
		        }
		        if(tagText.length()>2)
		        	tagText = tagText.substring(0, tagText.length()-2);
		        connector.addTags(tagText, i); 
			}
        }
    }
    
	public static void main(String[] args) {
		try {
			resolvePronoun();
			tagArticles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
