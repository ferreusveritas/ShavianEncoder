package com.ferreusveritas.shavianencoder.core.dictionary;

import com.ferreusveritas.shavianencoder.core.model.UDPosTag;

import java.util.List;
import java.util.Set;

public record DictionaryEntry(
	String headword,
	Set<UDPosTag> tags,
	List<Pronunciation> pronunciations
) {}
