package com.company;


import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.util.*;




public class SHIFT {

    private static void renameFile(String to) {
        File result = new File("temp_res.txt");
        File target = new File(to);
        result.renameTo(target);
    }//переименование файла

    public static boolean isFileEmpty(File file) {
        return file.length() == 0;
    } //проверка на пустой файл

    public static ArrayList<String> argumentChecking(String[] arr_args) {

        ArrayList<String> res = new ArrayList<>();
        String sorting = "";
        String type = "";
        int temp_i = 0;
        //анализируем параметры запуска
        if (arr_args.length > 2) {
            for (int i = 0; i <= 1; ++i) { //ищем в на первых двух позициях нужные аргументы
                if ((arr_args[i].equals("-s") || arr_args[i].equals("-i")) && type.length() == 0) {
                    type = arr_args[i].equals("-s") ? "-s" : "-i";
                    res.add(type);
                } else if (type.length() == 0 && i == 1) {
                    System.out.println("Неверно указан или отсутствует тип данных.");
                    res.clear();
                    return res;
                }
                if ((arr_args[i].equals("-a") || arr_args[i].equals("-d")) && sorting.length() == 0) {
                    sorting = arr_args[i].equals("-a") ? "-a" : "-d";
                    res.add(sorting);
                    temp_i = 2;
                } else if (sorting.length() == 0 && i == 1) {
                    sorting = "-a";
                    res.add(sorting);
                    temp_i = 1;
                }
            }

            for (int i = temp_i; i < arr_args.length; i++) {
                File files_in = new File(arr_args[i]);
                if (!files_in.isFile() && i > temp_i) {
                    System.out.println("Один или несколько входных файлов отсутствуют");
                    res.clear();
                    return res;
                }
                if (!arr_args[i].contains(".txt") || arr_args.length - temp_i < 2) { //смотрим чтобы после аргументов были только названия файлов и чтобы их было больше 1
                    System.out.println("Неверные набор или порядок параметров");
                    res.clear();
                    return res;
                }
                if (i == temp_i) {  //проверка наличия файла для записи результата (по ТЗ нужно создать НОВЫЙ)
                    File fOut = new File(arr_args[i]);
                    if (fOut.isFile()) {
                        System.out.println("Файл с именем для исходного файла существует. Удалите или переименуйте сторонний файл");
                        res.clear();
                        return res;
                    }
                }
                res.add(arr_args[i]);
            }
        } else {
            System.out.println("Неверное количество обязательных параметров");
            return res;
        }
        return res;
    } // проверка аргументов

    private static int compareDueToType(String line1, String line2, boolean Type) { // метод сравнения строк или чисел
        if (Type) {
            return line1.compareTo(line2);
        } else {
            return Integer.parseInt(line1) - Integer.parseInt(line2);
        }
    }

