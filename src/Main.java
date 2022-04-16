import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	
	public static void main(String[] args) throws IOException {
		Trie tree = loadBook("book.txt");
		Map<String, List<String>> words = getWordsLinks("https://crawler-test.com/", tree);
		
		Scanner scanner = new Scanner(System.in);
		try {
			while (true) {
				System.out.println("Search word: ");
				String word = scanner.nextLine().toUpperCase();
				
				if (words.containsKey(word)) {
					System.out.println("Word found in:");
					for (String link : words.get(word)) {
						System.out.println(link);
					}
					System.out.println();
				} else {
					System.out.println("Word not found.");
				}
			}
		} finally {		
			scanner.close();
		}
	}

	private static Trie loadBook(String path) {
		Trie trie = new Trie('A');
		
		try (BufferedReader bookReader = Files.newBufferedReader(Paths.get(path))) {
			String linha;
			while ((linha = bookReader.readLine()) != null) {
				linha = linha.toUpperCase();
				
				List<String> words = getTextWords(linha);
				for (String word : words) {
					trie.insert(word);
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return trie;
	}
	
	private static Map<String, List<String>> getWordsLinks(String url, Trie tree) {
		return getWordsLinks(url, url, tree, null, null);
	}
	
	private static Map<String, List<String>> getWordsLinks(String url, String mainURL, Trie tree, Map<String, List<String>> wordsLinks, List<String> visitedLinks) {
		if (wordsLinks == null) {
			wordsLinks = new HashMap<>();
		}
		if (visitedLinks == null) {
			visitedLinks = new ArrayList<>();
		}
		
		if (!url.startsWith(mainURL)) {
			return wordsLinks;
		}
		
		visitedLinks.add(url);
		
		Document document;
		try {
			document = Jsoup.connect(url).get();
			System.out.println("Downloaded: " + url);
		} catch (Exception e) {
			return wordsLinks;
		}
		Element body = document.getElementsByTag("body").get(0);
		
		String texto = body.text();
		
		List<String> words = getTextWords(texto.toUpperCase());
		for (String word : words) {
			
			if (tree.search(word)) {
				
				if (!wordsLinks.containsKey(word)) {
					wordsLinks.put(word, new ArrayList<String>());
				}
				
				List<String> links = wordsLinks.get(word);
				if (!links.contains(url)) {
					links.add(url);
				}
			}
		}
		
		Elements links = document.select("a[href]");
		for (Element link : links) {
			String urlFilho = link.attr("abs:href");
			if (!visitedLinks.contains(urlFilho)) {
				getWordsLinks(urlFilho, url, tree, wordsLinks, visitedLinks);
			}
		}
		
		return wordsLinks;
	}
	
	private static List<String> getTextWords(String texto) {
		List<String> words = new ArrayList<>();
		
		Pattern p = Pattern.compile("[a-zA-Z]+");
		Matcher m1 = p.matcher(texto);
		while (m1.find()) {
			String word = m1.group();
			words.add(word);
		}
		return words;
	}
}