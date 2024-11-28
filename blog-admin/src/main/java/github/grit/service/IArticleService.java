package github.grit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.grit.common.request.ArticleCreateRequest;
import github.grit.entity.Article;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 * @author pulato
 * @since 2024-11-28 19:19:08
 */
public interface IArticleService extends IService<Article> {
	Integer save(ArticleCreateRequest request);
}