    private static boolean hasErrorsInSorting (String line1, String line2,boolean isAsc, boolean isStr) { //проверка правильной последовательности сортировки
        String regex = "\\d+";                                                                   //для опредения только цифр в строке
        if (line1 != null) {
            if (!line1.contains(" ") && !line1.contains("\t") && !line1.isBlank()) {             // анализируем строку на пробелы, табуляцию и пустоту
                if (isAsc && isStr) {                                                            // в зависимости от входящих параметров сравниваем предыдущую строку с только что прочитанной
                    if (line2.compareTo(line1) <= 0) {
                        return false;
                    } else {
                        return true;
                    }
                } else if (!isAsc && isStr) {
                    if (line2.compareTo(line1) >= 0) {
                        return false;

                    } else {
                        return true;
                    }
                } else {
                    if (line1.matches(regex)) {                                                  //проверка на то, что в строке только число
                        int i = Integer.parseInt(line2) - Integer.parseInt(line1);
                        if (isAsc) {
                            if (i <= 0) {                                                        //Если сортировка соответствует параметру при запуске
                                return false;                                                    //выдается false и цикл поиска подходящей строки не запускается
                            } else {
                                return true;
                            }
                        } else {
                            if (i >= 0) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    } else return false;
                }
            }else return false;
        }else return false;
    }

    private static void sortType(List<String> arguments, boolean ascDesc, boolean strInt) {
        String regex = "\\d+";
        boolean exitFlag = false;
        boolean errorSort = false;
        for (int i = 3; i < arguments.size(); i++) {
            File file = new File(arguments.get(i));
            if (isFileEmpty(file)) {
                System.out.println("Файл" + arguments.get(i) + "пуст");
                continue;
            }
            if (i == 3) {
                try (
                        FileReader fileReader = new FileReader(arguments.get(i));
                        FileWriter writerFile1 = (arguments.size() != 4) ? new FileWriter("temp_res.txt") : new FileWriter(arguments.get(2));
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                ) {
                    String line = bufferedReader.readLine();
                    String tempLine1;
                    while (line != null) {
                        while (line.contains(" ") || line.contains("\t") || line.isBlank() || (!strInt && !line.matches(regex))) {
                            tempLine1 = bufferedReader.readLine();
                            if (tempLine1 == null) {
                                line = null;
                                exitFlag = true;
                                break;
                            }
                            if (hasErrorsInSorting(tempLine1, line, ascDesc, strInt)) { //проверка порядка сортировки
                                line = tempLine1;
                                break;
                            }
                        }
                        if (exitFlag) break;
                        writerFile1.write(line + "\n");
                        tempLine1 = bufferedReader.readLine();
                        while (hasErrorsInSorting(tempLine1, line, ascDesc, strInt)) {//проверка порядка сортировки, неверная сортировка запускает цикл
                            errorSort = true;
                            tempLine1 = bufferedReader.readLine();
                            if (tempLine1 == null) {
                                line = null;
                                break;
                            }
                        }
                        line = tempLine1;
                    }
                    writerFile1.close();
                    fileReader.close();
                    bufferedReader.close();
                    if (errorSort) {
                        System.out.println("В файле " + arguments.get(i) + "неверная сортировка. Произвдена частичная сортировка");
                    }
                    if (arguments.size() == 4) {//для случая с одним входным файлом
                        break;
                    }
                    renameFile("result.txt");
                    errorSort = false;
                } catch (FileNotFoundException e) {
                    System.err.println("Файл не найден: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Возникла ошибка при обработке файла: " + e.getMessage());
                }
            } else {
                try (
                        FileReader fileReader1 = new FileReader(arguments.get(i));
                        FileReader fileReader2 = new FileReader("result.txt");
                        FileWriter writer = i != arguments.size() - 1 ? new FileWriter("temp_res.txt") : new FileWriter(arguments.get(2));
                        BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                        BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
                ) {
                    String line1 = bufferedReader2.readLine();
                    String line2 = bufferedReader2.readLine();
                    String temp;
                    while (line1 != null || line2 != null) {
                        if (line1 != null) {
                            while (line1.contains(" ") || line1.contains("\t") || line1.isBlank() || (!strInt && !line1.matches(regex))) { //проверка на пробелы в строк и что в строке только числа при параметре -i
                                temp = bufferedReader1.readLine();
                                if (hasErrorsInSorting(temp, line1, ascDesc, strInt)) {
                                    line1 = temp;
                                    break;
                                }
                            }
                        }

                        if (line2 != null) {
                            while (line2.contains(" ") || line2.contains("\t") || line2.isBlank() || (!strInt && !line2.matches(regex))) { //проверка на пробелы в строке
                                line2 = bufferedReader2.readLine();

                                if (line2 == null) break;
                            }
                        }
                        if (ascDesc) {
                            if (line1 == null || (line2 != null && compareDueToType(line1, line2, strInt) > 0)) {
                                writer.write(line2 + "\n");
                                line2 = bufferedReader2.readLine();
                            } else {
                                writer.write(line1 + "\n");
                                temp = bufferedReader1.readLine();
                                while (hasErrorsInSorting(temp, line1, ascDesc, strInt)) {
                                    errorSort = true;
                                    temp = bufferedReader1.readLine();
                                    if (temp == null) {
                                        line1 = null;
                                        break;
                                    }
                                }
                                line1 = temp;
                            }
                        } else {
                            if (line1 == null || (line2 != null && compareDueToType(line1, line2, strInt) < 0)) {
                                writer.write(line2 + "\n");
                                line2 = bufferedReader2.readLine();
                            } else {
                                writer.write(line1 + "\n");
                                //line1 = bufferedReader1.readLine();
                                temp = bufferedReader1.readLine();
                                while (hasErrorsInSorting(temp, line1, ascDesc, strInt)) {
                                    errorSort = true;
                                    temp = bufferedReader1.readLine();
                                    if (temp == null) {
                                        line1 = null;
                                        break;
                                    }
                                }
                                line1 = temp;
                            }
                        }
                    }
                    if (errorSort) {
                        System.out.println("В файле " + arguments.get(i) + " неверная сортировка. Произвдена частичная сортировка");
                    }
                    writer.close();
                    fileReader1.close();
                    fileReader2.close();
                    bufferedReader1.close();
                    bufferedReader2.close();


                    File fileDel = new File("result.txt");
                    fileDel.delete(); //удаление временных файлов
                    if (i != arguments.size() - 1) {
                        renameFile("result.txt");
                    }
                    errorSort = false;
                } catch (FileNotFoundException e) {
                    System.err.println("Файл не найден: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Возникла ошибка при обработке файла: " + e.getMessage());
                    if ( i == arguments.size() - 1) { //при ошибке чтения последнего файла
                        renameFile(arguments.get(2));
                        System.out.println("Готово!");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<String> argsValid = new ArrayList<>();
        boolean isAscending;
        boolean isString = true;

        while (argsValid.size() < 1) {
            argsValid = argumentChecking(args);
            if (argsValid.size() > 1) break;
            System.out.println("Введите новые параметры через пробел");
            String[] parameters = sc.nextLine().split(" ");
            if (parameters[0].equals("exit")) System.exit(0);                //для выхода можно написать exit
            args = parameters;

        }
        isAscending = argsValid.contains("-a");
        if (argsValid.contains("-s")) {
            sortType(argsValid, isAscending, isString);
            System.out.println("Готово!");
        } else {
            sortType(argsValid, isAscending, !isString);
            System.out.println("Готово!");
        }


    }
}