package github.grit.config;

import javax.servlet.annotation.WebFilter;

import github.grit.filter.SimpleFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@WebFilter
public class FilterConfig {
	@Bean
	public FilterRegistrationBean<SimpleFilter> myFilter() {
		FilterRegistrationBean<SimpleFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SimpleFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setName("MyFilter");
		registrationBean.setOrder(1);
		return registrationBean;
	}
}
