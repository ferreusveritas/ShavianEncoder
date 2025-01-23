package com.ferreusveritas.shavianencoder.core.lexicon;

import com.ferreusveritas.shavianencoder.core.model.UDPosTag;

import java.util.Set;
import java.util.function.Predicate;

public record LexiconEntry(
	String shavian,
	Set<UDPosTag> pos
) {
	
	public boolean testTags(Predicate<UDPosTag> tag) {
		return pos.stream().anyMatch(tag);
	}
	
}
