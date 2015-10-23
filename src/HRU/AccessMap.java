package HRU;

import java.util.HashMap;

/**
 * Created by Lexx on 07.10.2015.
 */
public class AccessMap {
    private static AccessMap accessMap = new AccessMap();
    private HashMap<HRUsubj, HashMap<HRUobj, AccessType>> subjmap;
    private HashMap<HRUobj, HashMap<HRUsubj, AccessType>> objmap;

    private AccessMap() {
        subjmap = new HashMap<>();
        objmap = new HashMap<>();
    }

    public boolean createObj(HRUsubj creator, HRUobj object) throws IllegalAccessException {
        HashMap<HRUobj, AccessType> ownedObj = subjmap.get(creator);
        if (ownedObj != null) {
            Object access = ownedObj.get(object.getName());
            if (access == null) {
                ownedObj.put(object, AccessType.OWN);
                objmap.put(object, new HashMap<>());
                objmap.get(object).put(creator, AccessType.OWN);
                return true;
            } else throw new IllegalStateException("Объект с таким именем уже зарегестирован");
        } else throw new IllegalAccessException("Невозможно создать объект. Указанный пользователь" +
                "не существует");
    }

    public boolean createSubj(HRUsubj creator, String name, String password) throws IllegalStateException {
        HRUsubj subj = new HRUsubj(name, password);
        if (!subjmap.containsKey(subj)) {
            subjmap.put(subj, new HashMap<>());
            subjmap.get(subj).put(subj, AccessType.OWN);
            if (creator != null)
                subjmap.get(creator).put(subj, AccessType.OWN);
            objmap.put(subj, new HashMap<>());
            objmap.get(subj).put(subj, AccessType.OWN);
            if (creator != null)
                subjmap.get(creator).put(subj, AccessType.OWN);
            return true;
        } else throw new IllegalStateException("Субъект с таким именем уже зарегистрирован");
    }

    public int checkAccess(HRUsubj s, HRUobj o, AccessType access) throws IllegalAccessException {
        HashMap<HRUobj, AccessType> owned = subjmap.get(s);
        if (owned != null) {
            AccessType s_access = o == null ? null : (AccessType) owned.get(o);
            if (s_access == null) throw new IllegalStateException("Ошибка доступа.");
            if (access.compareTo(s_access) < 0) System.out.println("Ошибка доступа. Недостаточно прав доступа");
            return access.compareTo(s_access);
        } else throw new IllegalAccessException("Ошибка доступа. Субъект в системе не зарегистрирован");
    }

    public boolean destroySubj(HRUsubj executor, String name) throws IllegalStateException {
        HRUsubj subj = findSubj(name);
        try {
            if (checkAccess(executor, subj, AccessType.OWN) == 0) {
                if (subjmap.containsKey(subj)) {
                    if (subjmap.get(executor).get(subj) != null)
                        subjmap.get(executor).remove(subj);
                    subjmap.remove(subj);
                    objmap.remove(subj);
                } else
                    throw new IllegalArgumentException("Нельзя удалить несуществующий субъект");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean destroyObj(HRUsubj executor, String name) {
        HRUobj obj = findObj(name);
        try {
            if (checkAccess(executor, obj, AccessType.OWN) == 0) {
                if (objmap.containsKey(obj)) {
                    HashMap<HRUsubj, AccessType> owners = objmap.get(obj);
                    for (Object s : owners.keySet()) {
                        HashMap<HRUobj, AccessType> owned = subjmap.get(s);
                        owned.remove(obj);
                    }
                    objmap.remove(obj);
                    return true;
                } else
                    throw new IllegalStateException("Нельзя удалить несуществующий объект");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setAccess(HRUsubj executor, String subjname, String objname, AccessType accessType) {
        try {
            HRUobj o = findObj(objname);
            HRUsubj s = findSubj(subjname);
            if (checkAccess(executor, o, AccessType.OWN) == 0) {
                HashMap<HRUobj, AccessType> accessMapHashMap = s == null ? null : subjmap.get(s);
                if (accessMapHashMap != null) {
                    accessMapHashMap.put(o, accessType);
                    objmap.get(o).put(s, accessType);
                    return true;
                } else throw new IllegalArgumentException("Невозможно дать доступ " +
                        "незарегистрированному пользователю");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeAccess(HRUsubj executor, String subjname, String objname) {
        HRUsubj s = findSubj(subjname);
        HRUobj o = findObj(objname);
        try {
            if (checkAccess(executor, o, AccessType.OWN) == 0){
                HashMap<HRUobj, AccessType> ownedObj = s == null ? null : subjmap.get(s);
                if (ownedObj != null){
                    ownedObj.remove(o);
                    objmap.get(o).remove(s);
                    return true;
                } else
                    throw new IllegalArgumentException("Указанный субъект в системе" +
                            " не зарегестрирован");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HRUobj findObj(String name) {
        for (HRUobj o : objmap.keySet()) {
            if (o.getName().equals(name))
                return o;
        }
        return null;
    }

    public HRUsubj findSubj(String name) {
        for (HRUsubj s : subjmap.keySet()) {
            if (s.getName().equals(name))
                return s;
        }
        return null;
    }

    public void open(HRUsubj hrUsubj){
        HashMap<HRUobj, AccessType> map = subjmap.get(hrUsubj);
        for (HRUobj hrUobj : map.keySet()){
            String message = "[%s] имя: %s тип доступа: %s";
            String type = "object";
            if (hrUobj instanceof HRUsubj)
                type = "subject";
            System.out.println(String.format(message, type, hrUobj.getName(),
                    map.get(hrUobj).getDesc()));
        }
    }

    public static AccessMap getInstance() {
        return accessMap;
    }
}
