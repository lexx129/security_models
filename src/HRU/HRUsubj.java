package HRU;

/**
 * Created by Lexx on 07.10.2015.
 */
public class HRUsubj extends HRUobj{
    private String password;

    public HRUsubj(String name, String password) {
        super(name);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean createSubj(String name, String password){
        return AccessMap.getInstance().createSubj(this, name, password);
    }
    public boolean createObj (String name) throws IllegalAccessException {
        return AccessMap.getInstance().createObj(this, new HRUobj(name));
    }
    public boolean destroySubj(String name) throws IllegalStateException{
        return AccessMap.getInstance().destroySubj(this, name);
    }
    public boolean destroyObj (String name) throws IllegalStateException{
        return AccessMap.getInstance().destroyObj(this, name);
    }
    public boolean addAccess (String subj, String obj, AccessType accessType){
        return AccessMap.getInstance().setAccess(this, subj, obj, accessType);
    }
    public boolean removeAccess (String subj, String obj) {
        return AccessMap.getInstance().removeAccess(this, subj, obj);
    }
    public void open(){
        AccessMap.getInstance().open(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HRUobj hrUobj = (HRUobj) o;
        return name.equals(hrUobj.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + 31 * password.hashCode();
    }
}
