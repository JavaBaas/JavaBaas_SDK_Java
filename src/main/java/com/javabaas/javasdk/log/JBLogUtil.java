package com.javabaas.javasdk.log;

import com.javabaas.javasdk.JBUtils;

import java.util.Map;

/**
 * Created by zangyilin on 2017/11/1.
 */
public class JBLogUtil {
    public static class jbLog {
        public static boolean showJBLog = true;

        public static void i(String text) {
            if (showJBLog) {
                log.i(text);
            }
        }

        public static void i(Object o) {
            if (showJBLog) {
                log.i("" + o);
            }
        }

        public static void d(String text) {
            if (showJBLog) {
                log.d(text);
            }
        }

        public static void e(String text) {
            if (showJBLog) {
                log.e(text);
            }
        }
    }

    public static class log {
        public static final boolean show = true;
        public static final String Tag = "===JavaBaas===";
        public static String Cname = "";
        public static String Mname = "";
        private static JBLogger logger = JBLogger.instance();

        private static boolean shouldShow(int tag_level) {
            return show
                    && ((tag_level & JBLogger.instance().getLogLevel()) > 0);
        }

        protected static void getTrace() {
            StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
            String className = new StringBuilder().append(caller.getClassName()).toString();
            className = className.substring(className.lastIndexOf(".") + 1);

            Cname = className;
            Mname =
                    new StringBuilder().append(caller.getMethodName() + "->" + caller.getLineNumber() + ": ")
                            .toString();
        }

        // ================================================================================
        // Verbose
        // ================================================================================

        public static void v(String text) {
            if (!shouldShow(JBLogger.LOG_LEVEL_VERBOSE))
                return;

            getTrace();
            if (null == text) {
                text = "null";
            }

            logger.v(Tag, Mname + text);
        }

        // ================================================================================
        // Debug
        // ================================================================================

        public static void d(String text) {
            if (!shouldShow(JBLogger.LOG_LEVEL_DEBUG))
                return;

            getTrace();
            if (null == text) {
                text = "null";
            }

            logger.d(Tag, Cname + "->" + Mname + text);
        }

        public static void d(Map<String, Object> o) {
            if (!shouldShow(JBLogger.LOG_LEVEL_DEBUG))
                return;
            String text = "";
            getTrace();
            if (null == o) {
                text = "null";
            } else {
                try {
                    text = JBUtils.writeValueAsString(o);
                } catch (Exception e) {

                }
            }

            logger.d(Tag, Cname + "->" + Mname + text);
        }

        public static void d(int text) {
            d("" + text);
        }

        public static void d(float text) {
            d("" + text);
        }

        public static void d(double text) {
            d("" + text);
        }

        public static void d() {
            if (!shouldShow(JBLogger.LOG_LEVEL_DEBUG))
                return;

            getTrace();
            logger.d(Tag, Tag + "->" + Mname + "");
        }

        public static void d(String Tag, String text) {
            if (!shouldShow(JBLogger.LOG_LEVEL_DEBUG))
                return;

            getTrace();
            logger.d(Tag, Cname + "->" + Mname + text);
        }

        public static void d(String text, Exception e) {
            if (!shouldShow(JBLogger.LOG_LEVEL_ERROR))
                return;
            String tmp = Cname + "->" + Mname + text + ":" + e.toString();
            logger.d(Tag, tmp);
            e.printStackTrace();
        }

        // ================================================================================
        // Info
        // ================================================================================

        public static void i(String text) {
            if (!shouldShow(JBLogger.LOG_LEVEL_INFO))
                return;

            getTrace();
            if (null == text) {
                text = "null";
            }

            logger.i(Tag, Mname + text);
        }

        // ================================================================================
        // Warning
        // ================================================================================

        public static void w(String text) {
            if (!shouldShow(JBLogger.LOG_LEVEL_WARNING))
                return;

            getTrace();
            if (null == text) {
                text = "null";
            }

            logger.w(Tag, Mname + text);
        }

        // ================================================================================
        // Error
        // ================================================================================

        public static void e(String text) {
            if (!shouldShow(JBLogger.LOG_LEVEL_ERROR))
                return;

            getTrace();
            if (null == text) {
                text = "null";
            }

            logger.e(Tag, Cname + "->" + Mname + text);
        }

        public static void e() {
            if (!shouldShow(JBLogger.LOG_LEVEL_ERROR))
                return;
            getTrace();
            logger.e(Tag, Cname + "->" + Mname + "");
        }

        public static void e(String text, Exception e) {
            if (!shouldShow(JBLogger.LOG_LEVEL_ERROR))
                return;
            String tmp = text + Mname + "err:" + e.toString();
            logger.e(Tag, tmp);
            e.printStackTrace();
        }

        public static void e(String Tag, String text) {
            if (!shouldShow(JBLogger.LOG_LEVEL_ERROR))
                return;
            getTrace();
            logger.e(Tag, Cname + "->" + Mname + text);
        }

        public static void e(String Tag, String text, Exception e) {
            if (!shouldShow(JBLogger.LOG_LEVEL_ERROR))
                return;
            getTrace();
            logger.e(Tag, Cname + "->" + Mname + text + " err:" + e.toString());
        }
    }
}
