package com.javabaas.javasdk;


import com.javabaas.javasdk.callback.JBBooleanCallback;
import com.javabaas.javasdk.callback.JBLoginCallback;
import com.javabaas.javasdk.callback.JBObjectCallback;
import com.javabaas.javasdk.callback.JBUpdatePasswordCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangyilin on 2017/8/30.
 */
public class JBUser extends JBObject {

    private static final String SESSIONTOKEN = "sessionToken";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String PHONE = "phone";
    private static final String CODE = "code";
    private static final String AUTH = "auth";
    private static JBUser currentUser;

    /**
     * 当前登录的用户<br/>
     * 在安卓设备使用该方法获取当前用户
     *
     * @return 当前用户
     */
    public static JBUser getCurrentUser() {
        return currentUser;
    }

    public static void updateCurrentUser(JBUser user) {
        JBUser.currentUser = user;
    }

    public String getSessionToken() {
        return (String) get(SESSIONTOKEN);
    }

    public void setSessionToken(String sessionToken) {
        put(SESSIONTOKEN, sessionToken);
    }

    public String getUsername() {
        return (String) get(USERNAME);
    }

    public void setUsername(String username) {
        put(USERNAME, username);
    }

    public String getEmail() {
        return (String) get(EMAIL);
    }

    public void setEmail(String email) {
        put(EMAIL, email);
    }

    public void setPassword(String password) {
        put(PASSWORD, password);
    }

    public String getPhone() {
        return (String) get(PHONE);
    }

    public void setPhone(String phone) {
        put(PHONE, phone);
    }

    public JBUser() {
        super(userClassName());
    }

    public static String userClassName() {
        return "_User";
    }

    /**
     * 新建className为_User的query
     *
     */
    public static JBQuery<JBUser> getQuery() {
        return new JBQuery<>(JBUser.userClassName());
    }

