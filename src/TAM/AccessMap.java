package TAM;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Lexx on 14.12.2015.
 */
public class AccessMap {

        private static AccessMap accessMap = new AccessMap();
        private HashMap<TAMsubj, HashMap<TAMobj, HashSet<TAM.AccessType>>> subjmap;
        private HashMap<TAMobj, HashMap<TAMsubj, HashSet<TAM.AccessType>>> objmap;
        protected TAMsubj admin;

        private AccessMap() {
            subjmap = new HashMap<>();
            objmap = new HashMap<>();
        }


        public boolean createObj(TAMsubj creator, String name) {
            TAMobj new_obj = new TAMobj(name);
            HashMap<TAMobj, HashSet<TAM.AccessType>> ownedObj = subjmap.get(creator);
            if (ownedObj != null) {
                Object access = objmap.get(new_obj);
                if (access == null) {
//            if (!objmap.containsKey(new_obj)){
                    HashSet<TAM.AccessType> neu = new HashSet<>();
                    neu.add(TAM.AccessType.OWN);
                    ownedObj.put(new_obj, neu);
//                ownedObj.put(new_obj, AccessType.READ);
//                ownedObj.put(new_obj, AccessType.WRITE);
                    objmap.put(new_obj, new HashMap<>());
                    objmap.get(new_obj).put(creator, neu);
//                objmap.get(new_obj).put(creator, AccessType.WRITE);
//                objmap.get(new_obj).put(creator, AccessType.READ);
                    return true;
                } else {
                    System.err.println("������ � ����� ������ ��� ��������������");
                    return false;
                }
            } else {
                System.err.println("���������� ������� ������. ��������� ������������" +
                        "�� ����������");
                return false;
            }
        }

        public void createAdm(String name, String password) {
            admin = new TAMsubj(name, password);
            subjmap.put(admin, new HashMap<>());
            HashSet<TAM.AccessType> neu = new HashSet<>();
            neu.add(TAM.AccessType.OWN);
            subjmap.get(admin).put(admin, neu);
        }

        public boolean createSubj(TAMsubj creator, String name, String password) {
            TAMsubj subj = new TAMsubj(name, password);
            if (creator.equals(admin)) {
                if (!subjmap.containsKey(subj)) {
                    HashSet<TAM.AccessType> neu = new HashSet<>();
                    neu.add(TAM.AccessType.OWN);
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
                    System.err.println("������� � ����� ������ ��� ���������������");
                    return false;
                }
            }
            else {
                System.err.println("������ ������������� ����� ����� �� �������� ��������.");
                return false;
            }
        }

        public int checkAccess(TAMsubj s, TAMobj o, TAM.AccessType access) throws IllegalAccessException {
            HashMap<TAMobj, HashSet<TAM.AccessType>> owned = subjmap.get(s);
            Iterator<HashSet<TAM.AccessType>> iterator = null;
            if (owned != null) {
                TAM.AccessType s_access = null;
                if (o == null) s_access = null;
                else {
                    if (owned.get(o).isEmpty()) System.err.println("������ �������.");
                    if (owned.get(o).contains(access))
                        return 0;
//                while (iterator.hasNext()) {
//                    s_access = (AccessType) owned.get(o).iterator().next();
//                    if (s_access == null) throw new IllegalStateException("������ �������.");
//                    if (access.compareTo(s_access) < 0) System.out.println("������ �������. ������������ ���� �������");
//                    return access.compareTo(s_access);
//                }
                }
            } //else throw new IllegalAccessException("������ �������. ������� � ������� �� ���������������");
            System.err.println("������ �������. ������� � ������� �� ���������������.");

            return -1;
        }

