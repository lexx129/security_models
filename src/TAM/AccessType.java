package TAM;

/**
 * Created by Lexx on 14.12.2015.
 */
public enum AccessType {
    READ("READ"), WRITE("WRITE"), OWN("OWN"), EXECUTE("EXE");
    String desc;
    AccessType(String own) {
        desc = own;
    }

    public String getDesc() {
        return desc;
    }
}
