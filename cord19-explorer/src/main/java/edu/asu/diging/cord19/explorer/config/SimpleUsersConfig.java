package edu.asu.diging.cord19.explorer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import edu.asu.diging.simpleusers.core.config.SimpleUsers;
import edu.asu.diging.simpleusers.core.config.SimpleUsersConfiguration;

@Configuration
public class SimpleUsersConfig implements SimpleUsersConfiguration {

    @Value("${email_user}")
    private String emailUser;

    @Value("${email_password}")
    private String emailPassword;

    @Value("${email_host}")
    private String emailHost;

    @Value("${email_port}")
    private String emailPort;

    @Value("${email_from}")
    private String emailFrom;

    @Value("${app_url}")
    private String appUrl;

    @Override
    public void configure(SimpleUsers simpleUsers) {
        simpleUsers.usersEndpointPrefix("/admin/user/").userListView("admin/user/list").emailUsername(emailUser)
                .emailPassword(emailPassword).emailServerHost(emailHost).emailServerPort(emailPort).emailFrom(emailFrom)
                .instanceUrl(appUrl);
    }
}