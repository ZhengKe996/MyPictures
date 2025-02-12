package fun.timu.init.model.vo;


import io.swagger.annotations.License;
import lombok.Data;

@Data
public class GithubRepoVO {
    private int id;
    private String name;
    private String full_name;
    private String html_url;
    private String description;
    private int stargazers_count;
    private String language;
    private boolean fork;
    private String created_at;
    private String updated_at;
}