package TAM;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Lexx on 14.12.2015.
 */
public class Main {
    public static TAMsubj current_subj;
    private static String[] subjTypes = new String[]{"admin", "user"};
    private static String[] objTypes = new String[]{"secret", "nonsecret"};
    private static HashMap<String, HashSet<String>> dependencies = new HashMap<>();
    private static boolean secure = false;

    public static void main(String[] args) {
        System.out.println("***TAM security model***\n");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter command: ");
            String command = scanner.next();
            switch (command) {
                case "create_subj": {
                    createSubj(scanner);
                    break;
                }
                case "create_obj": {
                    createObj(scanner);
                    break;
                }
                case "destroy_obj": {
                    destroyObj(scanner);
                    break;
                }
                case "destroy_subj": {
                    destroySubj(scanner);
                    break;
                }
                case "login": {
                    login(scanner);
                    break;
                }
                case "set_access": {
                    setAccess(scanner);
                    break;
                }
                case "remove_access": {
                    removeAccess(scanner);
                    break;
                }
                case "open": {
                    if (current_subj != null) {
                        System.out.println("**Current access matrix for " + current_subj.getName() + "**");
                        current_subj.open();
                    } else System.err.println("You need to login first.");
                    break;
                }
                case "print_dep": {
                    printDep();
                    break;
                }
                case "exit": {
                    System.exit(0);
                }
                case "scenario": {
                    scenario();
                    break;
                }
                case "whoami": {
                    System.out.println(current_subj.getName());
                    break;
                }
                default:
                    System.err.println("Unknown command.");
                    break;
            }
        }
    }

    private static void removeAccess(Scanner scanner) {
        System.out.println("From what subject do we remove access?");
        String subj = scanner.next();
        System.out.println("On what object?");
        String obj = scanner.next();
        if (current_subj != null) {
            current_subj.removeAccess(subj, obj);
            System.out.println("**Access removed.**");
        } else System.err.println("You need to login first.");
    }

    private static void setAccess(Scanner scanner) {
        System.out.println("To what subject do we get access? ");
        String subj_name = scanner.next();
        System.out.println("On what object? ");
        String obj_name = scanner.next();
        System.out.println("What kind of access? ");
        String access_type = scanner.next();
        AccessType accessType;
        switch (access_type) {
            case "write":
                accessType = AccessType.WRITE;
                break;
            case "read":
                accessType = AccessType.READ;
                break;
            case "own":
                accessType = AccessType.OWN;
                break;
            case "exe":
                accessType = AccessType.EXECUTE;
                break;
            default:
                System.err.println("**Unknown access type.**");
                return;
        }
        if (current_subj != null) {
            if (current_subj.addAccess(subj_name, obj_name, accessType)) {
                TAMsubj father = AccessMap.getInstance().findSubj(subj_name);
                TAMobj son = AccessMap.getInstance().findObj(obj_name);
                if (dependencies.get(father.getType()) == null) {
                    HashSet<String> neu = new HashSet<>();
                    neu.add(son.getType());
                    dependencies.put(father.getType(), neu);
                } else if (!dependencies.get(father.getType()).contains(son.getType()))
                    dependencies.get(father.getType()).add(son.getType());
                System.out.println("**Access granted.**");
            }
        } else
            System.err.println("You need to login first.");
    }

    private static void setFullAccess(Scanner scanner) {
        System.out.println("To what subject do we get access? ");
        String subj_name = scanner.next();
        System.out.println("On what object? ");
        String obj_name = scanner.next();
        HashSet<AccessType> types = new HashSet<>();
        types.add(AccessType.READ);
        types.add(AccessType.WRITE);
        types.add(AccessType.EXECUTE);
        if (current_subj != null) {
            for (AccessType type : types) {
                if (current_subj.addAccess(subj_name, obj_name, type)) {
                    TAMsubj father = AccessMap.getInstance().findSubj(subj_name);
                    TAMobj son = AccessMap.getInstance().findObj(obj_name);
                    if (secure) {
                        if (father.getType().equals("admin") && son.getType().equals("nonsecret")) {
                            System.err.println("nonsecret type cannot be a child of admin type!");
//                            System.exit(-1);
                            return;
                        }
                    }
                    if (dependencies.get(father.getType()) == null) {
                        HashSet<String> neu = new HashSet<>();
                        neu.add(son.getType());
                        dependencies.put(father.getType(), neu);
                    } else if (!dependencies.get(father.getType()).contains(son.getType()))
                        dependencies.get(father.getType()).add(son.getType());
                    System.out.println("**Access granted.**");
                }
            }
        } else
            System.err.println("You need to login first.");
    }

    private static void login(Scanner scanner) {
        System.out.println("Username: ");
        String username = scanner.next();
        System.out.println("Password: ");
        String password = scanner.next();
        TAMsubj taMsubj = AccessMap.getInstance().findSubj(username);
        if (taMsubj != null && taMsubj.getName().equals(username) &&
                taMsubj.getPassword().equals(password)) {
            System.out.println("Successfully logged in as " + username);
            current_subj = taMsubj;
        } else System.err.println("Wrong username or password");
    }

    private static void destroySubj(Scanner scanner) {
//        if (current_subj == AccessMap.getInstance().admin) {
//            System.out.println("??? ?????????? ????????: ");
        System.out.println("What subject do you want to destroy? ");
        String subj_name = scanner.next();
        TAMsubj temp = AccessMap.getInstance().findSubj(subj_name);
        if (current_subj.destroySubj(subj_name)) {
            if (dependencies.get(subj_name) != null) {
                dependencies.remove(subj_name);
                System.out.println("Dependencies for subject " + subj_name + " are deleted.");
            }
            clearDep(temp.getType());
            System.out.println("Subject " + subj_name + " is successfully destroyed.");
        } else System.err.println("Access denied.");
    }
