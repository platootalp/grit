package github.grif.chain;

import org.springframework.core.Ordered;

/**
 * 责任链处理器
 * @param <T>
 */
public interface Handler<T> extends Ordered {

	boolean handle(T request);

	/**
	 * 获取表示标识（区分处理器属于哪条责任链）
	 * @return
	 */
	String getMark();
}
