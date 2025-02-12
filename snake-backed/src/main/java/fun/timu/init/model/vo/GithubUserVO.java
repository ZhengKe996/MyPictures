package fun.timu.init.model.vo;


import lombok.Data;

import java.util.Date;

@Data
public class GithubUserVO {
    private String login;
    private Long id;
    private String node_id;
    private String avatar_url;
    private String gravatar_id;
    private String url;
    private String html_url;
    private String followers_url;
    private String following_url;
    private String gists_url;
    private String starred_url;
    private String subscriptions_url;
    private String organizations_url;
    private String repos_url;
    private String events_url;
    private String received_events_url;
    private String type;
    private String user_view_type;
    private Boolean site_admin;
    private String name;
    private String company;
    private String blog;
    private String location;
    private String email;
    private Boolean hireable;
    private String bio;
    private String twitter_username;
    private Integer public_repos;
    private Integer public_gists;
    private Integer followers;
    private Integer following;
    private Date created_at;
    private Date updated_at;
}
