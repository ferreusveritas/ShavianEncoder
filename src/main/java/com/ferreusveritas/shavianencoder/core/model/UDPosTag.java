package com.ferreusveritas.shavianencoder.core.model;

/** Universal Dependencies Part-of-Speech Tags */
public enum UDPosTag {
	ADJ, // adjective
	ADP, // adposition
	ADV, // adverb
	AUX, // auxiliary
	CCONJ, // coordinating conjunction
	DET, // determiner
	INTJ, // interjection
	NOUN, // noun
	NUM, // numeral
	PART, // particle
	PRON, // pronoun
	PROPN, // proper noun
	PUNCT, // punctuation
	SCONJ, // subordinating conjunction
	SYM, // symbol
	VERB, // verb
	X, // other
	
	// Custom
	WHITE,	// Custom: Whitespace
	ABBR,	// Custom: Abbreviation
	CONT;	// Custom: Contraction
	
	public static UDPosTag of(String tag) {
		return valueOf(tag.trim().toUpperCase());
	}
	
	public boolean isWhitespace() {
		return this == WHITE;
	}
	
	public boolean isVerb() {
		return this == VERB;
	}
	
	public boolean isNoun() {
		return this == NOUN;
	}
	
	public boolean isProperNoun() {
		return this == PROPN;
	}
	
	public boolean isPronoun() {
		return this == PRON;
	}
	
	public boolean isAdjective() {
		return this == ADJ;
	}
	
	public boolean isSymbol() {
		return this == SYM;
	}
	
	public boolean isNumber() {
		return this == NUM;
	}
	
}