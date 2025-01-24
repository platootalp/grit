package github.grit;


import java.util.Collections;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author Moncoder
 * @version 1.0
 * @description MBG代码生成工具
 * @date 2024/3/20 13:34
 */
public class Generator {

	private static final String URL = "jdbc:mysql://127.0.0.1:3306/blog?" +
			"characterEncoding=UTF-8&useUnicode=true&useSSL=false";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	public static void main(String[] args) {
		FastAutoGenerator.create(URL, USERNAME, PASSWORD)
				.globalConfig(builder -> {
					builder.author("pulato") // 设置作者
//							.enableSwagger() // 开启 swagger 模式
							.commentDate("yyyy-MM-dd HH:mm:ss")   //注释日期
							.outputDir("blog-common/src/main/java") // 指定输出目录
							.disableOpenDir(); //禁止打开输出目录，默认打开
				})
				// 包配置
				.packageConfig(builder -> {
					builder.parent("github.grit")  // 设置父包名
							// 设置mapperXml生成路径
							.pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") +
									"/blog-common/src/main/resources/github/grit/mapper"));
				})
				.strategyConfig(builder -> {
					builder
							// 设置需要生成的表名(所有表)
							.addInclude("article","user")
							.addTablePrefix("sys_","wms_")
							// 1.entity配置
							.entityBuilder()
							.enableFileOverride()
							.enableLombok()
							// 数据库表映射到实体的命名策略：下划线转驼峰命
							.naming(NamingStrategy.underline_to_camel)
							// 数据库表字段映射到实体的命名策略：下划线转驼峰命
							.columnNaming(NamingStrategy.underline_to_camel)
							// 2.mapper配置
							.mapperBuilder()
							.enableFileOverride()
							// 3.service配置
							.serviceBuilder()
							.enableFileOverride()
							// 格式化 service 接口文件名称，%s进行匹配表名，如 UserService
							.formatServiceFileName("I%sService")
							// 格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl
							.formatServiceImplFileName("%sServiceImpl")
							// 4.controller配置
							.controllerBuilder()
							.enableFileOverride()
							.enableRestStyle();
				})
				.execute();
	}
}
