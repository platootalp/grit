package github.grit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.grit.entity.User;
import github.grit.mapper.UserMapper;
import github.grit.service.IUserService;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author pulato
 * @since 2024-11-28 19:19:08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
