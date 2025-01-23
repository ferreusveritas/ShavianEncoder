package com.ferreusveritas.shavianencoder.core.model;

/** Penn Treebank Part-of-Speech Tags */
public enum PTBPosTag {
		CC(UDPosTag.CCONJ),		// 1.	CC: Coordinating conjunction
		CD(UDPosTag.NUM),		// 2.	CD: Cardinal number
		DT(UDPosTag.DET),		// 3.	DT: Determiner
		EX(UDPosTag.PRON),		// 4.	EX: Existential there
		FW(UDPosTag.X),			// 5.	FW: Foreign word
		IN(UDPosTag.SCONJ),		// 6.	IN: Preposition or subordinating conjunction
		JJ(UDPosTag.ADJ),		// 7.	JJ: Adjective
		JJR(UDPosTag.ADJ),		// 8.	JJR: Adjective, comparative
		JJS(UDPosTag.ADJ),		// 9.	JJS: Adjective, superlative
		LS(UDPosTag.PUNCT),		// 10.	LS: List item marker
		MD(UDPosTag.VERB),		// 11.	MD: Modal
		NN(UDPosTag.NOUN),		// 12.	NN: Noun, singular or mass
		NNS(UDPosTag.NOUN),		// 13.	NNS: Noun, plural
		NNP(UDPosTag.PROPN),	// 14.	NNP: Proper noun, singular
		NNPS(UDPosTag.PROPN),	// 15.	NNPS: Proper noun, plural
		PDT(UDPosTag.DET),		// 16.	PDT: Predeterminer
		POS(UDPosTag.PART),		// 17.	POS: Possessive ending
		PRP(UDPosTag.PRON),		// 18.	PRP: Personal pronoun
		PRPS(UDPosTag.PRON),	// 19.	PRP$: Possessive pronoun
		RB(UDPosTag.ADV),		// 20.	RB: Adverb
		RBR(UDPosTag.ADV),		// 21.	RBR: Adverb, comparative
		RBS(UDPosTag.ADV),		// 22.	RBS: Adverb, superlative
		RP(UDPosTag.PART),		// 23.	RP: Particle
		SYM(UDPosTag.SYM),		// 24.	SYM: Symbol
		TO(UDPosTag.ADP),		// 25.	TO: to
		UH(UDPosTag.INTJ),		// 26.	UH: Interjection
		VB(UDPosTag.VERB),		// 27.	VB: Verb, base form
		VBD(UDPosTag.VERB),		// 28.	VBD: Verb, past tense
		VBG(UDPosTag.VERB),		// 29.	VBG: Verb, gerund or present participle
		VBN(UDPosTag.VERB),		// 30.	VBN: Verb, past participle
		VBP(UDPosTag.VERB),		// 31.	VBP: Verb, non-3rd person singular present
		VBZ(UDPosTag.VERB),		// 32.	VBZ: Verb, 3rd person singular present
		WDT(UDPosTag.PRON),		// 33.	WDT: Wh-determiner
		WP(UDPosTag.PRON),		// 34.	WP: Wh-pronoun
		WPS(UDPosTag.PRON),		// 35.	WP$: Possessive wh-pronoun
		WRB(UDPosTag.ADV);		// 36.	WRB: Wh-adverb
	
	private final UDPosTag udPosTag;
	
	PTBPosTag(UDPosTag udPosTag) {
		this.udPosTag = udPosTag;
	}
	
	public static PTBPosTag of(String str) {
		return PTBPosTag.valueOf(str.trim().toUpperCase().replaceAll("\\$", "S"));
	}
	
	public UDPosTag toUD() {
		return udPosTag;
	}
	
}