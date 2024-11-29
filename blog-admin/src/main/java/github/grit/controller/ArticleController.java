package github.grit.controller;


import github.grit.common.facade.Result;
import github.grit.common.request.ArticleCreateRequest;
import github.grit.service.IArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文章管理", description = "文章相关接口")
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

	private final IArticleService articleService;

	@Operation(summary = "创建文章", description = "创建一篇新文章")
	@PostMapping("/")
	public Result<Integer> createArticle(@RequestBody @Validated ArticleCreateRequest request){
		return Result.success(articleService.save(request));
	}
}
