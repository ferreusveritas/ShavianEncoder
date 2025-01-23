package com.ferreusveritas.shavianencoder.core.lexicon;

import com.ferreusveritas.shavianencoder.core.dictionary.Dictionary;
import com.ferreusveritas.shavianencoder.core.dictionary.DictionaryEntry;
import com.ferreusveritas.shavianencoder.core.dictionary.Pronunciation;
import com.ferreusveritas.shavianencoder.core.encoder.Mapper;
import com.ferreusveritas.shavianencoder.core.model.UDPosTag;

import java.util.*;
import java.util.function.Function;

public class Lexicon {
	
	private final Map<String, List<LexiconEntry>> value;
	
	public Lexicon(
		Mapper mapper,
		Dictionary dictionary
	) {
		this.value = generateShawLexicon(dictionary, mapper);
	}
	
	public boolean hasWord(String word) {
		return value.containsKey(word);
	}
	
	public List<LexiconEntry> getEntries(String word) {
		return value.getOrDefault(word.toLowerCase(), List.of());
	}
	
	private LexiconEntry entryToShavian(Mapper mapper, DictionaryEntry entry) {
		List<Pronunciation> pronunciations = entry.pronunciations();
		return new LexiconEntry(
			pronunciations.stream()
				.map(mapper::map)
				.reduce("", (a, b) -> a + b),
			entry.tags()
		);
	}
	
	private Map<String, List<LexiconEntry>> generateShawLexicon(
		Dictionary dictionary,
		Mapper mapper
	) {
		Map<String, List<LexiconEntry>> lexicon = new HashMap<>();
		
		for (String headword : dictionary.words()) { // Iterate over every word in the entire english dictionary
			
			List<DictionaryEntry> entries = dictionary.entries(headword);
			
			// Some written words have multiple uses and thus multiple part of speech entries.
			// For example: wind(verb, to turn) and wind(noun, a breeze)
			List<LexiconEntry> variants = entries.stream()
				.map(e -> entryToShavian(mapper, e))
				.toList();
			
			// Group the variants by their part of speech. Some words have differing pronunciations even within the same part of speech
			Map<Set<UDPosTag>, List<LexiconEntry>> byPOS = groupBy(variants, LexiconEntry::pos);
			
			// Attempt to find the best pronunciation for each part of speech
			for(Set<UDPosTag> pos : byPOS.keySet()) {
				List<LexiconEntry> alternates = byPOS.get(pos);
				alternates = findBestPronunciation(alternates);
				byPOS.put(pos, alternates);
			}
			List<LexiconEntry> toAdd = byPOS.values().stream().flatMap(List::stream).toList();
			lexicon.put(headword, toAdd);
		}
		return Map.copyOf(lexicon);
	}
	
	/** Group a list of items by a key function */
	private <K, V> Map<K, List<V>> groupBy(List<V> list, Function<V, K> keyMaker) {
		Map<K, List<V>> map = new HashMap<>();
		for (V item : list) {
			K key = keyMaker.apply(item);
			List<V> group = map.computeIfAbsent(key, k -> new ArrayList<>());
			group.add(item);
		}
		return map;
	}
	
	/** Attempt to find the best pronunciation for a word with multiple pronunciations */
	private List<LexiconEntry> findBestPronunciation(List<LexiconEntry> alternates) {
		if (alternates.size() <= 1) {
			return alternates;
		}
		return alternates.stream().sorted((a, b) -> {
			// prefer /w/ over /hw/
			boolean aHasHW = a.shavian().contains("𐑣𐑢");
			boolean bHasHW = b.shavian().contains("𐑣𐑢");
			if (aHasHW || bHasHW) {
				return aHasHW && !bHasHW ? 1 : !aHasHW ? -1 : 0;
			}
			// otherwise, prefer longer version as it's less likely to be the "fast speech" version
			return Integer.compare(a.shavian().length(), b.shavian().length());
		}).toList();
	}
	
}
