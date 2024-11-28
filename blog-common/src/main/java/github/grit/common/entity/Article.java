package github.grit.common.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(value = "article", autoResultMap = true)
@Accessors(chain = true)
public class Article {
	@TableId(value = "pkid",type = IdType.AUTO)
	private Integer id;

	@TableField("biz_id")
	private Long bizId;

	@TableField("biz_type")
	private String bizType;

	@TableField("type")
	private String type;

	@TableField("url")
	private String url;

	@TableField("sys_create_time")
	private Date sysCreateTime;

	@TableField("sys_update_time")
	private Date sysUpdateTime;

	@TableField("sys_deleted")
	private Boolean sysDeleted;
}
