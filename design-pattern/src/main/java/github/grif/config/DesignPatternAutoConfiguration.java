package github.grif.config;

import github.grif.chain.ChainContext;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DesignPatternAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ChainContext.class)
	 public ChainContext chainContext() {
	 	return new ChainContext();
	 }
}
