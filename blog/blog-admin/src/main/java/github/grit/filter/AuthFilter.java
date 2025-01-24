package github.grit.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.jwt.JWTUtil;
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
