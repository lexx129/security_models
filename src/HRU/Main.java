package HRU;

import java.util.Scanner;

/**
 * Created by Lexx on 07.10.2015.
 */
public class Main {

    public static HRUsubj current_subj;

    public static void main(String[] args) {
        System.out.println("?????????????? ??????????????: ");
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
        System.out.println("� ���� �������� ������? ");
        String subj = scan.next();
        System.out.println("� ���� �������� ������? ");
        String obj = scan.next();
        if (current_subj != null)
            current_subj.removeAccess(subj, obj);
        else
            System.err.println("������� ��������� ��������������.");
    }

    private static void setAccess(Scanner scan) {
        System.out.println("���� ���� ������? ");
        String subj_name = scan.next();
        System.out.println("�� ��� ���� ������? ");
        String obj_name = scan.next();
        System.out.println("����� ��� ������� ����? ");
        String access_type = scan.next();
        AccessType accessType = null;
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
            default:
                System.err.println("��������� ��� ������� �� ���������.");
                return;
        }
        if (current_subj != null)
            current_subj.addAccess(subj_name, obj_name, accessType);
        else
            System.err.println("������� ���������� ��������������.");
    }

    private static void login(Scanner scan) {
        System.out.println("������� ��� ������������: ");
        String username = scan.next();
        System.out.println("������� ������: ");
        String password = scan.next();
        HRUsubj hrUsubj = AccessMap.getInstance().findSubj(username);
        if (hrUsubj != null && hrUsubj.getName().equals(username) && hrUsubj.getPassword().
                equals(password)) {
            System.out.println("�������� ����������� ��� ������ " + username);
            current_subj = hrUsubj;
        }
        else System.err.println("������� ������ �� ����������.");
    }

    private static void destroySubj(Scanner scan) {
        System.out.println("��� ���������� ��������: ");
        String subj_name = scan.next();
        if (current_subj != null)
            current_subj.destroySubj(subj_name);
        else System.err.println("������� ���������� ��������������.");
    }

    private static void destroyObj(Scanner scan) {
        System.out.println("��� ���������� �������: ");
        String obj_name = scan.next();
        if (current_subj != null)
            current_subj.destroyObj(obj_name);
        else System.err.println("������� ���������� ��������������.");
    }

    private static void createObj(Scanner scan) {
        System.out.println("��� ��� ������ �������: ");
        String obj_name = scan.next();
        if (current_subj != null){
            try {
                current_subj.createObj(obj_name);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        else System.err.println("������� ���������� ��������������.");
    }

    private static void createSubj(Scanner scan) {
        System.out.println("��� ������ ��������: ");
        String username = scan.next();
        System.out.println("������ ��� ������ ��������: ");
        String password = scan.next();
        AccessMap.getInstance().createSubj(current_subj, username, password);
    }

}
