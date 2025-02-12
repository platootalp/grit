package github.grif.chain;

import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ChainContext<T> implements CommandLineRunner {

	// 存储责任链，标识与处理器列表的映射
	private final Map<String, List<Handler<T>>> chainMap = new HashMap<>();

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void run(String... args) throws Exception {
		// 获取所有实现了 Handler 接口的 Bean
		Map<String, Handler> handlerMap = applicationContext.getBeansOfType(Handler.class);

		// 遍历所有 Handler，按责任链的标识进行组织
		handlerMap.forEach((beanName, handler) -> {
			// 获取该处理器的责任链标识
			String mark = getHandlerMark(handler);

			// 根据 mark 获取责任链中的处理器列表，若没有则创建一个新的
			List<Handler<T>> handlers = chainMap.computeIfAbsent(mark, k -> new ArrayList<>());

			// 将处理器加入到责任链中
			handlers.add(handler);

			// 根据处理器的 order 对责任链进行排序
			List<Handler<T>> sortedHandlers = handlers.stream()
					.sorted(Comparator.comparingInt(Handler::getOrder))
					.collect(Collectors.toList());

			// 将排序后的责任链保存回 chainMap
			chainMap.put(mark, sortedHandlers);
		});
	}

	/**
	 * 获取处理器的 mark（标识符）
	 *
	 * @param handler 处理器
	 * @return mark
	 */
	private String getHandlerMark(Handler<T> handler) {
		// 这里假设 Handler 实现类会提供获取标识的方法，可以根据需求修改
		if (handler != null) {
			return handler.getMark();
		}
		return "default";
	}

	/**
	 * 执行指定 mark 的责任链
	 *
	 * @param mark 责任链标识
	 * @param request 请求参数
	 */
	public void executeChain(String mark, T request) {
		List<Handler<T>> handlers = chainMap.get(mark);
		if (CollectionUtils.isEmpty(handlers)) {
			throw new RuntimeException("No handlers found for mark: " + mark);
		}

		// 遍历执行责任链
		for (Handler<T> handler : handlers) {
			if (!handler.handle(request)) {
				break;
			}
		}
	}
}

