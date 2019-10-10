package com.maosong.tools;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static android.os.Process.myPid;
import static android.os.Process.myTid;

/**
 * Created by tory on 2018/7/15.
 */

public class LogUtil {
    public final static String LOG_FILE_DIR =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Lesing" + File.separator + "log" + File.separator;

    /**
     * 分割线
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /**
     * json index
     */
    private static final int JSON_INDENT = 4;

    /**
     * 写文件handler
     */
    private static Handler writeHandler;

    private static SparseArray<String> tagMap = new SparseArray<>();
    /**
     * 超过DEBUG 以上的日志级别
     */
    private static int printLevel = Log.DEBUG;
    /**
     * 当前日志输出文件
     */
    private static File currentLogFile;

    public static boolean IS_DEBUG = true;

    static {
        tagMap.put(Log.VERBOSE, "V");
        tagMap.put(Log.DEBUG, "D");
        tagMap.put(Log.INFO, "I");
        tagMap.put(Log.WARN, "W");
        tagMap.put(Log.ERROR, "E");
        HandlerThread writeThread = new HandlerThread("JavLog");
        writeThread.start();
        writeHandler = new Handler(writeThread.getLooper());
    }

    private static void write(int level, String tag, String msg) {
        write(level, tag, msg, null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void write(final int level, final String tag, final String msg, final Throwable ex) {
        if (level < printLevel) {
            return;
        }
        writeHandler.post(new Runnable() {
            @Override
            public void run() {
                PrintWriter writer = null;
                SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String dateStr = format.format(new Date(System.currentTimeMillis()));
                try {
                    File currentLogFileDir = new File(LOG_FILE_DIR);
                    if (!currentLogFileDir.exists()){
                        currentLogFileDir.mkdirs();
                    }
                    Date date = new Date();
                    currentLogFile = new File(currentLogFileDir, String.format("Lesing-%tF.log", date));
                    if (!currentLogFile.exists()) {
                        currentLogFile.createNewFile();
                    }
                    writer = new PrintWriter(new FileOutputStream(currentLogFile, true), true);
                    writer.append(dateStr).append(" ").
                            append(String.valueOf(myPid())).append(" ").
                            append(String.valueOf(myTid())).append(" ").
                            append(tagMap.get(level)).append(" ").
                            append(tag).append(" ").
                            append(msg).append("\n");
                    if (ex != null) {
                        ex.printStackTrace(writer);
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        });
    }


    /**
     * 简单消息打印: tag 自动获取
     */
    public static void d(String msg) {
        d(msg, (Throwable) null);
    }

    public static void w(String msg) {
        w(msg, (Throwable) null);
    }

    public static void e(String msg) {
        e(msg, (Throwable) null);
    }

    public static void i(String msg) {
        i(msg, (Throwable) null);
    }

    public static void v(String msg) {
        i(msg, (Throwable) null);
    }

    @SuppressWarnings("unused")
    public static void wtf(String msg) {
        wtf(msg, (Throwable) null);
    }

    /**
     * 简单消息打印和异常答应: tag 自动获取
     * 主要是调试堆栈用到异常
     */
    public static void d(String msg, Throwable ex) {
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = getTag(caller);
        String msgHeader = addMsgHead(caller);
        d(tag, msgHeader + msg, ex);
    }

    public static void w(String msg, Throwable ex) {
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = getTag(caller);
        String msgHeader = addMsgHead(caller);
        w(tag, msgHeader + msg, ex);
    }

    public static void e(String msg, Throwable ex) {
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = getTag(caller);
        String msgHeader = addMsgHead(caller);
        e(tag, msgHeader + msg, ex);
    }

    public static void i(String msg, Throwable ex) {
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = getTag(caller);
        String msgHeader = addMsgHead(caller);
        i(tag, msgHeader + msg, ex);
    }

    public static void v(String msg, Throwable ex) {
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = getTag(caller);
        String msgHeader = addMsgHead(caller);
        v(tag, msgHeader + msg, ex);
    }

    @SuppressWarnings("WeakerAccess")
    public static void wtf(String msg, Throwable ex) {
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = getTag(caller);
        String msgHeader = addMsgHead(caller);
        wtf(tag, msgHeader + msg, ex);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, (Throwable) null);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, (Throwable) null);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, (Throwable) null);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, (Throwable) null);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, (Throwable) null);
    }

