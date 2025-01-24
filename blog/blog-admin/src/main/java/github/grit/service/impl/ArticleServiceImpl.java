package github.grit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.grit.common.request.ArticleCreateRequest;
import github.grit.common.response.ArticleDetailResponse;
import github.grit.entity.Article;
import github.grit.mapper.ArticleMapper;
import github.grit.service.IArticleService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 * @author pulato
 * @since 2024-11-28 19:19:08
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

	private final ArticleMapper articleMapper;

	@Override
	public Integer save(ArticleCreateRequest request) {
		Article article = new Article();
		BeanUtils.copyProperties(request,article);
		return articleMapper.insert(article);
	}

	@Override
	public ArticleDetailResponse get(Integer id) {
		return new ArticleDetailResponse();
	}
}
