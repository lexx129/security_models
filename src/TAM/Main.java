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
                default:
                    System.err.println("Unknown command.");
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
                if (!dependencies.get(father.getType()).contains(son.getType()))
                    dependencies.get(subj_name).add(obj_name);
                System.out.println("**Access granted.**");
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
            if ((dependencies.get(current_subj.getType()) == null) ) { //|| !dependencies.get(current_subj.getType()).contains(fresh.getType())) {
                HashSet<String> neu = new HashSet<>();
                neu.add(fresh.getType());
                dependencies.put(current_subj.getType(), neu);
            }
            else if (!dependencies.get(current_subj.getType()).contains(fresh.getType())){
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
            if ((dependencies.get(current_subj.getType()) == null) ) { //|| !dependencies.get(current_subj.getType()).contains(fresh.getType())) {
                HashSet<String> neu = new HashSet<>();
                neu.add(fresh.getType());
                dependencies.put(current_subj.getType(), neu);
            } else
            if (!dependencies.get(current_subj.getType()).contains(fresh.getType())) {
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


}
