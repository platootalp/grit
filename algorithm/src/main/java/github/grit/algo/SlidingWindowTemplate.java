package github.grit.algo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class SlidingWindowTemplate {

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
					if (charCountMap.get(c) == 0) charCountMap.remove(c);
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
