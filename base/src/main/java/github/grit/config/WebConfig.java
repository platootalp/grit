package github.grit.config;

import github.grit.interceptor.SimpleInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final SimpleInterceptor simpleInterceptor;

	public WebConfig(SimpleInterceptor simpleInterceptor) {
		this.simpleInterceptor =simpleInterceptor;
	}
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(simpleInterceptor)
				.addPathPatterns("/demo/interceptor") // 拦截的路径
				.excludePathPatterns("/demo/filter"); // 排除的路径
	}
}
