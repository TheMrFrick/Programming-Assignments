package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile
	 *            Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException
	 *             If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap<String, Occurrence> keywords = new HashMap<>(1000, 2.0f);
		Scanner sc2 = null;
		try {
			sc2 = new Scanner(new File(docFile));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		}
		while (sc2.hasNextLine()) {
			Scanner s2 = new Scanner(sc2.nextLine());
			while (s2.hasNext()) {
				String s = s2.next();
//				System.out.println(s);
				s = getKeyword(s);
//				System.out.println(s);
				boolean alreadyIn = false;
				Iterator it = keywords.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					if (pair.getKey().equals(s)) {
						alreadyIn = true;
//						System.out.println("Key equals s");
						Occurrence occ = (Occurrence) pair.getValue();
						occ.frequency = occ.frequency + 1;
						pair.setValue(occ);
					}
				}
				if (s != null && !alreadyIn) {
					keywords.put(s, new Occurrence(docFile, 1));
				}
			}
		}
		System.out.println(keywords.size());
		System.out.println(keywords.toString());
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return keywords;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws
	 *            Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		// DETERMINE IF WORD IS IN SET ALREADY

		for (String x : kws.keySet()) {
			// IF IN KEYWORDINDEX
			if (keywordsIndex.containsKey(x)) {
				keywordsIndex.get(x).add((Occurrence) kws.get(x));
				insertLastOccurrence(keywordsIndex.get(x));
			} else { // NOT IN THE KEYWORD INDEX
				ArrayList<Occurrence> temp = new ArrayList<>();
				temp.add((Occurrence) kws.get(x));
				keywordsIndex.put(x, temp);
			}
		}
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation, consists only of alphabetic letters, and is not a
	 * noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word
	 *            Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		// DETERMINING IF THERE ARE ANY PUNCTUATION IN THE WORD
		if (word == null) {
			return null;
		}
		int indexNonAlpha = -1;
		word = word.trim();
		for (int i = 0; i < word.length(); i++) {
			if (!Character.isAlphabetic(word.charAt(i))) {
				indexNonAlpha = i;
				break;
			}
		}
