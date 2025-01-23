package com.ferreusveritas.shavianencoder.core.model;

public record SpeechEntity(
	String normal, // Normalized form of the word
	UDPosTag pos, // Part of Speech Tag
	String lemma // Lemmatized form of the word
) {
}
