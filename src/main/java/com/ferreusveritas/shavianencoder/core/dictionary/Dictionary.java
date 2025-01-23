package com.ferreusveritas.shavianencoder.core.dictionary;

import java.util.List;

public interface Dictionary {
	List<String> words();
	List<DictionaryEntry> entries(String word);
}
