package HRU;

/**
 * Created by Lexx on 07.10.2015.
 */
public class HRUobj {
    protected String name;

    public HRUobj(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HRUobj obj = (HRUobj) o;
        return name.equals(obj.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
