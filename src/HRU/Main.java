package HRU;

import java.util.Scanner;

/**
 * Created by Lexx on 07.10.2015.
 */
public class Main {

    public static HRUsubj current_subj;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        AccessMap.getInstance().createAdm("admin", "admin");
        while (true) {
            System.out.println("Введите команду: ");
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
                case "login": {
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
                case "scenario": {
                    System.out.println("Номер сценария атаки: ");
                    int code = scan.nextInt();
                    switch (code) {
                        case 1: {
                            scenario1();
                            break;
                        }
                    }
                    break;
                }
                default:
                    System.err.println("Команда не распознана.");
            }
        }
    }

    private static void scenario1() {
//        AccessMap.getInstance().createAdm("admin", "admin");
        current_subj = AccessMap.getInstance().admin;

        AccessMap.getInstance().createSubj(current_subj, "secret", "secret");
        HRUsubj secretuser = AccessMap.getInstance().findSubj("secret");
        System.out.println("Создан доверенный пользователь.");
        AccessMap.getInstance().createSubj(current_subj, "hijacker", "123");
        HRUsubj hijacker = AccessMap.getInstance().findSubj("hijacker");
        System.out.println("Создан пользователь - злоумышленник.");
        current_subj = secretuser;
        AccessMap.getInstance().createObj(secretuser, "o1");
        System.out.println("Создан каталог о1 с секретной информацией.");
        AccessMap.getInstance().createObj(secretuser, "o3");
        System.out.println("Создан каталог о3 с секретной информацией.");
        current_subj = hijacker;
        AccessMap.getInstance().createObj(hijacker, "o2");
        System.out.println("Создан каталог о2 с обычным уровнем доступа.");
//        current_subj = secretuser;
        AccessMap.getInstance().setAccess(secretuser, "secret", "o1", AccessType.READ);
        AccessMap.getInstance().setAccess(secretuser, "secret", "o1", AccessType.WRITE);
        AccessMap.getInstance().setAccess(secretuser, "secret", "o1", AccessType.EXECUTE);
        AccessMap.getInstance().setAccess(secretuser, "secret", "o3", AccessType.READ);
        AccessMap.getInstance().setAccess(secretuser, "secret", "o3", AccessType.WRITE);
        AccessMap.getInstance().setAccess(secretuser, "secret", "o3", AccessType.EXECUTE);
//        current_subj = hijacker;
        System.out.println("Текущие права доступа доверенного пользователя: ");
        AccessMap.getInstance().open(secretuser);
        System.out.println("--------------");
        AccessMap.getInstance().setAccess(hijacker, "hijacker", "o2", AccessType.READ);
        AccessMap.getInstance().setAccess(hijacker, "hijacker", "o2", AccessType.WRITE);
        AccessMap.getInstance().setAccess(hijacker, "hijacker", "o2", AccessType.EXECUTE);
        System.out.println("Текущие права доступа пользователя-злоумышленника: ");
        AccessMap.getInstance().open(hijacker);
        AccessMap.getInstance().setAccess(hijacker, "secret", "o2", AccessType.READ);
        AccessMap.getInstance().setAccess(hijacker, "secret", "o2", AccessType.WRITE);
        AccessMap.getInstance().createObj(current_subj, "trojan");
        System.out.println("Создан вредоносный файл");
        AccessMap.getInstance().setAccess(hijacker, "secret", "trojan", AccessType.READ);
        AccessMap.getInstance().setAccess(hijacker, "secret", "trojan", AccessType.WRITE);
        AccessMap.getInstance().setAccess(current_subj, "secret", "trojan", AccessType.EXECUTE);
        System.out.println("Злоумышленник дает право на запуск вредоносной программы " +
                "доверенному пользователю.");
        current_subj = secretuser;
        System.out.println("Доверенный пользователь запускает троян со своими правами доступа!");
        if (!AccessMap.getInstance().createSubj(current_subj, "tr", "tr"))
            System.exit(-1);
        System.out.println("Создан вредоносный субъект");
        HRUsubj tr = AccessMap.getInstance().findSubj("tr");
        AccessMap.getInstance().setAccess(hijacker, "tr", "o2", AccessType.WRITE);
        AccessMap.getInstance().setAccess(secretuser, "tr", "o1", AccessType.READ);
        AccessMap.getInstance().setAccess(secretuser, "tr", "o1", AccessType.WRITE);
        AccessMap.getInstance().setAccess(secretuser, "tr", "o1", AccessType.EXECUTE);
        AccessMap.getInstance().setAccess(secretuser, "tr", "o3", AccessType.READ);
        AccessMap.getInstance().setAccess(secretuser, "tr", "o3", AccessType.WRITE);
        AccessMap.getInstance().setAccess(secretuser, "tr", "o3", AccessType.EXECUTE);
        AccessMap.getInstance().open(tr);
        AccessMap.getInstance().destroySubj(AccessMap.getInstance().admin, "tr");
        System.out.println("Вредоносный субъект с правами доступа доверенного субъекта" +
                "скопировал секретные данные в открытую папку о2, а затем удалился.");
//        AccessMap.getInstance().setAccess(current_subj, )

    }

    private static void removeAccess(Scanner scan) {
        System.out.println("У кого забираем доступ? ");
        String subj = scan.next();
        System.out.println("К чему забираем доступ? ");
        String obj = scan.next();
        if (current_subj != null) {
            current_subj.removeAccess(subj, obj);
            System.out.println("Выполнено");
        } else
            System.err.println("Сначала необхоимо авторизоваться.");
    }

    private static void setAccess(Scanner scan) {
        System.out.println("Кому даем доступ? ");
        String subj_name = scan.next();
        System.out.println("На что даем доступ? ");
        String obj_name = scan.next();
        System.out.println("Какой тип доступа даем? ");
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
            case "exe":
                accessType = AccessType.EXECUTE;
                break;
            default:
                System.err.println("Введенный тип доступа не распознан.");
                return;
        }
        if (current_subj != null) {
            if (current_subj.addAccess(subj_name, obj_name, accessType))
                System.out.println("Выполнено.");
        } else
            System.err.println("Сначала необходимо авторизоваться.");
    }

    private static void login(Scanner scan) {
        System.out.println("Введите имя пользователя: ");
        String username = scan.next();
        System.out.println("Введите пароль: ");
        String password = scan.next();
        HRUsubj hrUsubj = AccessMap.getInstance().findSubj(username);
        if (hrUsubj != null && hrUsubj.getName().equals(username) && hrUsubj.getPassword().
                equals(password)) {
            System.out.println("Успешная авторизация под именем " + username);
            current_subj = hrUsubj;
        } else System.err.println("Учетные данные не распознаны.");
    }

    private static void destroySubj(Scanner scan) {
        if (current_subj == AccessMap.getInstance().admin) {
            System.out.println("Имя удаляемого субъекта: ");
            String subj_name = scan.next();
            if (current_subj.destroySubj(subj_name))
                System.out.println("Субъект удален.");
        } else System.err.println("Только администратор может удалить субъект.");
    }

    private static void destroyObj(Scanner scan) {
        System.out.println("Имя удаляемого объекта: ");
        String obj_name = scan.next();
        if (current_subj != null) {
            if (current_subj.destroyObj(obj_name))
                System.out.println("Объект удален.");
        } else System.err.println("Сначала необходимо авторизоваться.");
    }

    private static void createObj(Scanner scan) {
        System.out.println("Имя для нового объекта: ");
        String obj_name = scan.next();
        if (current_subj != null) {
            try {
                current_subj.createObj(obj_name);
                System.out.println("Объект создан.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else System.err.println("Сначала необходимо авторизоваться.");
    }

    private static void createSubj(Scanner scan) {
        if (current_subj == AccessMap.getInstance().admin) {
            System.out.println("Имя нового субъекта: ");
            String username = scan.next();
            System.out.println("Пароль для нового субъекта: ");
            String password = scan.next();
            AccessMap.getInstance().createSubj(current_subj, username, password);
            System.out.println("Субъект создан.");
        } else System.err.println("Только администратор может создать субъект.");
    }

}
