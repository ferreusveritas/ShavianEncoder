package com.ferreusveritas.shavianencoder.core.services;

import com.ferreusveritas.shavianencoder.core.encoder.Transliterator;
import com.ferreusveritas.shavianencoder.core.model.EncodeRequest;
import com.ferreusveritas.shavianencoder.core.model.ShavianResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncoderService {
	
	private final Transliterator transliterator;
	
	@Autowired
	public EncoderService(
		Transliterator transliterator
	) {
		this.transliterator = transliterator;
	}
	
	public ShavianResponse encode(EncodeRequest request) {
		String result = transliterator.transliterate(request.message());
		return new ShavianResponse(result);
	}
	
}
