package com.ferreusveritas.shavianencoder.app.web.controllers;

import com.ferreusveritas.shavianencoder.core.model.EncodeRequest;
import com.ferreusveritas.shavianencoder.core.model.ShavianResponse;
import com.ferreusveritas.shavianencoder.core.services.EncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/encode")
public class EncodeController {
	
	private final EncoderService encoderService;
	
	@Autowired
	public EncodeController(
		EncoderService encoderService
	) {
		this.encoderService = encoderService;
	}
	
	// Add a simple post here
	@PostMapping()
	public ShavianResponse encode(
		@RequestBody EncodeRequest request
	) {
		return encoderService.encode(request);
	}
	
}
