package github.grit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author pulato
 * @since 2024-11-28 19:19:08
 */
@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pkid", type = IdType.AUTO)
    private Integer id;

    /**
     * 账户
     */
    private String account;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    private LocalDateTime sysCreateTime;

    private LocalDateTime sysUpdateTime;

    private Boolean sysDeleted;
}
