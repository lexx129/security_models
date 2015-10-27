package HRU;

/**
 * Created by Lexx on 07.10.2015.
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
