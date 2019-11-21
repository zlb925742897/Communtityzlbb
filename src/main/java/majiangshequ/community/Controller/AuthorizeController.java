package majiangshequ.community.Controller;

import majiangshequ.community.Model.User;
import majiangshequ.community.dto.AccessTokenDto;
import majiangshequ.community.dto.GitHubUser;
import majiangshequ.community.provider.GitHubProvider;
import majiangshequ.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
     private GitHubProvider gitHubProvider;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String  clientSecret;
    @Value("${github.redirect_uri}")
    private String  redirectUri;
    @Autowired
    private UserMapper userMapper;
    @RequestMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request){
        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setClient_id(clientId);
        accessTokenDto.setClient_secret(clientSecret);
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri(redirectUri);
        accessTokenDto.setState(state);
        String accessToken = gitHubProvider.getAccessToken(accessTokenDto);
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);
        if(gitHubUser!=null){
            //如果不为空应该存入数据库中
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(gitHubUser.getName());
            user.setAccountId(String.valueOf(gitHubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            //如果发现user不为空,说明用户已经登录了 那么就需要将登录按键变成用户名
            HttpSession session = request.getSession();
            //把user存入到session中
            session.setAttribute("user",gitHubUser);
            return "redirect:/";
        }
        return "redirect:/";
    }

}
