package TAM;

/**
 * Created by Lexx on 14.12.2015.
 */
public class TAMobj {
    protected String name;

    public TAMobj(String name){
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
        TAMobj obj = (TAMobj) o;
        return name.equals(obj.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
