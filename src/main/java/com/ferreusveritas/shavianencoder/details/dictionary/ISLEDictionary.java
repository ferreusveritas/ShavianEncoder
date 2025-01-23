package com.ferreusveritas.shavianencoder.details.dictionary;

import com.ferreusveritas.shavianencoder.core.dictionary.Dictionary;
import com.ferreusveritas.shavianencoder.core.dictionary.DictionaryEntry;
import com.ferreusveritas.shavianencoder.core.dictionary.Pronunciation;
import com.ferreusveritas.shavianencoder.core.dictionary.Syllable;
import com.ferreusveritas.shavianencoder.core.lexicon.Lexicon;
import com.ferreusveritas.shavianencoder.core.model.PTBPosTag;
import com.ferreusveritas.shavianencoder.core.model.UDPosTag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ISLEDictionary implements Dictionary {
	
	private final Map<String, List<DictionaryEntry>> value;
	
	public ISLEDictionary(String resource) throws IOException {
		InputStream stream = Lexicon.class.getResourceAsStream(resource);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		this.value = importISLEDict(reader);
	}
	
	public ISLEDictionary(BufferedReader reader) throws IOException {
		this.value = importISLEDict(reader);
	}
	
	@Override
	public List<String> words() {
		return value.keySet().stream().sorted().toList();
	}
	
	@Override
	public List<DictionaryEntry> entries(String word) {
		return value.getOrDefault(word, List.of());
	}
	
	private record DictLine(
		String headword,
		String tagString,
		String[] pronunciations
	) {}
	
	public Map<String, List<DictionaryEntry>> importISLEDict(BufferedReader reader) throws IOException {
		Map<String, List<DictionaryEntry>> dictionary = new HashMap<>();
		String line;
		while ((line = reader.readLine()) != null) {
			DictLine dictLine = readDictLine(line);
			if (dictLine == null) {
				continue;
			}
			String headword = processHeadword(dictLine.headword());
			if (headword == null) {
				continue;
			}
			
			Set<UDPosTag> tags = loadPosTags(dictLine.tagString);
			DictionaryEntry dictionaryEntry = createDictionaryEntry(headword, dictLine.pronunciations(), tags);
			dictionary.computeIfAbsent(headword, k -> new ArrayList<>()).add(dictionaryEntry);
		}
		return dictionary;
	}
	
	private DictLine readDictLine(String line) {
		if (skipLine(line)) {
			return null;
		}
		String[] parts = line.split("\\s+");
		String entry = parts[0];
		String[] rest = new String[parts.length - 1];
		System.arraycopy(parts, 1, rest, 0, rest.length);
		String[] headwordTags = entry.replace(")", "").split("\\(");
		String headword = headwordTags[0];
		String tagString = headwordTags.length > 1 ? headwordTags[1] : null;
		String[] pronunciations = new String[rest.length - 2];
		System.arraycopy(rest, 1, pronunciations, 0, pronunciations.length);
		pronunciations = String.join(" ", pronunciations).split(" # ");
		return new DictLine(headword, tagString, pronunciations);
	}
	
	private DictionaryEntry createDictionaryEntry(String headword, String[] pronunciations, Set<UDPosTag> tags) {
		List<Pronunciation> pronunciation = new ArrayList<>();
		for (String word : pronunciations) {
			List<Syllable> syllables = new ArrayList<>();
			for (String s : word.split(" \\. ")) {
				String[] phonemes = s.split(" ");
				Syllable syllable = new Syllable(List.of(phonemes));
				syllables.add(syllable);
			}
			Pronunciation pronunciation1 = new Pronunciation(syllables);
			pronunciation.add(pronunciation1);
		}
		return new DictionaryEntry(headword, tags, pronunciation);
	}
	
	private boolean skipLine(String line) {
		return line.matches("^#|^\\s*$");
	}
	
	private String processHeadword(String headword) {
		if (headword.contains("_")) {
			return null; // Skip headwords that contain underscores.  Underscores are used for word pairings.
		}
		return headword;
	}
	
	private Set<UDPosTag> loadPosTags(String tagString) {
		List<String> tags = tagString != null ? List.of(tagString.split(",")) : List.of();
		boolean isAbbreviation = tags.contains("+abbreviation");
		tags = normalizeTags(tags);
		Set<UDPosTag> posTags = EnumSet.noneOf(UDPosTag.class);
		for(String tagStr : tags) {
			PTBPosTag tag = PTBPosTag.of(tagStr);
			posTags.add(tag.toUD());
			if(isAbbreviation) {
				posTags.add(UDPosTag.ABBR);
			}
		}
		return posTags;
	}
	
	public List<String> normalizeTags(List<String> ppos) {
		return ppos.stream()
			.map(pos -> pos.replaceAll("_\\d+\\.\\d+$", "")) // Remove ranking numbers: nnp_surname_0.001 -> nnp_surname
			.filter(pos -> !pos.startsWith("+")) // Remove tags that start with +
			.filter(pos -> !pos.startsWith("root:")) // Remove tags that start with root:
			.filter(pos -> !pos.contains("_root")) // Remove tags that contain _root
			.filter(pos -> !pos.startsWith("fw_misspelling:")) // Remove fw_misspelling:*
			.filter(pos -> !pos.equals("punc")) // Remove punctuation tags
			.filter(pos -> !pos.equals("of")) // Remove of tags
			.map(pos -> pos.startsWith("nnp_") ? "nnp" : pos) // Normalize nnp_* to nnp
			.map(pos -> pos.startsWith("nnps_") ? "nnps" : pos)// Normalize nnps_* to nnps
			.map(pos -> pos.startsWith("_country") ? "nnp" : pos) // Normalize _country:* to nnp
			.toList();
	}
	
}
