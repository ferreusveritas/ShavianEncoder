package com.ferreusveritas.shavianencoder.details.encoder;

import com.ferreusveritas.shavianencoder.core.dictionary.Pronunciation;
import com.ferreusveritas.shavianencoder.core.dictionary.Syllable;
import com.ferreusveritas.shavianencoder.core.encoder.Mapper;

import java.util.List;
import java.util.Map;

public class ShawMapper implements Mapper {
	
	private record ConvertPair(String pattern, String shaw) {}
	
	private final List<ConvertPair> pairs;
	
	public ShawMapper(Map<String, String> map) {
		this.pairs = getOrderedMapping(map);
	}
	
	private List<ConvertPair> getOrderedMapping(Map<String, String> mappings) {
		return mappings.keySet().stream()
			.sorted((a, b) -> Integer.compare(b.length(), a.length()))
			.map(pattern -> new ConvertPair(pattern, mappings.get(pattern)))
			.toList();
	}
	
	@Override
	public String map(Pronunciation pronunciation) {
		List<Syllable> syllables = pronunciation.syllables();
		return syllables.stream()
			.map(this::map)
			.reduce("", (a, b) -> a + b);
	}
	
	private String map(Syllable syllable) {
		String ipaIgnore = "[ˈˌ˺]";
		String syl = String
			.join("", syllable.phonemes()) // join all phonemes in the syllable
			.replaceAll(ipaIgnore, ""); // remove all ignored characters
		
		StringBuilder converted = new StringBuilder();
		String remaining = syl;
		
		for(int i = 0; i < pairs.size() && !remaining.isEmpty(); i++) {
			ConvertPair pair = pairs.get(i);
			String pattern = pair.pattern();
			if(remaining.startsWith(pattern)) {
				converted.append(pair.shaw());
				remaining = remaining.substring(pattern.length());
				i = 0;
			}
		}
		converted.append(remaining); // just pass thru anything we couldn't convert
		return converted.toString();
	}
	
}
