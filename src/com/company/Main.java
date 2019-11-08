package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class Main {

    public static final String fileName = "test.json";
    private static List<Pojo> company;
    private static List<Pojo> list;


    public static void main(String[] args) throws IOException {

        if (!Files.exists(Paths.get(fileName))) {
            System.err.println("Файл отсутствует.Проверьте правильно ли указан путь к файлу.");
            System.exit(0);
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            System.out.println();
            company = new Gson().fromJson(bufferedReader, new TypeToken<List<Pojo>>() {
            }.getType());

            System.out.println("Все компании:");
            for (Pojo company : company) {
                {

                    TemporalAccessor temp = DateTimeFormatter
                            .ofPattern("yyyy-MM-d")
                            .parse(company.getEgrul_date());


                    System.out.println(company.getName_short() + " - Дата основания: " + DateTimeFormatter.ofPattern("dd/MM/yy").format(temp));
                }

            }

            int count = 0;
            System.out.println("\n" + "---------------------------------------------------------------" + "\n" + "Выводим на экран все ценные бумаги, просроченные на текущий момент:");
            for (Pojo comp : company) {
                for (Securities securitys : comp.getSecurities()) {
                    if (LocalDate.parse(securitys.getDate_to(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now(ZoneId.of("Europe/Moscow")))) {

                        System.out.println("Код - " + securitys.getCode() + "; Дата истечения - " + securitys.getDate_to() + "; Полное название организации-владельца - " + comp.getName_full());
                        count++;

                    }
                }
            }

            System.out.println("\n" + "Суммарное число бумаг просроченных на текущий день: " + count);
        } catch (IOException e) {
            e.printStackTrace();


        }


        System.out.println("\n" + "---------------------------------------------------------------" + "\n" + "Введите дату в формате «ДД.ММ.ГГГГ», или «ДД.ММ,ГГ» или «ДД/ММ/ГГГГ» или «ДД/ММ/ГГ» (без кавычек), что бы получить название и дату создания всех организаций, основаннных после введеной даты: ");
        System.out.println("Введите код валюты(EUR, USD, RUB), что бы получить id и коды ценных бумаг, использующих заданную валюту: ");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String string;
        try {
            while (!(string = bufferedReader.readLine()).equalsIgnoreCase("exit")) {
                {

                    if (string.length() == 3) {

                        String finalString = string;
                        company.stream()
                                .flatMap(a -> Arrays.stream(a.getSecurities()))
                                .filter(s -> s.getCurrency().getCode().equals(finalString))
                                .forEach(System.out::println);


                        System.out.println("\n" + "---------------------------------------------------------------" + "\n" + "Введите дату в формате «ДД.ММ.ГГГГ», или «ДД.ММ,ГГ» или «ДД/ММ/ГГГГ» или «ДД/ММ/ГГ» (без кавычек), что бы получить название и дату создания всех организаций, основаннных после введеной даты: ");
                        System.out.println("Введите код валюты(EUR, USD, RUB), что бы получить id и коды ценных бумаг, использующих заданную валюту: ");
                        System.out.println("\n" + "Для выхода введите exit.");


                    } else if (correctDate(string)) {
                        try {


                            String date = string.replace(".", "").replace("/", "");
        /*                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(date.length() == 6 ? "ddMMyy" : "ddMMyyyy");
                            LocalDate inputDate = LocalDate.parse(date, formatter);
*/


                            DateTimeFormatter dtf2 =
                                    new DateTimeFormatterBuilder().appendPattern("ddMM")
                                            .appendValueReduced(
                                                    ChronoField.YEAR, 2, 4, Year.now().getValue() - 80
                                            ).toFormatter();

                            LocalDate inputDate1 = LocalDate.parse(date, dtf2);
                            System.out.println("Вы ввели дату "+inputDate1);




                            list = company.stream()
                                    .filter(x -> LocalDate.parse(x.getEgrul_date(), DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)).isAfter(inputDate1))
                                    .collect(Collectors.toList());

                            if (list.isEmpty()) {
                                System.err.println("Организации созданы ранее, чем введенная дата. Введите другую дату. Для выхода введите exit.");
                            } else {

                                list.forEach(System.out::println);
                            }

                        } catch (Exception e) {
                            System.err.println("Вы ввели дату в неверном формате. Для выхода введите exit.");
                        }


                    } else if (string.isEmpty()) {
                        System.err.println("Вы ничего не ввели. Для выхода введите exit.");
                    } else {
                        System.err.println("Проверьте правильность введенных данных.");
                        System.err.println("Для выхода введите exit.");

                    }
                }


            }


        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private static boolean correctDate(String string) {
        if (string.length() != 8 && string.length() != 10) {
            return false;
        }

        string = string.replace(".", "").replace("/", "");
        return (string.length() == 6 || string.length() == 8);
    }
}













