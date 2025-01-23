package com.ferreusveritas.shavianencoder.app.config;

import com.ferreusveritas.shavianencoder.core.dictionary.Dictionary;
import com.ferreusveritas.shavianencoder.core.encoder.Mapper;
import com.ferreusveritas.shavianencoder.core.encoder.SpeechTagger;
import com.ferreusveritas.shavianencoder.core.encoder.Transliterator;
import com.ferreusveritas.shavianencoder.core.lexicon.Lexicon;
import com.ferreusveritas.shavianencoder.details.dictionary.ISLEDictionary;
import com.ferreusveritas.shavianencoder.details.encoder.ShawMappingData;
import com.ferreusveritas.shavianencoder.details.encoder.ShavianTransliterator;
import com.ferreusveritas.shavianencoder.details.encoder.ShawMapper;
import com.ferreusveritas.shavianencoder.details.encoder.SpeechTaggerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AppConfig {
	
	@Bean
	Transliterator shavianEncoder(
		Dictionary dictionary,
		SpeechTagger speechTagger,
		ShawMappingData shawMappingData,
		Lexicon lexicon
	) throws IOException {
		return new ShavianTransliterator(
			dictionary,
			speechTagger,
			shawMappingData,
			lexicon
		);
	}
	
	@Bean
	Lexicon lexicon(
		Mapper mapper,
		Dictionary dictionary
	) {
		return new Lexicon(
			mapper,
			dictionary
		);
	}
	
	@Bean
	Dictionary dictionary() throws IOException {
		return new ISLEDictionary("/data/ISLEDict.txt");
	}
	
	@Bean
	SpeechTagger speechTagger() {
		return new SpeechTaggerImpl();
	}
	
	@Bean
	ShawMappingData mapping() {
		return new ShawMappingData();
	}
	
	@Bean
	Mapper mapper(
		ShawMappingData shawMappingData
	) {
		return new ShawMapper(shawMappingData.getIpa2shaw());
	}
	
}
