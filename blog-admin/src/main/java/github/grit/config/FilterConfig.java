package github.grit.config;


import github.grit.filter.SimpleFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
	@Bean
	public FilterRegistrationBean<SimpleFilter> myFilter() {
		FilterRegistrationBean<SimpleFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SimpleFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setName("MyFilter");
		registrationBean.setOrder(0);
		return registrationBean;
	}
}
