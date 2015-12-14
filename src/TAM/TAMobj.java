package TAM;

/**
 * Created by Lexx on 14.12.2015.
 */
public class TAMobj {
    protected String name;
    protected String type;

    public TAMobj(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
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
        return name.equals(obj.name) && type.equals(obj.type);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
