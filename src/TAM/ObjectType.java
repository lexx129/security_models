package TAM;

/**
 * Created by Lexx on 14.12.2015.
 */
public enum ObjectType {
    SECRET("v"), NOSECRET("n");

    String desc;

    ObjectType(String n) {
        desc = n;
    }

    public String getDesc() {
        return desc;
    }
}
