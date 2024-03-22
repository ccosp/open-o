package oscar;

import org.oscarehr.common.UserDao;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.oscarehr.common.Secrole;

@Controller
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String getUsers(Model model) {
        List<Secrole> users = userDao.getRoles();
        model.addAttribute("users", users);
        return "user"; // View name
    }
}