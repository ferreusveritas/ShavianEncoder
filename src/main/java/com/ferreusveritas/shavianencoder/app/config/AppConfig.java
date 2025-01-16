package com.ferreusveritas.shavianencoder.app.config;

import com.ferreusveritas.shavianencoder.core.encoder.ShavianEncoder;
import com.ferreusveritas.shavianencoder.details.encoder.ShavianEncoderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	@Bean
	ShavianEncoder shavianEncoder() {
		return new ShavianEncoderImpl();
	}
	
}
