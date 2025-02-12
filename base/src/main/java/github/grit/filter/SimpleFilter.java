package github.grit.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


public class SimpleFilter implements Filter {
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		System.out.println("对request进行过滤");
		try {
			filterChain.doFilter(servletRequest,servletResponse);
		}
		catch (ServletException e) {
			throw new RuntimeException(e);
		}
		System.out.println("对response进行过滤");
	}
}
