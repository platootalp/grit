package github.grif.responsibility;

import java.util.List;

import java.util.ArrayList;

// HandlerChain 类
public class HandlerChain {

	private final List<Handler> chain = new ArrayList<>();

	// 静态方法，用于创建 HandlerChain 并添加处理器
	public static HandlerChain of(Handler... handlers) {
		HandlerChain handlerChain = new HandlerChain();
		for (Handler handler : handlers) {
			handlerChain.addHandler(handler);
		}
		return handlerChain;
	}

	// 添加处理器
	public void addHandler(Handler handler) {
		chain.add(handler);
	}

	// 执行链上的处理逻辑
	public void handle(String request) {
		for (Handler handler : chain) {
			if (handler.handle(request)) {
				System.out.println("Request handled: " + request);
				return;
			}
		}
		System.out.println("No handler could process the request: " + request);
	}

	// 测试用 main 方法
	public static void main(String[] args) {

	}
}