    /**
     * 通过用户名和密码注册用户 同步
     *
     * @throws JBException 异常信息
     */
    public void signUp() throws JBException {
        signUpFromJavabaas(true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 通过用户名和密码注册用户 异步
     *
     * @param callback 成功或失败回调
     */
    public void signUpInBackground(JBBooleanCallback callback) {
        signUpFromJavabaas(false, callback);
    }

    private void signUpFromJavabaas(final boolean sync, final JBBooleanCallback callback) {
        String path = JBHttpClient.getUserPath();
        Map<String, Object> body = this.getObjectForSaveBody();
        saveToJavaBaas(path, JBHttpMethod.POST, null, body, sync, callback);
    }

    /**
     * 获取短信验证码 同步
     *
     * @param phone 手机号
     * @throws JBException 异常信息
     */
    public static void getSmsCode(String phone) throws JBException {
        getSmsCodeFromJavabaas(phone, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 获取短信验证码 异步
     *
     * @param phone 手机号
     * @param callback 获取短信验证码成功或者失败回调
     */
    public static void getSmsCodeInBackground(String phone, JBBooleanCallback callback) {
        getSmsCodeFromJavabaas(phone,false, callback);
    }

    private static void getSmsCodeFromJavabaas(final String phone, final boolean sync, final JBBooleanCallback callback) {
        if (JBUtils.isEmpty(phone)) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("getSmsCode/" + phone);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    /**
     * 第三方登录注册登录 同步<br/>
     * 如果已经注册直接登录,如果没有注册会自动创建一个新的用户
     *
     * @param auth 授权信息
     * @param type 1:微博 2:qq 3:微信 4:微信小程序
     * @throws JBException 异常信息
     */
    public void signUpWithSns(JBAuth auth, JBSnsType type) throws JBException {
        signUpWithSnsFromJavabaas(auth, type, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 第三方登录注册登录 异步<br/>
     * 如果已经注册直接登录,如果没有注册会自动创建一个新的用户
     *
     * @param auth 授权信息
     * @param type 1:微博 2:qq 3:微信 4:微信小程序
     * @param callback 成功或失败回调
     */
    public void signUpWithSnsInBackground(JBAuth auth, JBSnsType type, JBBooleanCallback callback) {
        signUpWithSnsFromJavabaas(auth, type, false, callback);
    }

    private void signUpWithSnsFromJavabaas(JBAuth auth, JBSnsType type, boolean sync, JBBooleanCallback callback) {
        String path = JBHttpClient.getUserPath("registerWithSns/" + type.getCode());
        Map<String, Object> body = new HashMap<>();
        body.put(AUTH, auth);
        body.put("user", getObjectForSaveBody());
        saveToJavaBaas(path, JBHttpMethod.POST, null, body, sync, callback);
    }

    /**
     * 用户名密码登录 同步
     *
     * @param username 用户名
     * @param password 密码
     * @return user信息
     * @throws JBException 异常
     */
    public static JBUser login(String username, String password) throws JBException {
        final JBUser[] users = {null};
        loginFromJavabaas(username, password, true, new JBLoginCallback() {
            @Override
            public void done(boolean success, JBUser user, JBException e) {
                if (success) {
                    users[0] = user;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return users[0];
    }

    /**
     * 用户名密码登录 异步
     *
     * @param username 用户名
     * @param password 密码
     * @param callback 登录回调
     */
    public static void loginInBackground(String username, String password, JBLoginCallback callback) {
        loginFromJavabaas(username, password, false, callback);
    }

    private static void loginFromJavabaas(final String username, final String password, final boolean sync, final JBLoginCallback callback) {
        if (JBUtils.isEmpty(username) || JBUtils.isEmpty(password)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("login");
        JBHttpParams params = new JBHttpParams();
        params.put("username", username);
        params.put("password", password);
        final JBUser user = new JBUser();
        user.setUsername(username);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, params, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                processResult(user, result.getData());
                callback.done(true, user, null);
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

    /**
     * 手机号验证码登录 同步
     *
     * @param phone 手机号
     * @param code 验证码
     * @return user信息
     * @throws JBException 异常信息
     */
    public static JBUser loginWithPhone(String phone, String code) throws JBException {
        final JBUser[] users = {null};
        loginWithPhoneFromJavabaas(phone, code, true, new JBLoginCallback() {
            @Override
            public void done(boolean success, JBUser user, JBException e) {
                if (success) {
                    users[0] = user;
                } else {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return users[0];
    }

    /**
     * 手机号验证码登录 异步
     *
     * @param phone 手机号
     * @param code 验证码
     * @param callback 登录回调
     */
    public static void loginWithPhoneInBackground(String phone, String code, JBLoginCallback callback) {
        loginWithPhoneFromJavabaas(phone, code, false, callback);
    }
    
    private static void loginWithPhoneFromJavabaas(final String phone, final String code, final boolean sync, final JBLoginCallback callback) {
        if (JBUtils.isEmpty(phone) || JBUtils.isEmpty(code)) {
            callback.done(false, null, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("loginWithPhone");
        Map<String, Object> body = new HashMap<>();
        body.put(PHONE, phone);
        body.put(CODE, code);
        sendRequestForLogin(path, body, sync, callback);
    }

    /**
     * 第三方登录 同步<br/>
     * 如果之前没有注册用户,本方法不会创建新用户,返回登录失败信息
     *
     * @param auth 第三方授权信息
     * @param type 1:微博 2:qq 3:微信 4:微信小程序
     * @return user信息
     * @throws JBException 异常信息
     */
    public static JBUser loginWithSns(JBAuth auth, JBSnsType type) throws JBException {
        final JBUser[] users = {null};
        loginWithSnsFromJavabaas(auth, type, true, new JBLoginCallback() {
            @Override
            public void done(boolean success, JBUser user, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                } else {
                    users[0] = user;
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return users[0];
    }

    /**
     * 第三方登录 异步<br/>
     * 如果之前没有注册用户,本方法不会创建新用户,返回登录失败信息
     *
     * @param auth 第三方授权信息
     * @param type 1:微博 2:qq 3:微信 4:微信小程序
     * @param callback 登录回调
     */
    public static void loginWithSnsInBackground(JBAuth auth, JBSnsType type, JBLoginCallback callback) {
        loginWithSnsFromJavabaas(auth, type, false, callback);
    }

    private static void loginWithSnsFromJavabaas(JBAuth auth, JBSnsType type, boolean sync, JBLoginCallback callback) {
        String path = JBHttpClient.getUserPath("loginWithSns/" + type.getCode());
        sendRequestForLogin(path, auth, sync, callback);
    }

    private static void sendRequestForLogin(final String path, final Map<String, Object> body, final boolean sync, final JBLoginCallback callback) {
        final JBUser user = new JBUser();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                processResult(user, result.getData());
                callback.done(true, user, null);
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

    /**
     * 获取绑定用短信验证码 同步
     *
     * @param phone 手机号
     * @throws JBException 异常信息
     */
    public static void getBindCode(String phone) throws JBException {
        getBindCodeFromJavabaas(phone, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 获取绑定用短信验证码 异步
     *
     * @param phone 手机号
     * @param callback 获取绑定用短信验证码成功或者失败回调
     */
    public static void getBindCodeInBackground(String phone, JBBooleanCallback callback) {
        getBindCodeFromJavabaas(phone,false, callback);
    }

    private static void getBindCodeFromJavabaas(final String phone, final boolean sync, final JBBooleanCallback callback) {
        if (JBUtils.isEmpty(phone)) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("getBindCode/" + phone);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.GET, null, null, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                } else {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    /**
     * 绑定手机号 同步
     *
     * @param phone 手机号
     * @param code  手机绑定验证码
     * @throws JBException 异常信息
     */
    public static void bindPhone(String phone, String code) throws JBException {
        bindPhoneWithJavabaas(phone, code, true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 绑定手机号 异步
     *
     * @param phone 手机号
     * @param code  手机绑定验证码
     * @param callback 回调信息
     */
    public static void bindPhoneInBackground(String phone, String code, JBBooleanCallback callback) {
        bindPhoneWithJavabaas(phone, code, false, callback);
    }

    private static void bindPhoneWithJavabaas(final String phone, final String code, final boolean sync, final JBBooleanCallback callback) {
        if (JBUtils.isEmpty(phone) || JBUtils.isEmpty(code)) {
            callback.done(false, new JBException(JBCode.REQUEST_PARAM_ERROR));
            return;
        }
        String path = JBHttpClient.getUserPath("bindPhone");
        Map<String, Object> body = new HashMap<>();
        body.put("phone", phone);
        body.put("code", code);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.POST, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                } else {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    /**
     * 重置用户sessionToken 同步<br/>
     * 用户本人可以reset自己的sessionToken,master权限可以reset所有人的sessionToken.
     *
     * 本方法2.0.1版本暂时取消,以后版本可能会加上
     *
     * @param userId 用户id
     * @throws JBException 异常信息
     */
    private static void resetSessionToken(String userId) throws JBException {
        String path = JBHttpClient.getUserPath(userId + "/resetSessionToken");
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, null, true, new JBObjectCallback() {
            @Override
            public void onFailure(JBException error) {
                JBExceptionHolder.add(error);
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 修改密码 同步<br/>
     * 修改密码会重置用户的sessionToken
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 新的sessionToken
     * @throws JBException 异常信息
     */
    public String updatePassword(String oldPassword, String newPassword) throws JBException {
        String sessionToken = null;
        updatePasswordFromJavabaas(oldPassword, newPassword, true, new JBUpdatePasswordCallback() {
            @Override
            public void done(boolean success, String sessionToken, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                } else {
                    sessionToken = sessionToken;
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
        return sessionToken;
    }

    /**
     * 修改密码 异步<br/>
     * 修改密码会重置用户的sessionToken
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param callback 修改密码回调,里面包含更新后的sessionToken
     */
    public void updatePasswordInBackground(String oldPassword, String newPassword, JBUpdatePasswordCallback callback) {
        updatePasswordFromJavabaas(oldPassword, newPassword, false, callback);
    }

    private void updatePasswordFromJavabaas(final String oldPassword, final String newPassword, final boolean sync, final JBUpdatePasswordCallback callback) {
        String userId = this.getObjectId();
        if (JBUtils.isEmpty(userId)) {
            callback.done(false, null, new JBException(JBCode.USER_NOT_LOGIN));
            return;
        }
        String path = JBHttpClient.getUserPath(userId + "/updatePassword");
        Map<String, Object> body = new HashMap<>();
        body.put("oldPassword", oldPassword);
        body.put("newPassword", newPassword);
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback == null) {
                    return;
                }
                if (result.getData().get("sessionToken") == null) {
                    callback.done(false, null, new JBException(JBCode.INTERNAL_ERROR.getCode(), "未能获取sessionToken"));
                } else {
                    String sessionToken = (String) result.getData().get("sessionToken");
                    callback.done(true, sessionToken, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, null, error);
                }
            }
        });
    }

    /**
     * 修改用户信息 同步
     *
     * @throws JBException 异常信息
     */
    public void update() throws JBException {
        updateFromJavabaas(true, new JBBooleanCallback() {
            @Override
            public void done(boolean success, JBException e) {
                if (!success) {
                    JBExceptionHolder.add(e);
                }
            }
        });
        if (JBExceptionHolder.exists()) {
            throw JBExceptionHolder.remove();
        }
    }

    /**
     * 修改用户信息 异步
     *
     * @param callback 修改成功或失败回调
     */
    public void updateInBackground(JBBooleanCallback callback) {
        updateFromJavabaas(false, callback);
    }

    private void updateFromJavabaas(final boolean sync, final JBBooleanCallback callback) {
        String userId = getObjectId();
        if (JBUtils.isEmpty(userId)) {
            callback.done(false, new JBException(JBCode.USER_NOT_LOGIN));
            return;
        }
        String path = JBHttpClient.getUserPath(userId);
        Map<String, Object> body = getObjectForSaveBody();
        JBHttpClient.INSTANCE().sendRequest(path, JBHttpMethod.PUT, null, body, sync, new JBObjectCallback() {
            @Override
            public void onSuccess(JBResult result) {
                if (callback != null) {
                    callback.done(true, null);
                }
            }

            @Override
            public void onFailure(JBException error) {
                if (callback != null) {
                    callback.done(false, error);
                }
            }
        });
    }

    protected static void processResult(JBUser user, Map<String, Object> map) {
        if (map == null || map.get("result") == null) {
            return;
        }
        Object o = map.get("result");
        if (o instanceof Map) {
            JBUtils.copyPropertiesFromMapToJBObject(user, (Map<String, Object>) o);
        }
    }
}
