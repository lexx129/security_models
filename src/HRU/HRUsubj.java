package HRU;

/**
 * Created by Lexx on 07.10.2015.
 */
public class HRUsubj extends HRUobj{
    protected String password;

    public HRUsubj(String name, String password) {
        super(name);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

}
