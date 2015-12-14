package TAM;

/**
 * Created by Lexx on 14.12.2015.
 */
public enum SubjectType {
     ADMIN("a"), USER("u");
    String desc;

    SubjectType(String user) {
        desc = user;

    }

    public String getDesc() {
        return desc;
    }
}
