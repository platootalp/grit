package github.grit.common.request;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "文章创建请求数据")
@Data
public class ArticleCreateRequest {

	@Schema(description = "文章标题", example = "Spring Boot教程")
	@NotBlank(message = "标题不能为空")
	private String title;

	@Schema(description = "文章描述", example = "这是一篇关于Spring Boot的入门教程")
	private String description;

	@Schema(description = "文章内容", example = "本文将详细介绍Spring Boot的核心特性")
	@NotBlank(message = "内容不能为空")
	private String content;

	@Schema(description = "文章状态", example = "PUBLISHED", allowableValues = {"DRAFT", "PUBLISHED", "ARCHIVED"})
	private String status;
}
