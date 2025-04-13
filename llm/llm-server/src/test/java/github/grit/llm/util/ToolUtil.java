package github.grit.llm.util;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class ToolUtil {

    @Tool(value = {"快速计算 base^exponent的结果"})
    public int quickPower(@P(value = "底数") int base, @P("指数") int exponent) {
        int initialBase = base;
        int initialExponent = exponent;

        int result = 1;
        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result *= base;
            }
            base *= base;
            exponent >>= 1;
        }
        System.out.println("[TOOL CALL] 调用了 fastPow 工具, 参数: a=" +  initialBase + ", b=" + initialExponent);
        return result;
    }

}
