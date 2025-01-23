package com.ferreusveritas.shavianencoder.details.encoder;

import com.ferreusveritas.shavianencoder.core.encoder.SpeechTagger;
import com.ferreusveritas.shavianencoder.core.model.SpeechEntity;
import com.ferreusveritas.shavianencoder.core.model.UDPosTag;
import opennlp.tools.lemmatizer.Lemmatizer;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SpeechTaggerImpl implements SpeechTagger {
	
	private final Tokenizer tokenizer;
	private final POSTaggerME posTagger;
	private final Lemmatizer lemmatizer;
	
	public SpeechTaggerImpl() {
		this.tokenizer = SimpleTokenizer.INSTANCE;
		SimpleTokenizer.INSTANCE.setKeepNewLines(true);
		
		try (InputStream modelIn = getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-pos-1.2-2.5.0.bin")) {
			POSModel posModel = new POSModel(modelIn);
			this.posTagger = new POSTaggerME(posModel);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load POS model", e);
		}
		
		try (InputStream modelIn = getClass().getResourceAsStream("/models/opennlp-en-ud-ewt-lemmas-1.2-2.5.0.bin")) {
			LemmatizerModel model = new LemmatizerModel(modelIn);
			this.lemmatizer = new LemmatizerME(model);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load lemmatizer model", e);
		}
		
	}
	
	@Override
	public List<SpeechEntity> tagSentence(String message) {
		String[] tokens = tokenizer.tokenize(message);
		String[] posTags = posTagger.tag(tokens);
		String[] lemmas = lemmatizer.lemmatize(tokens, posTags);
		List<SpeechEntity> result = new ArrayList<>(tokens.length);
		for (int i = 0; i < tokens.length; i++) {
			String normal = tokens[i];
			String posTag = posTags[i];
			String lemma = lemmas[i];
			UDPosTag pos = UDPosTag.of(posTag);
			result.add(new SpeechEntity(normal, pos, lemma));
		}
		return expandWithWhitespace(result, message);
	}
	
	private List<SpeechEntity> expandWithWhitespace(List<SpeechEntity> entities, String message) {
		List<SpeechEntity> result = new ArrayList<>();
		int start = 0;
		for (SpeechEntity entity : entities) {
			int end = message.indexOf(entity.normal(), start);
			if (end > start) {
				String whitespace = message.substring(start, end);
				result.add(new SpeechEntity(whitespace, UDPosTag.WHITE, " "));
			}
			result.add(entity);
			start = end + entity.normal().length();
		}
		if (start < message.length()) {
			String whitespace = message.substring(start);
			result.add(new SpeechEntity(whitespace, UDPosTag.WHITE, " "));
		}
		return result;
	}
	
}
