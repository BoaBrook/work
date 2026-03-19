package cn.stylefeng.guns.core.utils;

import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtils {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    private static final String ENCODING = "UTF-8";

    public static String camel2Underline(String line) {
        if (line != null && !"".equals(line)) {
            line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
            StringBuffer sb = new StringBuffer();
            Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
            Matcher matcher = pattern.matcher(line);

            while(matcher.find()) {
                String word = matcher.group();
                sb.append(word.toUpperCase());
                sb.append(matcher.end() == line.length() ? "" : "_");
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~") : null;
    }

    public static String substring(String str, int beginIndex, int endIndex) {
        if (endIndex > str.length()) {
            endIndex = str.length();
        }

        return str.substring(beginIndex, endIndex);
    }

    public static String join(String[] list, String sep) {
        if (isNullOrEmpty(list)) {
            return "";
        } else {
            List<String> tmp = new ArrayList();

            for(String s : list) {
                tmp.add(s);
            }

            return join((Collection)tmp, sep);
        }
    }

    public static String join(Collection<String> list, String sep) {
        if (isNullOrEmpty(list)) {
            return "";
        } else {
            StringBuffer sBuffer = new StringBuffer();
            Iterator<String> it = list.iterator();
            sBuffer.append((String)it.next());

            while(it.hasNext()) {
                sBuffer.append(sep);
                sBuffer.append((String)it.next());
            }

            return sBuffer.toString();
        }
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = (T[])Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static boolean isTrue(Object obj) {
        if (obj == null) {
            return false;
        } else {
            return obj instanceof Boolean ? (Boolean)obj : obj.toString().equals("true");
        }
    }

    public static boolean isAllChar(String str, char ch) {
        for(int i = 0; i < str.length(); ++i) {
            if (str.codePointAt(i) != ch) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEmptyEqual(Object obj1, Object obj2) {
        if (isNullOrEmpty(obj1) && isNullOrEmpty(obj2)) {
            return true;
        } else {
            return !isNullOrEmpty(obj1) && !isNullOrEmpty(obj2) ? obj1.equals(obj2) : false;
        }
    }

    public static boolean isEmpty(Object obj) {
        return isNullOrEmpty(obj);
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        } else if (obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map)obj).isEmpty();
        } else if (!(obj instanceof Object[])) {
            return false;
        } else {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            } else {
                boolean empty = true;

                for(int i = 0; i < object.length; ++i) {
                    if (!isNullOrEmpty(object[i])) {
                        empty = false;
                        break;
                    }
                }

                return empty;
            }
        }
    }

    public static String compress(String str) {
        if (null != str && str.length() > 0) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(out);
                gzip.write(str.getBytes());
                gzip.close();
                return out.toString("ISO-8859-1");
            } catch (IOException e) {
                log.error("Error compressing string", e);
                return str;
            }
        } else {
            return str;
        }
    }

    public static String unCompress(String str) {
        if (null != str && str.length() > 0) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
                GZIPInputStream gzip = new GZIPInputStream(in);
                byte[] buffer = new byte[256];
                int n = 0;

                while((n = gzip.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }

                return out.toString("UTF-8");
            } catch (Exception e) {
                log.error("Error uncompressing string", e);
                return str;
            }
        } else {
            return str;
        }
    }

    public static <T> boolean contain(T[] arr, T obj) {
        if (arr != null && obj != null) {
            for(T o : arr) {
                if (obj.equals(o)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static String safeStr(String str) {
        return isEmpty(str) ? "" : str;
    }

    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            char[] methodName = str.toCharArray();
            methodName[0] = toUpperCase(methodName[0]);
            return String.valueOf(methodName);
        }
    }

    public static char toUpperCase(char chars) {
        if ('a' <= chars && chars <= 'z') {
            chars = (char)(chars ^ 32);
        }

        return chars;
    }

    public static Boolean getBool(String value, Boolean defaultValue) {
        return isEmpty(value) ? defaultValue : "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value);
    }

    public static Integer getInt(String value, Integer defaultValue) {
        return !isEmpty(value) ? Integer.valueOf(value) : defaultValue;
    }

    public static Long getLong(String value, Long defaultValue) {
        return !isEmpty(value) ? Long.valueOf(value) : defaultValue;
    }

    public static Float getFloat(String value, Float defaultValue) {
        return !isEmpty(value) ? Float.valueOf(value) : defaultValue;
    }

    public static String get(String value, String defaultValue) {
        return !isEmpty(value) ? value : defaultValue;
    }
}

