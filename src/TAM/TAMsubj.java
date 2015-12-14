package TAM;

/**
 * Created by Lexx on 14.12.2015.
 */
public class TAMsubj extends TAMobj{
    private String password;

    public TAMsubj(String name, String password, String type) {
        super(name, type);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean createSubj(String name, String password, String type){
        return TAM.AccessMap.getInstance().createSubj(this, name, password, type);
    }
    public boolean createObj (String name, String type) {
        return TAM.AccessMap.getInstance().createObj(this, name, type);
    }
    public boolean destroySubj(String name) throws IllegalStateException{
        return TAM.AccessMap.getInstance().destroySubj(this, name);
    }
    public boolean destroyObj (String name) throws IllegalStateException{
        return TAM.AccessMap.getInstance().destroyObj(this, name);
    }
    public boolean addAccess (String subj, String obj, TAM.AccessType accessType){
        return TAM.AccessMap.getInstance().setAccess(this, subj, obj, accessType);
    }
    public boolean removeAccess (String subj, String obj) {
        return TAM.AccessMap.getInstance().removeAccess(this, subj, obj);
    }
    public void open(){
        AccessMap.getInstance().open(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TAMobj TAMobj = (TAMobj) o;
        return name.equals(TAMobj.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + 31 * password.hashCode();
    }
}
