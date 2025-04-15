package github.grit.datastructure;

import java.util.List;

public abstract class SlidingWindow<T> {
	protected int left = 0;
	protected int right = 0;

	// 每次加入一个元素（右边界扩张）
	protected abstract void add(T element);

	// 每次移除一个元素（左边界收缩）
	protected abstract void remove(T element);

	// 当前窗口是否满足特定条件
	protected abstract boolean isValid();

	// 当前窗口处理逻辑（记录答案等）
	protected abstract void process();

	// 执行滑动窗口
	public void slidingWindow(List<T> data, int windowSize) {
		for (right = 0; right < data.size(); right++) {
			// in
			add(data.get(right));

			// 窗口扩张
			if(right < windowSize - 1){
				continue;
			}

			// update 数据
			if (isValid()) {
				process();
			}

			// out
			remove(data.get(left));
			left++;
		}
	}

}

