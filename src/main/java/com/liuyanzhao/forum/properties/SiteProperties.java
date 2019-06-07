package com.liuyanzhao.forum.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 言曌
 * @date 2018/4/8 下午10:46
 */

@Component
@Data
@ConfigurationProperties(prefix = "site")
public class SiteProperties {

}
