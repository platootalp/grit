package github.grit.service;

import github.common.request.ArticleCreateRequest;

public interface ArticleService {

	Integer save(ArticleCreateRequest request);
}
