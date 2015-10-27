package HRU;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Lexx on 07.10.2015.
 */
public class AccessMap {
    private static AccessMap accessMap = new AccessMap();
    private HashMap<HRUsubj, HashMap<HRUobj, HashSet<AccessType>>> subjmap;
    private HashMap<HRUobj, HashMap<HRUsubj, HashSet<AccessType>>> objmap;
    protected HRUsubj admin;

    private AccessMap() {
        subjmap = new HashMap<>();
        objmap = new HashMap<>();
    }


    public boolean createObj(HRUsubj creator, String name) {
        HRUobj new_obj = new HRUobj(name);
        HashMap<HRUobj, HashSet<AccessType>> ownedObj = subjmap.get(creator);
        if (ownedObj != null) {
            Object access = objmap.get(new_obj);
            if (access == null) {
//            if (!objmap.containsKey(new_obj)){
                HashSet<AccessType> neu = new HashSet<>();
                neu.add(AccessType.OWN);
                ownedObj.put(new_obj, neu);
//                ownedObj.put(new_obj, AccessType.READ);
//                ownedObj.put(new_obj, AccessType.WRITE);
                objmap.put(new_obj, new HashMap<>());
                objmap.get(new_obj).put(creator, neu);
//                objmap.get(new_obj).put(creator, AccessType.WRITE);
//                objmap.get(new_obj).put(creator, AccessType.READ);
                return true;
            } else {
                System.err.println("Объект с таким именем уже зарегестирован");
                return false;
            }
        } else {
            System.err.println("Невозможно создать объект. Указанный пользователь" +
                    "не существует");
            return false;
        }
    }

    public void createAdm(String name, String password) {
        admin = new HRUsubj(name, password);
        subjmap.put(admin, new HashMap<>());
        HashSet<AccessType> neu = new HashSet<>();
        neu.add(AccessType.OWN);
        subjmap.get(admin).put(admin, neu);
    }

    public boolean createSubj(HRUsubj creator, String name, String password) {
        HRUsubj subj = new HRUsubj(name, password);
        if (!subjmap.containsKey(subj)) {
            HashSet<AccessType> neu = new HashSet<>();
            neu.add(AccessType.OWN);
            subjmap.put(subj, new HashMap<>());
            subjmap.get(subj).put(subj, neu);
            if (creator != null)
                subjmap.get(creator).put(subj, neu);
            objmap.put(subj, new HashMap<>());

            objmap.get(subj).put(subj, neu);
            if (creator != null)
                subjmap.get(creator).put(subj, neu);
            return true;
        } else {
            System.err.println("Субъект с таким именем уже зарегистрирован");
            return false;
        }
    }

    public int checkAccess(HRUsubj s, HRUobj o, AccessType access) throws IllegalAccessException {
        HashMap<HRUobj, HashSet<AccessType>> owned = subjmap.get(s);
        Iterator<HashSet<AccessType>> iterator = null;
        if (owned != null) {
            AccessType s_access = null;
            if (o == null) s_access = null;
            else {
                if (owned.get(o).isEmpty()) System.err.println("Ошибка доступа.");
                if (owned.get(o).contains(access))
                    return 0;
//                while (iterator.hasNext()) {
//                    s_access = (AccessType) owned.get(o).iterator().next();
//                    if (s_access == null) throw new IllegalStateException("Ошибка доступа.");
//                    if (access.compareTo(s_access) < 0) System.out.println("Ошибка доступа. Недостаточно прав доступа");
//                    return access.compareTo(s_access);
//                }
            }
        } //else throw new IllegalAccessException("Ошибка доступа. Субъект в системе не зарегистрирован");
        System.err.println("Ощибка доступа. Субъект в системе не зарегестрирован.");

        return -1;
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
                    HashMap<HRUsubj, HashSet<AccessType>> owners = objmap.get(obj);
                    for (Object s : owners.keySet()) {
                        HashMap<HRUobj, HashSet<AccessType>> owned = subjmap.get(s);
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
                HashMap<HRUobj, HashSet<AccessType>> accessMapHashMap;
                if (s == null) accessMapHashMap = null;
                else accessMapHashMap = subjmap.get(s);
                if (accessMapHashMap != null) {
                    HashSet<AccessType> neu = accessMapHashMap.get(o);
                    if (neu == null)
                        neu = new HashSet<>();
                    neu.add(accessType);
                    accessMapHashMap.put(o, neu);
//                    objmap.get(o).put(s, accessType);
//                    HashMap<HRUsubj, AccessType> adding = new HashMap<>();
//                    adding.put(s, accessType);
                    HashMap<HRUsubj, HashSet<AccessType>> cur = objmap.get(o);
                    neu = cur.get(s);
                    if (neu == null)
                        neu = new HashSet<>();
                    neu.add(accessType);
                    cur.put(s, neu);
                    objmap.put(o, cur);
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
            if (checkAccess(executor, o, AccessType.OWN) == 0) {
                HashMap<HRUobj, HashSet<AccessType>> ownedObj = s == null ? null : subjmap.get(s);
                if (ownedObj != null) {
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

    public void open(HRUsubj hrUsubj) {
        HashMap<HRUobj, HashSet<AccessType>> map = subjmap.get(hrUsubj);


        for (HRUobj hrUobj : map.keySet()) {
            String message = "[%s] имя: %s тип доступа: %s";
            String type = "object";
            if (hrUobj instanceof HRUsubj)
                type = "subject";
            for (AccessType accessType : map.get(hrUobj))
                System.out.println(String.format(message, type, hrUobj.getName(),
                        accessType.getDesc()));

        }
    }

    public static AccessMap getInstance() {
        return accessMap;
    }
}
