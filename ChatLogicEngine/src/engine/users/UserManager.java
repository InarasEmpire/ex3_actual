package  engine.users;

import java.util.*;

public class UserManager {
    private final Set<UserContainer> usersList ;

    public UserManager() {
        usersList = new HashSet<>();
    }

    public synchronized void addUser(String username, Boolean type) {
        usersList.add(new UserContainer(username, type));
    }

    public synchronized void removeUser(String username) {
        for (Iterator<UserContainer> iter = usersList.iterator(); iter.hasNext(); ) {
            UserContainer currUser = iter.next();
            if (currUser.Name == username) {
                iter.remove();
                return;
            }
        }
    }

    public synchronized Set<String> getUsers(){
        Set<String> names = new HashSet<>();
        for (Iterator<UserContainer> iter = usersList.iterator(); iter.hasNext(); ) {
            UserContainer currUser = iter.next();
            names.add(currUser.Name);
        }

        return names;
    }

    public synchronized Set<UserContainer> getUsersContainers()
    {
        return Collections.unmodifiableSet(usersList);
    }

    public boolean isUserExists(String username) {
        for (Iterator<UserContainer> iter = usersList.iterator(); iter.hasNext(); ) {
            UserContainer currUser = iter.next();
            if (currUser.Name.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean getUserType(String username)
    {
        for (Iterator<UserContainer> iter = usersList.iterator(); iter.hasNext(); ) {
            UserContainer currUser = iter.next();
            if (currUser.Name == username) {
                return currUser.IsComputer;
            }
        }

        return false;
    }

}