    @SuppressWarnings("unused")
    public static void wtf(String tag, String msg) {
        wtf(tag, msg, null);
    }

    /* */

    /**
     * 格式化打印
     * wrap {@link Log#d(String, String)} with {@link String#format(String, Object...)}
     */
    public static void d(String tag, String format, Object... args) {
        d(tag, String.format(format, args));
    }

    public static void i(String tag, String format, Object... args) {
        i(tag, String.format(format, args));
    }

    public static void v(String tag, String format, Object... args) {
        v(tag, String.format(format, args));
    }

    public static void w(String tag, String format, Object... args) {
        w(tag, String.format(format, args));
    }

    public static void e(String tag, String format, Object... args) {
        e(tag, String.format(format, args));
    }

    public static void d(String tag, String msg, Throwable ex) {
        write(Log.DEBUG, tag, msg, ex);
        if (IS_DEBUG) {
            Log.d(tag, msg, ex);
        }
    }

    public static void e(String tag, String msg, Throwable ex) {
        write(Log.ERROR, tag, msg, ex);
        if (IS_DEBUG) {
            Log.e(tag, msg, ex);
        }
    }

    public static void w(String tag, String msg, Throwable ex) {
        write(Log.WARN, tag, msg, ex);
        if (IS_DEBUG) {
            Log.w(tag, msg, ex);
        }
    }

    public static void i(String tag, String msg, Throwable ex) {
        write(Log.INFO, tag, msg);
        if (IS_DEBUG) {
            Log.i(tag, msg, ex);
        }
    }

    public static void v(String tag, String msg, Throwable ex) {
        write(Log.VERBOSE, tag, msg);
        if (IS_DEBUG) {
            Log.v(tag, msg, ex);
        }
    }

    /**
     * 中断（ASSERT）级别的错误，毕竟是What The Fuck，
     */
    @SuppressWarnings("WeakerAccess")
    public static int wtf(String tag, String msg, Throwable ex) {
        write(Log.ASSERT, tag, msg, ex);
        if (IS_DEBUG) {
            Log.w(tag, msg, ex);
        }
        return 0;
    }

    @SuppressWarnings("unused")
    private static void printStackTrace(String tag) {
        String msg = "stack trace";
        RuntimeException ex = new RuntimeException();
        write(Log.DEBUG, tag, msg, ex);
        Log.d(tag, msg, ex);
    }

    @SuppressWarnings("unused")
    public static void setPrintLevel(int level) {
        printLevel = level;
    }

    /**
     * 打印JSon文件
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static void printJson(String tag, String msg, String headString) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "║ " + line);
        }
        printLine(tag, false);
    }

    /**
     * 打印XML文件
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static void printXml(String tag, String xml, String headString) {
        if (xml != null) {
            xml = formatXML(xml);
            xml = headString + "\n" + xml;
        } else {
            xml = headString + "Log with null object";
        }

        printLine(tag, true);
        String[] lines = xml.split(LINE_SEPARATOR);
        for (String line : lines) {
            if (!isEmpty(line)) {
                Log.d(tag, "║ " + line);
            }
        }
        printLine(tag, false);
    }

    private static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }


    private static String formatXML(String inputXML) {
        try {
            Source xmlInput = new StreamSource(new StringReader(inputXML));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (Exception e) {
            e.printStackTrace();
            return inputXML;
        }
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    private static String getTag(StackTraceElement caller) {
        String callerClazzName = caller.getClassName();
        String simpleClassName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return simpleClassName;
    }

    private static String addMsgHead(StackTraceElement caller) {
        String msgHeader = "%s(L:%d)...";
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        msgHeader = String.format(Locale.ENGLISH, msgHeader, methodName, lineNumber);
        return msgHeader;
    }
}
