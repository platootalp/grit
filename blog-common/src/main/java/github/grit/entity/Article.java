package github.grit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文章表
 * </p>
 *
 * @author pulato
 * @since 2024-11-28 19:19:08
 */
@Getter
@Setter
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pkid", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private String status;

    /**
     * 内容
     */
    private String content;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 分类ID
     */
    private Integer categoryId;

    private LocalDateTime sysCreateTime;

    private LocalDateTime sysUpdateTime;

    private Boolean sysDeleted;
}
