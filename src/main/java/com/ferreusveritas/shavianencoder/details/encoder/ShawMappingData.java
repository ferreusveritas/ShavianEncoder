package com.ferreusveritas.shavianencoder.details.encoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ShawMappingData {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShawMappingData.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private static Map<String, String> loadMap(String json) {
		try {
			return MAPPER.readValue(json, new TypeReference<>(){});
		} catch (Exception e) {
			LOG.debug("Failed to load map", e);
			return Map.of();
		}
	}
	
	private final Map<String, String> abbreviationsMap = loadMap("""
	{
		"to": "𐑑",
		"the": "𐑞",
		"and": "𐑯",
		"of": "𐑝"
	}
	""");
	
	private final String namingDot = "·";
	
	private final Map<String, String> consonantsMap = loadMap("""
	{
		"p": "𐑐",
		"b": "𐑚",
		"d": "𐑛",
		"t": "𐑑",
		"k": "𐑒",
		"g": "𐑜",
		"f": "𐑓",
		"v": "𐑝",
		"ɵ": "𐑔",
		"ð": "𐑞",
		"s": "𐑕",
		"z": "𐑟",
		"ʃ": "𐑖",
		"ʒ": "𐑠",
		"tʃ": "𐑗",
		"dʒ": "𐑡",
		"j": "𐑘",
		"w": "𐑢",
		"ŋ": "𐑙",
		"h": "𐑣",
		"l̩": "𐑩𐑤",
		"l": "𐑤",
		"ɹ": "𐑮",
		"r": "𐑮",
		"m": "𐑥",
		"n": "𐑯",
		"n̩": "𐑩𐑯"
	}
	""");
	
	private final Map<String, String> vowelsMap = loadMap("""
	{
		"ɪ": "𐑦",
		"i": "𐑰",
		"iː": "𐑰",
		"ɛ": "𐑧",
		"eɪ": "𐑱",
		"æ": "𐑨",
		"ɑɪ": "𐑲",
		"aɪ": "𐑲",
		"ə": "𐑩",
		"ʌ": "𐑳",
		"ɒ": "𐑪",
		"oʊ": "𐑴",
		"ʊ": "𐑫",
		"u": "𐑵",
		"aʊ": "𐑬",
		"ɔi": "𐑶",
		"ɔɪ": "𐑶",
		"ɑ": "𐑭",
		"ɑː": "𐑭",
		"ɔ": "𐑷",
		"ɔː": "𐑷",
		"ei": "𐑱",
		"iə": "𐑾",
		"ju": "𐑿"
	}
	""");
	
	private final Map<String, String> rColoredMap = loadMap("""
	{
		"ɑɹ": "𐑸",
		"ɑːɹ": "𐑸",
		"ɔɹ": "𐑹",
		"ɔəɹ": "𐑹",
		"ɛəɹ": "𐑺",
		"ɛɹ": "𐑺",
		"ɝ": "𐑻",
		"ɜɹ": "𐑻",
		"ɚ": "𐑼",
		"əɹ": "𐑼",
		"ɪɹ": "𐑽",
		"ɪəɹ": "𐑽"
	}
	""");
	
	private final Map<String, String> ipa2shaw;
	private final List<String> consonants;
	private final List<String> nasals;
	private final List<String> fricatives;
	private final List<String> affricates;
	private final List<String> voiced;
	private final List<String> unvoiced;
	
	
	public ShawMappingData() {
		this.ipa2shaw = Stream.of(consonantsMap, vowelsMap, rColoredMap)
			.flatMap(m -> m.entrySet().stream())
			.collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
		this.consonants = consonantsMap.values().stream().toList();
		this.nasals = List.of("𐑙", "𐑥", "𐑯");
		this.fricatives = List.of("𐑓", "𐑝", "𐑕", "𐑟", "𐑔", "𐑞", "𐑖", "𐑠");
		this.affricates = List.of("𐑗", "𐑡");
		this.voiced = List.of("𐑚", "𐑛", "𐑜", "𐑝", "𐑞", "𐑟", "𐑠", "𐑡");
		this.unvoiced = List.of("𐑐", "𐑑", "𐑒", "𐑓", "𐑔", "𐑕", "𐑖", "𐑗");
	}
	
	public Map<String, String> getAbbreviationsMap() {
		return abbreviationsMap;
	}
	
	public String getNamingDot() {
		return namingDot;
	}
	
	public Map<String, String> getConsonantsMap() {
		return consonantsMap;
	}
	
	public List<String> getConsonants() {
		return consonants;
	}
	
	public Map<String, String> getVowelsMap() {
		return vowelsMap;
	}
	
	public Map<String, String> getRColored() {
		return rColoredMap;
	}
	
	public Map<String, String> getIpa2shaw() {
		return ipa2shaw;
	}
	
	public List<String> getNasals() {
		return nasals;
	}
	
	public List<String> getAffricates() {
		return affricates;
	}
	
	public List<String> getFricatives() {
		return fricatives;
	}
	
	public List<String> getUnvoiced() {
		return unvoiced;
	}
	
	public List<String> getVoiced() {
		return voiced;
	}
	
}
