package github.grit.controller;


import github.grit.common.facade.Result;
import github.grit.common.request.ArticleCreateRequest;
import github.grit.service.IArticleService;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

	private final IArticleService articleService;

	@PostMapping("/")
	public Result<Integer> saveArticle(@RequestBody @Validated ArticleCreateRequest request){
		return Result.success(articleService.save(request));
	}
}
