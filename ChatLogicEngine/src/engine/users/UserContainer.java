package engine.users;

/*
Adding and retrieving users is synchronized and for that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 *
 */
public class UserContainer{
    String Name;
    Boolean IsComputer;

    public UserContainer(String name, Boolean IsComputer){
        this.Name = name;
        this.IsComputer = IsComputer;
    }
}
