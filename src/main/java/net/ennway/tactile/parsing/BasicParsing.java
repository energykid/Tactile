package net.ennway.tactile.parsing;

import java.util.Objects;

public class BasicParsing {

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static Object typeOfString(String str)
    {
        if (Objects.equals(str, "false"))
            return false;
        if (Objects.equals(str, "true"))
            return true;
        if (isNumeric(str))
            return Double.parseDouble(str);
        return str;
    }
    public static String blockStateString(String str)
    {
        if (str.charAt(str.length() - 1) == ']')
        {
            int ch = str.length() - 1;
            for (int i = 0; i < 50; i++)
            {
                if (str.charAt(ch) == '[') break;
                ch--;
            }
            return str.substring(ch + 1, str.length() - 1);
        }
        return "";
    }
    // Use this to separate the string for an entity data entry in cursor.json.
    // Will separate "data=3" into ["data", "3", "="].
    public static String[] entityStateStrings(String str)
    {
        if (str.contains("!="))
        {
            return new String[] {str.substring(0, str.indexOf("!=")), str.substring(str.indexOf("!=") + 2), "!="};
        }
        if (str.contains("="))
        {
            return new String[] {str.substring(0, str.indexOf("=")), str.substring(str.indexOf("=") + 1), "="};
        }
        if (str.contains(">"))
        {
            return new String[] {str.substring(0, str.indexOf(">")), str.substring(str.indexOf(">") + 1), ">"};
        }
        if (str.contains("<"))
        {
            return new String[] {str.substring(0, str.indexOf("<")), str.substring(str.indexOf("<") + 1), "<"};
        }
        return new String[] {"", "", ""};
    }
    public static int compareNumericValues(Object first, Object second, Class cl)
    {
        if (cl == Integer.class)
        {
            return ((Integer)first).compareTo((Integer) second);
        }
        if (cl == Float.class)
        {
            return ((Float)first).compareTo((Float) second);
        }
        if (cl == Double.class)
        {
            return ((Double)first).compareTo((Double) second);
        }
        if (cl == Long.class)
        {
            return ((Long)first).compareTo((Long) second);
        }

        return 0;
    }

}
