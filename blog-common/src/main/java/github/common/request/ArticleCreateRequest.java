package github.common.request;

import lombok.Data;

@Data
public class ArticleCreateRequest {
	private String title;
	private String description;
	private String content;
	private String status;

}
