package HRU;

import java.util.Scanner;

/**
 * Created by Lexx on 07.10.2015.
 */
public class Main {

    public static HRUsubj current_subj;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while (true) {
            String command = scan.next();
            switch (command) {
                case "create_subj": {
                    createSubj(scan);
                    break;
                }
                case "create_obj": {
                    createObj(scan);
                    break;
                }
                case "destroy_obj": {
                    destroyObj(scan);
                    break;
                }
                case "destroy_subj": {
                    destroySubj(scan);
                    break;
                }
                case "login:": {
                    login(scan);
                    break;
                }
                case "set_access": {
                    setAccess(scan);
                    break;
                }
                case "remove_access": {
                    removeAccess(scan);
                    break;
                }
                case "open": {
                    if (current_subj != null)
                        current_subj.open();
                    break;
                }
            }
        }
    }

    private static void removeAccess(Scanner scan) {
        String subj = scan.next();
        String obj = scan.next();
        if (current_subj != null)
            current_subj.removeAccess(subj, obj);
        else
            System.err.println("Сначала выполните авторизацию");
    }

    private static void setAccess(Scanner scan) {
        String subj_name = scan.next();
        String obj_name = scan.next();
        String access_type = scan.next();
        AccessType accessType = null;
        if (access_type.equals("write"))
            accessType = AccessType.WRITE;
        else if (access_type.equals("read"))
            accessType = AccessType.READ;
        else if (access_type.equals("own"))
            accessType = AccessType.OWN;
        else {
            System.out.println("Тип доступа не распознан");
            return;
        }
        if (current_subj != null)
            current_subj.addAccess(subj_name, obj_name, accessType);
        else
            System.err.println("Сначала необходимо авторизоваться");
    }

    private static void login(Scanner scan) {
        String username = scan.next();
        String password = scan.next();
        HRUsubj hrUsubj = AccessMap.getInstance().findSubj(username);
        if (hrUsubj != null && hrUsubj.getName().equals(username) && hrUsubj.getPassword().
                equals(password)) {
            System.out.println("Вы успешно вошли как " + username);
            current_subj = hrUsubj;
        }
        else System.err.println("Авторизация не удалась");
    }

    private static void destroySubj(Scanner scan) {
        String subj_name = scan.next();
        if (current_subj != null)
            current_subj.destroySubj(subj_name);
        else System.err.println("Сначала авторизуйтесь");
    }

    private static void destroyObj(Scanner scan) {
        String obj_name = scan.next();
        if (current_subj != null)
            current_subj.destroyObj(obj_name);
        else System.err.println("Сначала авторизуйтесь");
    }

    private static void createObj(Scanner scan) {
        String obj_name = scan.next();
        if (current_subj != null){
            try {
                current_subj.createObj(obj_name);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        else System.err.println("Сначала авторизуйтесь");
    }

    private static void createSubj(Scanner scan) {
        String username = scan.next();
        String password = scan.next();
        AccessMap.getInstance().createSubj(current_subj, username, password);
    }

}
