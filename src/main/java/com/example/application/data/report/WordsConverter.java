package com.example.application.data.report;

import com.ibm.icu.text.RuleBasedNumberFormat;

import java.util.Locale;

public class WordsConverter {

    public static String getDayOfMonth(int day) {
        return (day < 10 ? "0" : "") + day;
    }

    public static String convertGenetiveNumericMonthToString(int numeric) {
        String[] months = {
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        return months[numeric - 1];
    }

    public static String convertNumericMonthToString(int numeric) {
        String[] months = {
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        return months[numeric - 1];
    }

    public static String convertNameToGenitive(String lastName, String firstName, String secondName) {
        if (lastName.charAt(lastName.length() - 1) == 'н' || lastName.charAt(lastName.length() - 1) == 'в')
            lastName += "а";
        else if (lastName.charAt(lastName.length() - 1) == 'а')
            lastName = lastName.substring(0, lastName.length() - 1) + "ой";

        String firstNameSubstr = firstName.substring(0, firstName.length() - 1);
        if (firstName.charAt(firstName.length() - 1) == 'а')
            firstName = firstNameSubstr + "ы";
        else if (firstName.charAt(firstName.length() - 1) == 'й')
            firstName = firstNameSubstr + "я";
        else if (firstName.charAt(firstName.length() - 1) == 'я' || firstName.charAt(firstName.length() - 1) == 'ь')
            firstName = firstNameSubstr + "и";
        else if (firstName.charAt(firstName.length() - 1) == 'л' || firstName.charAt(firstName.length() - 1) == 'н')
            firstName += "а";

        if (secondName.charAt(secondName.length() - 1) == 'ч')
            secondName += "а";
        else if (secondName.charAt(secondName.length() - 1) == 'а')
            secondName = secondName.substring(0, secondName.length() - 1) + "ы";

        return lastName + " " + firstName + " " + secondName;
    }

    public static String convertPositionToGenitive(String position) {
        String[] positionSplitted = position.split(" ");
        StringBuilder positionGenitive = new StringBuilder();
        for (String p : positionSplitted) {
            if (p.charAt(p.length() - 1) == 'й')
                positionGenitive.append(p, 0, p.length() - 2).append("ого").append(" ");
            else if (p.charAt(p.length() - 1) == 'я')
                positionGenitive.append(p, 0, p.length() - 2).append("ой").append(" ");
            else if (p.charAt(p.length() - 1) == 'р')
                positionGenitive.append(p).append("а").append(" ");
        }
        return positionGenitive.toString();
    }

    public static String convertCoinsToGenitive(String coins) {
        if (coins.charAt(0) == '0')
            coins = coins.substring(1);

        if (Integer.parseInt(coins) < 20 && Integer.parseInt(coins) > 10)
            return coins + " копеек";

        String result;
        switch (coins.charAt(coins.length() - 1)) {
            case '1':
                result = coins + " копейка";
                break;
            case '2':
            case '3':
            case '4':
                result = coins + " копейки";
                break;
            default:
                result = coins + " копеек";
                break;
        }
        return result;
    }

    public static String convertRublesToGenitive(String rubles) {
        String result;
        switch (rubles.charAt(rubles.length() - 1)) {
            case '1':
                result = " рубль";
                break;
            case '2':
            case '3':
            case '4':
                result = " рубля";
                break;
            default:
                result = " рублей";
                break;
        }
        return result;
    }

    public static String convertNumericRublesToString(int numeric) {
        RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"),
                RuleBasedNumberFormat.SPELLOUT);
        return nf.format(numeric);
    }
}