        public boolean destroySubj(TAMsubj executor, String name) throws IllegalStateException {
            TAMsubj subj = findSubj(name);
            try {
                if (checkAccess(executor, subj, TAM.AccessType.OWN) == 0) {
                    if (subjmap.containsKey(subj)) {
                        if (subjmap.get(executor).get(subj) != null)
                            subjmap.get(executor).remove(subj);
                        subjmap.remove(subj);
                        objmap.remove(subj);
                    } else
                        throw new IllegalArgumentException("������ ������� �������������� �������");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean destroyObj(TAMsubj executor, String name) {
            TAMobj obj = findObj(name);
            try {
                if (checkAccess(executor, obj, TAM.AccessType.OWN) == 0) {
                    if (objmap.containsKey(obj)) {
                        HashMap<TAMsubj, HashSet<TAM.AccessType>> owners = objmap.get(obj);
                        for (Object s : owners.keySet()) {
                            HashMap<TAMobj, HashSet<TAM.AccessType>> owned = subjmap.get(s);
                            owned.remove(obj);
                        }
                        objmap.remove(obj);
                        return true;
                    } else
                        throw new IllegalStateException("������ ������� �������������� ������");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean setAccess(TAMsubj executor, String subjname, String objname, TAM.AccessType accessType) {
            try {
                TAMobj o = findObj(objname);
                TAMsubj s = findSubj(subjname);
                if (checkAccess(executor, o, TAM.AccessType.OWN) == 0) {
                    HashMap<TAMobj, HashSet<TAM.AccessType>> accessMapHashMap;
                    if (s == null) accessMapHashMap = null;
                    else accessMapHashMap = subjmap.get(s);
                    if (accessMapHashMap != null) {
                        HashSet<TAM.AccessType> neu = accessMapHashMap.get(o);
                        if (neu == null)
                            neu = new HashSet<>();
                        neu.add(accessType);
                        accessMapHashMap.put(o, neu);
//                    objmap.get(o).put(s, accessType);
//                    HashMap<TAMsubj, AccessType> adding = new HashMap<>();
//                    adding.put(s, accessType);
                        HashMap<TAMsubj, HashSet<TAM.AccessType>> cur = objmap.get(o);
                        neu = cur.get(s);
                        if (neu == null)
                            neu = new HashSet<>();
                        neu.add(accessType);
                        cur.put(s, neu);
                        objmap.put(o, cur);
                        return true;
                    } else throw new IllegalArgumentException("���������� ���� ������ " +
                            "��������������������� ������������");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return false;
        }

        public boolean removeAccess(TAMsubj executor, String subjname, String objname) {
            TAMsubj s = findSubj(subjname);
            TAMobj o = findObj(objname);
            try {
                if (checkAccess(executor, o, TAM.AccessType.OWN) == 0) {
                    HashMap<TAMobj, HashSet<TAM.AccessType>> ownedObj = s == null ? null : subjmap.get(s);
                    if (ownedObj != null) {
                        ownedObj.remove(o);
                        objmap.get(o).remove(s);
                        return true;
                    } else
                        throw new IllegalArgumentException("��������� ������� � �������" +
                                " �� ���������������");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }

        public TAMobj findObj(String name) {
            for (TAMobj o : objmap.keySet()) {
                if (o.getName().equals(name))
                    return o;
            }
            return null;
        }

        public TAMsubj findSubj(String name) {
            for (TAMsubj s : subjmap.keySet()) {
                if (s.getName().equals(name))
                    return s;
            }
            return null;
        }

        public void open(TAMsubj taMsubj) {
            HashMap<TAMobj, HashSet<TAM.AccessType>> map = subjmap.get(taMsubj);


            for (TAMobj taMobj : map.keySet()) {
                String message = "[%s] ���: %s ��� �������: %s";
                String type = "object";
                if (taMobj instanceof TAMsubj)
                    type = "subject";
                for (TAM.AccessType accessType : map.get(taMobj))
                    System.out.println(String.format(message, type, taMobj.getName(),
                            accessType.getDesc()));

            }
        }

        public static AccessMap getInstance() {
            return accessMap;
        }
    }


