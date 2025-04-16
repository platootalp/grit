package github.grit.algo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class SlidingWindowTemplate {

	/**
	 * 定长滑动窗口
	 * @param data
	 * @param windowSize
	 * @param add
	 * @param remove
	 * @param process
	 * @param isValid
	 * @param <T>
	 */
	public static <T> void slidingWindow(List<T> data, int windowSize,
										 Consumer<T> add,
										 Consumer<T> remove,
										 Runnable process,
										 Supplier<Boolean> isValid) {
		int left = 0;
		for (int right = 0; right < data.size(); right++) {
			add.accept(data.get(right));

			if (right < windowSize - 1)
				continue;

			if (isValid.get())
				process.run();

			remove.accept(data.get(left));
			left++;
		}
	}

	/**
	 * 求最长/最大子数组模板
	 * @param data
	 * @param add
	 * @param remove
	 * @param isValid
	 * @param update
	 * @param <T>
	 */
	public static <T> void variableSlidingWindow(List<T> data,
												 Consumer<T> add,
												 Consumer<T> remove,
												 Supplier<Boolean> isValid,
												 Runnable update) {

		int left = 0;
		for (int right = 0; right < data.size(); right++) {
			add.accept(data.get(right));
			// 不合法的窗口进行收缩
			while (!isValid.get()) {
				remove.accept(data.get(left++));
			}
			// 合法窗口处理
			update.run();
		}
	}

	/**
	 * 求最长子串 （leetcode3090）
	 * @param s
	 * @return
	 */
	public int maximumLengthSubstring(String s) {
		int result = 0;

		char[] cs = s.toCharArray();
		int left = 0;
		int[] ccount = new int[26];
		for (int i = 0; i < cs.length; i++) {
			ccount[cs[i] - 'a']++;
			while(ccount[cs[i] - 'a'] > 2){
				// 缩小窗口
				ccount[cs[left] - 'a']--;
				left++;
			}
			result = Math.max(result, i - left + 1);
		}
		return result;
	}

	public static int maxFreq(String s, int maxLetters, int minSize, int maxSize) {
		List<Character> data = s.chars().mapToObj(c -> (char) c).collect(Collectors.toList());

		Map<Character, Integer> charCountMap = new HashMap<>();
		StringBuilder windowStr = new StringBuilder();
		Map<String, Integer> freqMap = new HashMap<>();
		int[] maxFreq = {0}; // 用数组包裹做引用变量

		slidingWindow(
				data, minSize,
				c -> { // add
					charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
					windowStr.append(c);
				},
				c -> { // remove
					charCountMap.put(c, charCountMap.get(c) - 1);
					if (charCountMap.get(c) == 0)
						charCountMap.remove(c);
					windowStr.deleteCharAt(0);
				},
				() -> {
					String str = windowStr.toString();
					freqMap.put(str, freqMap.getOrDefault(str, 0) + 1);
					maxFreq[0] = Math.max(maxFreq[0], freqMap.get(str));
				},
				() -> charCountMap.size() <= maxLetters
		);

		return maxFreq[0];
	}

	public static void main(String[] args) {

		int count = maxFreq("aabcabcab", 2, 2, 3);
		System.out.println(count);
	}
}