//    }

    private static void destroyObj(Scanner scan) {
        System.out.println("What object do you want to destroy? ");
        String obj_name = scan.next();
        if (current_subj != null) {
            TAMobj temp = AccessMap.getInstance().findObj(obj_name);
            if (current_subj.destroyObj(obj_name)) {
                if (dependencies.get(obj_name) != null) {
                    dependencies.remove(obj_name);
                    System.out.println("Dependencies for subject " + obj_name + " are deleted.");
                }
                clearDep(temp.getType());
                System.out.println("Object " + obj_name + " is successfully destroyed.");
            }
        } else System.err.println("Access denied.");
    }

    private static void createObj(Scanner scan) {
        System.out.println("Enter new object's name: ");
        String obj_name = scan.next();
        System.out.println("Enter new object's type: ");
        String type = scan.next();
        while (!Arrays.asList(objTypes).contains(type)) {
            System.err.println("Unknown object type");
            System.out.println("Enter proper object type: ");
            type = scan.next();
        }
        if (current_subj != null) {
            current_subj.createObj(obj_name, type);
            TAMobj fresh = AccessMap.getInstance().findObj(obj_name);
            if ((dependencies.get(current_subj.getType()) == null)) { //|| !dependencies.get(current_subj.getType()).contains(fresh.getType())) {
                HashSet<String> neu = new HashSet<>();
                neu.add(fresh.getType());
                dependencies.put(current_subj.getType(), neu);
            } else if (!dependencies.get(current_subj.getType()).contains(fresh.getType())) {
                dependencies.get(current_subj.getType()).add(fresh.getType());
               /* System.out.println("Dependence for object type " + fresh.getType()
                        + " in subject " + current_subj.getType() + " is added");*/
            }
            System.out.println("Success.");
        } else System.err.println("You need to login first.");
    }

    private static void createSubj(Scanner scan) {
//        if (current_subj == AccessMap.getInstance().admin) {
        System.out.println("Enter new subject's name: ");
        String username = scan.next();
        System.out.println("Enter new subject's password: ");
        String password = scan.next();
        System.out.println("Enter new subject's type: ");
        String type = scan.next();
        while (!Arrays.asList(subjTypes).contains(type)) {
            System.err.println("Unknown subject type");
            System.out.println("Enter proper subject type: ");
            type = scan.next();
        }
        AccessMap.getInstance().createSubj(current_subj, username, password, type);
        TAMsubj fresh = AccessMap.getInstance().findSubj(username);
        if (current_subj != null) {
            if ((dependencies.get(current_subj.getType()) == null)) { //|| !dependencies.get(current_subj.getType()).contains(fresh.getType())) {
                HashSet<String> neu = new HashSet<>();
                neu.add(fresh.getType());
                dependencies.put(current_subj.getType(), neu);
            } else if (!dependencies.get(current_subj.getType()).contains(fresh.getType())) {
                dependencies.get(current_subj.getType()).add(fresh.getType());
               /* System.out.println("Dependence for subject type" + fresh.getType()
                        + " in subject" + current_subj.getType() + "is added");*/
            }
        }
        System.out.println("Success.");
//        } else System.err.println("?????? ????????????? ????? ??????? ???????.");
    }

    private static void printDep() {
        for (String object : dependencies.keySet()) {
            String message = "Родительский тип: %s | дочерние типы: %s";
            HashSet<String> curr = dependencies.get(object);
            System.out.println(String.format(message, object, curr));
        }
    }

    private static void clearDep(String target) {
        for (String object : dependencies.keySet()) {
            HashSet<String> curr = dependencies.get(object);
            if (curr.contains(target))
                dependencies.get(object).remove(target);
        }
    }

    private static void scenario() {
        AccessMap map = AccessMap.getInstance();
        System.out.println("**Preparing start conditions**");
        Scanner scan = new Scanner(System.in);
        map.createSubj(current_subj, "s1", "123", "admin");
        map.createSubj(current_subj, "s2", "234", "user");
        current_subj = map.findSubj("s1");
        map.createObj(current_subj, "o1", "secret");
        map.createObj(current_subj, "o3", "secret");
        current_subj = map.findSubj("s2");
        map.createObj(current_subj, "o2", "nonsecret");
        map.setAccess(current_subj, "s1", "o2", AccessType.READ);
        map.setAccess(current_subj, "s1", "o2", AccessType.WRITE);
        map.setAccess(current_subj, "s1", "o2", AccessType.EXECUTE);
        System.out.println("\n**Now create a trojan nonsecret object**\n");
//        System.out.println("\n**Create admin user s1**\n");
//        createSubj(scan);
//        System.out.println("\n**Create simple user s2**\n");
//        createSubj(scan);
//        current_subj = map.findSubj("s1");
//        System.out.println("\n**Create secret object o1**\n");
//        createObj(scan);
//        System.out.println("\n**Create secret object o3**\n");
//        createObj(scan);
//        current_subj = map.findSubj("s2");
//        System.out.println("\n**Create nonsecret object o2**\n");
//        createObj(scan);
//        System.out.println("\n**Now give READ, WRITE and EXECUTE rights to s1 on o2**\n");
//        setAccess(scan);
//        setAccess(scan);
//        setAccess(scan);
//        System.out.println("_____________\nCurrent access matrix for s1\n_____________");
//        map.open(map.findSubj("s1"));
//        System.out.println("---------");
//        System.out.println("_____________\nCurrent access matrix for s2\n_____________");
//        map.open(map.findSubj("s2"));
//        System.out.println("---------");
        createObj(scan);
        HashSet<String> temp = new HashSet<>();
        temp.add("nonsecret");
        dependencies.put("nonsecret", temp);
//       temp.clear();
        System.out.println("\n**Now give READ, WRITE, EXECUTE rights to s1 on trojan**\n");
        setFullAccess(scan);
        if (secure)
            return;
//        setAccess(scan);
//        setAccess(scan);
//        setAccess(scan);
        current_subj = map.findSubj("s1");
        System.out.println("\n**Now admin user is going to run trojan!**\n");
        createSubj(scan);
        System.out.println("\n**Trojan admin subject was created, giving it access to o2**\n");
        current_subj = map.findSubj("s2");
        setFullAccess(scan);
        HashSet<String> temp1 = new HashSet<>();
        temp1.add("admin");
        dependencies.put("secret", temp1);
        dependencies.get("nonsecret").add("admin");
        dependencies.get("user").add("admin");
        dependencies.get("admin").add("admin");
        current_subj = map.findSubj("s1");
        System.out.println("\n**Giving to trojan rights on o1**\n");
        setFullAccess(scan);
        System.out.println("\n**Giving to trojan rights on o3**\n");
        setFullAccess(scan);
        printDep();
        System.out.println("\n**Now trojan gonna create secret object o` in nonsecret o2**\n");
        current_subj = map.findSubj("trojan");
        System.out.println("\n**Trojan subject creates secret o`**\n");
        createObj(scan);
        setFullAccess(scan);
        System.out.println("\n**Trojan gives READ to s2 on o`**\n");
        setAccess(scan);
        dependencies.get("secret").add("secret");
        dependencies.get("nonsecret").add("secret");
        printDep();
        System.out.println("\n**Trojan copied secret file o3 to o'. Now s2 can read o3.**\n + " +
                "**Attack succeeded!**");
    }


}
