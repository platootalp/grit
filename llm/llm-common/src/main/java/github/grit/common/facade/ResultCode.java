package github.grit.common.facade;


/**
 * @author lenovo
 * @version 1.0
 * @description 返回结果状态码及信息
 * @date 2024/3/9 16:35
 */
public enum ResultCode implements ICode {

	SUCCESS(200, "操作成功"),
	// 重定向
	REDIRECT(301, "redirect"),
	BADREQUEST(400, "请求错误"),
	UNAUTHORIZED(401, "未登录或token已经过期"),
	FORBIDDEN(403, "无权限访问"),
	NOT_FOUND(404, "资源不存在"),
	ERROR(500, "服务器错误"),
	;
	/**
	 * 状态码
	 */
	private Integer code;
	/**
	 * 提示消息
	 */
	private String message;

	ResultCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public Integer getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}

