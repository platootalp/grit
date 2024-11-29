package github.grit.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SimpleInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		System.out.println("PreHandle: 请求处理之前执行");
		String name = request.getParameter("name");
		return "interceptor".equals(name); // 返回 true 继续处理请求，返回 false 拦截请求
	}

	// 在处理请求之后、生成视图之前执行（如日志记录）
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		System.out.println("PostHandle: 请求处理之后，生成视图之前执行");
	}

	// 在请求完成后执行（如资源清理）
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		System.out.println("AfterCompletion: 请求完成后执行");
	}
}