//		System.out.println(indexNonAlpha);
		if (indexNonAlpha != -1) {
			for (int i = indexNonAlpha; i < word.length(); i++) {
				if (Character.isAlphabetic(word.charAt(i))) {
					return null; // can't strip
				}
			}
			word = word.substring(0, indexNonAlpha);
//			System.out.println("substring word: " + word);
		}
		if (word.equals("")) {
			return null;
		}

		// DETERMINING IF WORD IS A NOISE WORD
		if (!noiseWords.contains(word)) {
			return word.toLowerCase();
		}

		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return null;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs
	 *            List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> temp = new ArrayList<>();

		if (occs.size() == 1) {
			return null;
		}

		int lo = 0;
		int hi = occs.size() - 2;
		int target = occs.get(occs.size() - 1).frequency;
		int mid = (hi + lo) / 2;

		while (hi >= lo) {
			temp.add(mid);
			if (occs.get(mid).frequency == target) {
				break;
			}
			if (occs.get(mid).frequency < target) {
				lo = mid + 1;
			}
			if (occs.get(mid).frequency > target) {
				hi = mid - 1;
			}
			mid = (hi + lo) / 2;
		}

		// possible that mid is larger or lower than target
		if (occs.get(mid).frequency > target) {
			occs.add(mid + 1, occs.get(occs.size() - 1));
		} else {
			occs.add(mid, occs.get(occs.size() - 1));
		}

		// remove extra
		occs.remove(occs.size() - 1);

		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return temp;
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile
	 *            Name of file that has a list of all the document file names, one
	 *            name per line
	 * @param noiseWordsFile
	 *            Name of file that has a list of noise words, one noise word per
	 *            line
	 * @throws FileNotFoundException
	 *             If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies. (Note that a matching document will only appear once in
	 * the result.) Ties in frequency values are broken in favor of the first
	 * keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in
	 * the result. The result set is limited to 5 entries. If there are no matches
	 * at all, result is null.
	 * 
	 * @param kw1
	 *            First keyword
	 * @param kw1
	 *            Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		// case that they both are false
		ArrayList<String> matches = new ArrayList<>(5); // setting cap size to 5 since can only have 5 results.
		if (keywordsIndex.get(kw1) == null && keywordsIndex.get(kw2) == null) {
			return null;
		} else if (keywordsIndex.get(kw1) != null && keywordsIndex.get(kw2) == null) {
			for (int i = 0; i < keywordsIndex.get(kw1).size(); i++) {
				if (i == 5) {
					break;
				}
				matches.add(i, keywordsIndex.get(kw1).get(i).document);
			}
			return matches;
		} else if (keywordsIndex.get(kw1) == null && keywordsIndex.get(kw2) != null) {
			for (int i = 0; i < keywordsIndex.get(kw2).size(); i++) {
				if (i == 5) {
					break;
				}
				matches.add(i, keywordsIndex.get(kw2).get(i).document);
			}
			return matches;
		}

		ArrayList<String> matcheskw1 = new ArrayList<>(5);
		ArrayList<String> matcheskw2 = new ArrayList<>(5);
		if (keywordsIndex.get(kw1) != null) {
			for (int i = 0; i < keywordsIndex.get(kw1).size(); i++) {
				if (i == 5) {
					break;
				}
				for (int j = 0; j < keywordsIndex.get(kw1).size(); j++) {
					if (!inArray(matcheskw1, keywordsIndex.get(kw1).get(j).document)) {
						matcheskw1.add(keywordsIndex.get(kw1).get(j).document);
					}
				}
			}
		}
		if (keywordsIndex.get(kw2) != null) {
			for (int i = 0; i < keywordsIndex.get(kw2).size(); i++) {
				if (i == 5) {
					break;
				}
				for (int j = 0; j < keywordsIndex.get(kw2).size(); j++) {
					if (!inArray(matcheskw2, keywordsIndex.get(kw2).get(j).document)) {
						matcheskw2.add(keywordsIndex.get(kw2).get(j).document);
					}
				}
			}
		}

		System.out.println("Matcheskw1: " + matcheskw1.toString());
		System.out.println("Matcheskw2: " + matcheskw2.toString());

		int indexkw1 = 0, indexkw2 = 0;
		while (true) {
			if (matches.size() == 5 || (indexkw1 == matcheskw1.size() && indexkw2 == matcheskw2.size())) {
				break;
			}
			int occkw1 = -1, occkw2 = -1;
			if (indexkw1 < matcheskw1.size()) {
				for (int i = 0; i < keywordsIndex.get(kw1).size(); i++) {
					if (keywordsIndex.get(kw1).get(i).document.equals(matcheskw1.get(indexkw1))) {
						occkw1 = keywordsIndex.get(kw1).get(i).frequency;
					}
				}
			}
			if (indexkw2 < matcheskw2.size()) {
				for (int i = 0; i < keywordsIndex.get(kw2).size(); i++) {
					if (keywordsIndex.get(kw2).get(i).document.equals(matcheskw2.get(indexkw2))) {
						occkw2 = keywordsIndex.get(kw2).get(i).frequency;
					}
				}
			}
			System.out.println("occkw1: " + occkw1 + "\tocckw2: " + occkw2);
			if (occkw1 >= occkw2) {
				if (!inArray(matches, matcheskw1.get(indexkw1))) {
					matches.add(matcheskw1.get(indexkw1));
				}
				indexkw1++;
			} else {
				if (!inArray(matches, matcheskw2.get(indexkw2))) {
					matches.add(matcheskw2.get(indexkw2));
				}
				indexkw2++;
			}
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return matches;
	}

	private boolean inArray(ArrayList<String> arr, String name) {
		if (arr.size() == 0) {
			return false;
		}
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).equals(name)) {
				return true;
			}
		}
		return false;
	}
}
