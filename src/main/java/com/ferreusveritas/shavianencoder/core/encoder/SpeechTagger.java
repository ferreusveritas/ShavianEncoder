package com.ferreusveritas.shavianencoder.core.encoder;

import com.ferreusveritas.shavianencoder.core.model.SpeechEntity;

import java.util.List;

public interface SpeechTagger {
	List<SpeechEntity> tagSentence(String message);
}
