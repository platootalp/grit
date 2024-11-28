package github.grit.controller;


import github.common.facade.Result;
import github.common.request.ArticleCreateRequest;
import github.grit.service.ArticleService;
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

	private final ArticleService articleService;

	@PostMapping("/")
	public Result<Integer> saveArticle(@RequestBody @Validated ArticleCreateRequest request){
		return Result.success(articleService.save(request));
	}
}
