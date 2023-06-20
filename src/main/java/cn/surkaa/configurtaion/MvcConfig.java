package cn.surkaa.configurtaion;

import cn.surkaa.interceptor.CheckLoginInterceptor;
import cn.surkaa.interceptor.UserAgentInterceptor;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author SurKaa
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // mybatis-plus 拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加记录所有请求的拦截器
        registry.addInterceptor(new UserAgentInterceptor());
        // 添加检查请求是否登录的拦截器
        registry.addInterceptor(new CheckLoginInterceptor());
    }
}
