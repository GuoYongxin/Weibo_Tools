package com.roger.weibotool.common.parser;

public class AutoFillHelper
{

    public static boolean isPrimitiveType(Class<?> clazz)
    {
        return clazz == Integer.class || clazz == Integer.TYPE
                || clazz == Long.class || clazz == Long.TYPE
                || clazz == Byte.class || clazz == Byte.TYPE
                || clazz == Short.class || clazz == Short.TYPE
                || clazz == Float.class || clazz == Float.TYPE
                || clazz == Double.class || clazz == Double.TYPE
                || clazz == Boolean.class || clazz == Boolean.TYPE
                || clazz == String.class;
    }

    public static Object getValue(Class<?> clazz, String value)
    {
        if (clazz == Integer.class || clazz == Integer.TYPE)
        {
            int i = Integer.parseInt(value);
            return new Integer(i);
        }
        else if (clazz == Long.class || clazz == Long.TYPE)
        {
            long l = Long.parseLong(value);
            return new Long(l);
        }
        else if (clazz == Byte.class || clazz == Byte.TYPE)
        {
            byte b = Byte.parseByte(value);
            return new Byte(b);
        }
        else if (clazz == Short.class || clazz == Short.TYPE)
        {
            short s = Short.parseShort(value);
            return new Short(s);
        }
        else if (clazz == Float.class || clazz == Float.TYPE)
        {
            float f = Float.parseFloat(value);
            return new Float(f);
        }
        else if (clazz == Double.class || clazz == Double.TYPE)
        {
            double d = Double.parseDouble(value);
            return new Double(d);
        }
        else if (clazz == Boolean.class || clazz == Boolean.TYPE)
        {
            boolean b = Boolean.parseBoolean(value);
            return new Boolean(b);
        }
        else if (clazz == String.class)
        {
            return value;
        }
        else
        {
            throw new IllegalArgumentException(clazz.getName()
                    + " is not a primitive type.");
        }
    }
}
