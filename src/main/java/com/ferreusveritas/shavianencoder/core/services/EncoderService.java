package com.ferreusveritas.shavianencoder.core.services;

import com.ferreusveritas.shavianencoder.core.encoder.ShavianEncoder;
import com.ferreusveritas.shavianencoder.core.model.EncodeRequest;
import com.ferreusveritas.shavianencoder.core.model.ShavianResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncoderService {
	
	private final ShavianEncoder shavianEncoder;
	
	@Autowired
	public EncoderService(
		ShavianEncoder shavianEncoder
	) {
		this.shavianEncoder = shavianEncoder;
	}
	
	public ShavianResponse encode(EncodeRequest request) {
		String result = shavianEncoder.encode(request.message());
		return new ShavianResponse(result);
	}
	
}
