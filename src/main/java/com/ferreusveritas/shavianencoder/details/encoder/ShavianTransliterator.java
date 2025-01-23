package com.ferreusveritas.shavianencoder.details.encoder;

import com.ferreusveritas.shavianencoder.core.dictionary.Dictionary;
import com.ferreusveritas.shavianencoder.core.lexicon.Lexicon;
import com.ferreusveritas.shavianencoder.core.encoder.Transliterator;
import com.ferreusveritas.shavianencoder.core.encoder.SpeechTagger;
import com.ferreusveritas.shavianencoder.core.lexicon.LexiconEntry;
import com.ferreusveritas.shavianencoder.core.model.UDPosTag;
import com.ferreusveritas.shavianencoder.core.model.SpeechEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShavianTransliterator implements Transliterator {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShavianTransliterator.class);
	
	private final Dictionary dictionary;
	private final SpeechTagger speechTagger;
	private final ShawMappingData shawMappingData;
	private final Lexicon lexicon;
	
	public ShavianTransliterator(
		Dictionary dictionary,
		SpeechTagger speechTagger,
		ShawMappingData shawMappingData,
		Lexicon lexicon
	) throws IOException {
		this.dictionary = dictionary;
		this.speechTagger = speechTagger;
		this.shawMappingData = shawMappingData;
		this.lexicon = lexicon;
	}
	
	@Override
	public String transliterate(String english) {
		List<SpeechEntity> entities = speechTagger.tagSentence(english);
		entities = handleContractions(entities);
		StringBuilder out = new StringBuilder();
		int i = 0;
		for(SpeechEntity entity : entities) {
			String shaw = transliterateEntity(entity, i, entities);
			LOG.info(entity.normal() + " -> " + shaw);
			out.append(shaw);
			i++;
		}
		return fixSpacing(out.toString());
	}
	
	private String fixSpacing(String text) {
		return text
			.replaceAll("␍", "\n") // Match ␍ and replace with newline
			.replaceAll("␉", "\t"); // Match ␉ and replace with tab
	}
	
	private String getFinalSSound(String base) {
		List<String> finals = List.of("𐑕", "𐑟", "𐑖", "𐑠", "𐑗", "𐑡");
		String last = base.substring(base.length() - 2); // shaw characters are 2-wide
		if(finals.contains(last)) {
			return "𐑦𐑟";
		}
		if(shawMappingData.getUnvoiced().contains(last)) {
			return "𐑕";
		}
		return "𐑟";
	}
	
	private String guessPronunciation(SpeechEntity entity) {
	
		if(lexicon.hasWord(entity.lemma())) {
			String base = transliterateEntity(new SpeechEntity(entity.lemma(), entity.pos(), entity.lemma()));
			
			// build -ing words from lemma
			if (entity.pos().isVerb() && entity.normal().endsWith("ing")) {
				return base + "𐑦𐑙";
			}
			
			// build -ed words from lemma
			if (entity.pos().isVerb() && entity.normal().endsWith("ed")) {
				String last = base.substring(base.length() - 2); // shaw characters are 2-wide
				String ending = (last.equals("𐑛") || last.equals("𐑑")) ? "𐑦𐑛"
					: shawMappingData.getNasals().contains(last) ? "𐑛"
					: shawMappingData.getConsonants().contains(last) ? "𐑑"
					: "𐑛";
				return base + ending;
			}
			
			// build -(e)s words from lemma
			if ((entity.pos().isNoun() && !entity.pos().isProperNoun() && entity.normal().endsWith("s") && !entity.lemma().endsWith("s"))) {
				String ending = getFinalSSound(base);
				return base + ending;
			}
		
			return base;
		}
		
		// guess CamelCased words based on individual pieces
		if(entity.normal().matches("[A-Z]?[a-z]+[A-Z]+([a-zA-Z])+")) { // detect camelCase or TitleCase
			boolean startsWithCapital = Character.isUpperCase(entity.normal().charAt(0));
			String[] normals = entity.normal().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
			for (int i = 0; i < normals.length; i++) {
				SpeechEntity tag = speechTagger.tagSentence(normals[i].toLowerCase()).get(0);
				normals[i] = transliterateEntity(tag);
			}
			return (startsWithCapital ? shawMappingData.getNamingDot() : "") + String.join(shawMappingData.getNamingDot(), normals);
		}
		
		return entity.normal();
	}
	
	private String getPossessive(SpeechEntity entity) {
		boolean endsWithS = entity.normal().endsWith("s");
		final String prevShaw = transliterateEntity(entity);
		String finalSSound = getFinalSSound(prevShaw);
		if(endsWithS) {
			return prevShaw + finalSSound + "'";
		}
		return prevShaw + "'" + finalSSound;
	}
	
	private boolean isName(SpeechEntity entity) {
		List<LexiconEntry> variants = lexicon.getEntries(entity.normal());
		if(variants.isEmpty()) {
			return false;
		}
		if(!entity.pos().isNoun()) {
			return false;
		}
		boolean foundProper = variants.stream().anyMatch(entry -> entry.testTags(UDPosTag::isProperNoun));
		boolean foundOther = variants.stream().anyMatch(entry ->
			entry.testTags(UDPosTag::isNoun) ||
			entry.testTags(UDPosTag::isAdjective) ||
			entry.testTags(UDPosTag::isVerb)
		);
		return foundProper && (!foundOther || Character.isUpperCase(entity.normal().charAt(0)));
	}
	
	private String transliterateEntity(SpeechEntity entity) {
		return transliterateEntity(entity, 0, List.of(entity));
	}
	
	private String transliterateEntity(SpeechEntity entity, int i, List<SpeechEntity> entities) {
		List<LexiconEntry> variants = lexicon.getEntries(entity.normal());
		
		if(entity.pos().isWhitespace()) {
			return entity.normal();
		}
		
		if(shawMappingData.getAbbreviationsMap().containsKey(entity.normal())) {
			return shawMappingData.getAbbreviationsMap().get(entity.normal());
		}
		
		if(entity.pos().isSymbol()) {
			return entity.normal();
		}
		
		// Check if tag is num and string doesn't start with alphabetic character
		if(entity.pos().isNumber() && !Character.isAlphabetic(entity.normal().charAt(0))) {
			return entity.normal();
		}
		
		if(entity.normal().endsWith("'s")) {
			entity = new SpeechEntity(entity.normal().substring(0, entity.normal().length() - 2), entity.pos(), entity.lemma());
			return getPossessive(entity);
		}
		
		if(entity.normal().endsWith("s'")) {
			entity = new SpeechEntity(entity.normal().substring(0, entity.normal().length() - 1), entity.pos(), entity.lemma());
			return getPossessive(entity);
		}
		
		if(variants.isEmpty()) {
			return guessPronunciation(entity);
		}
		
		UDPosTag pos = entity.pos();
		String shaw = variants.get(0).shavian();
		
		if (variants.size() > 1) {
			shaw = variants.stream()
				.filter(entry -> entry.pos().contains(pos))
				.findFirst().map(LexiconEntry::shavian)
				.orElse(shaw);
		}
		
		// Sometimes tagger doesn't tag names with NNP, but if the only ISLE entry is
		// NNP we can be pretty sure it should be a proper noun
		boolean isProper = pos.isProperNoun() || isName(entity);
		return isProper ? shawMappingData.getNamingDot() + shaw : shaw;
	}
	
	private boolean isApostrophe(String str) {
		return str.equals("'") || str.equals("’");
	}
	
	private List<SpeechEntity> handleContractions(List<SpeechEntity> entities) {
		List<SpeechEntity> result = new ArrayList<>();
		for (int i = 0; i < entities.size(); i++) {
			SpeechEntity entity = entities.get(i);
			if(i + 2 < entities.size() && isApostrophe(entities.get(i + 1).normal())) {
				String combined = entities.get(i).normal() + "'" + entities.get(i + 2).normal();
				boolean isContraction = !dictionary.entries(combined.toLowerCase()).isEmpty();
				if(isContraction) {
					result.add(new SpeechEntity(combined, UDPosTag.CONT, combined));
					i += 2;
					continue;
				}
				boolean isPossessive = entities.get(i + 2).normal().equals("s");
				if(isPossessive) {
					result.add(new SpeechEntity(combined, entity.pos(), combined));
					i += 2;
					continue;
				}
			}
			if(i + 1 < entities.size() && isApostrophe(entities.get(i + 1).normal())) {
				String combined = entities.get(i).normal() + "'";
				result.add(new SpeechEntity(combined, entity.pos(), combined));
				i++;
				continue;
			}
			result.add(entity);
		}
		return List.copyOf(result);
	}
	
}
