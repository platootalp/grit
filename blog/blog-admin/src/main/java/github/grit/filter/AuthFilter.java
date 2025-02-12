package github.grit.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;


import github.grit.util.UserUtil;

import org.springframework.core.annotation.Order;


/**
 * jwt认证
 */
@WebFilter("/*")
@Order(0)
public class AuthFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if(!UserUtil.verify(request)){
			return;
		}
		filterChain.doFilter(servletRequest,servletResponse);
	}
}
