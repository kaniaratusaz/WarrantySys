package service;

import dao.UserDAO;
import model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private static User loggedInUser;

    public User login(String username, String password) throws Exception {
        User user = userDAO.login(username, password);
        if (user == null) {
            throw new Exception("Username atau password salah.");
        }
        loggedInUser = user;
        return user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}
