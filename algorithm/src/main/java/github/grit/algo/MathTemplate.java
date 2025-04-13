package github.grit.algo;

public class MathTemplate {

    /**
     * 快速幂
     *
     * @param base     底数
     * @param exponent 指数
     * @return 计算结果
     */
    public static int quickPower(int base, int exponent) {
        int result = 1;
        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result *= base;
            }
            base *= base;
            exponent >>= 1;
        }
        return result;
    }

}
