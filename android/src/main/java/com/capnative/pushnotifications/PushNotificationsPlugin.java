package com.capnative.pushnotifications;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;

@CapacitorPlugin(
    name = "PushNotifications",
    permissions = @Permission(strings = { Manifest.permission.POST_NOTIFICATIONS }, alias = PushNotificationsPlugin.PUSH_NOTIFICATIONS)
)
public class PushNotificationsPlugin extends Plugin {

    static final String PUSH_NOTIFICATIONS = "receive";
    private static PushNotificationsPlugin instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    protected void handleOnDestroy() {
        if (instance == this) instance = null;
    }

    @Override
    protected void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getString("google.message_id") != null) {
            JSObject data = new JSObject();
            for (String key : extras.keySet()) {
                if (key.startsWith("google.") || key.startsWith("gcm.") || key.equals("from") || key.equals("collapse_key")) continue;
                Object value = extras.get(key);
                if (value != null) data.put(key, value.toString());
            }
            JSObject notification = new JSObject();
            notification.put("id", extras.getString("google.message_id"));
            notification.put("data", data);
            JSObject action = new JSObject();
            action.put("actionId", "tap");
            action.put("notification", notification);
            notifyListeners("pushNotificationActionPerformed", action, true);
        }
    }

    private boolean permissionGranted() {
        return Build.VERSION.SDK_INT < 33 || getPermissionState(PUSH_NOTIFICATIONS) == PermissionState.GRANTED;
    }

    private JSObject permissionResult() {
        JSObject result = new JSObject();
        result.put("receive", permissionGranted() ? "granted" : getPermissionState(PUSH_NOTIFICATIONS).toString());
        return result;
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        call.resolve(permissionResult());
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (permissionGranted()) {
            call.resolve(permissionResult());
        } else {
            requestPermissionForAlias(PUSH_NOTIFICATIONS, call, "permissionsCallback");
        }
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        call.resolve(permissionResult());
    }

    @PluginMethod
    public void register(PluginCall call) {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance()
            .getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful() || task.getResult() == null) {
                    Exception e = task.getException();
                    JSObject error = new JSObject();
                    error.put("error", e != null ? e.getLocalizedMessage() : "Unable to get FCM token");
                    notifyListeners("registrationError", error, true);
                } else {
                    onNewToken(task.getResult());
                }
            });
        call.resolve();
    }

    @PluginMethod
    public void unregister(PluginCall call) {
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        FirebaseMessaging.getInstance()
            .deleteToken()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) call.resolve();
                else call.reject("Failed to unregister", task.getException());
            });
    }

    @PluginMethod
    public void getToken(PluginCall call) {
        FirebaseMessaging.getInstance()
            .getToken()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    JSObject ret = new JSObject();
                    ret.put("value", task.getResult());
                    call.resolve(ret);
                } else {
                    call.reject("Failed to get token", task.getException());
                }
            });
    }

    @PluginMethod
    public void subscribeToTopic(PluginCall call) {
        String topic = call.getString("topic");
        if (topic == null) {
            call.reject("topic is required");
            return;
        }
        FirebaseMessaging.getInstance()
            .subscribeToTopic(topic)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) call.resolve();
                else call.reject("Failed to subscribe", task.getException());
            });
    }

    @PluginMethod
    public void unsubscribeFromTopic(PluginCall call) {
        String topic = call.getString("topic");
        if (topic == null) {
            call.reject("topic is required");
            return;
        }
        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(topic)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) call.resolve();
                else call.reject("Failed to unsubscribe", task.getException());
            });
    }

    @PluginMethod
    public void getDeliveredNotifications(PluginCall call) {
        NotificationManager notificationManager =
            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        JSArray notifications = new JSArray();
        for (StatusBarNotification statusBarNotification : notificationManager.getActiveNotifications()) {
            Notification nativeNotification = statusBarNotification.getNotification();
            Bundle extras = nativeNotification.extras;
            JSObject notification = new JSObject();
            notification.put("id", String.valueOf(statusBarNotification.getId()));
            if (statusBarNotification.getTag() != null) {
                notification.put("tag", statusBarNotification.getTag());
            }
            if (extras != null) {
                CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
                CharSequence body = extras.getCharSequence(Notification.EXTRA_TEXT);
                if (title != null) notification.put("title", title.toString());
                if (body != null) notification.put("body", body.toString());
            }
            JSObject data = new JSObject();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    if (key.startsWith("android.") || key.startsWith("androidx.")
                        || key.startsWith("google.") || key.startsWith("gcm.")
                        || key.equals("from") || key.equals("collapse_key")
                        || Notification.EXTRA_TITLE.equals(key) || Notification.EXTRA_TEXT.equals(key)) continue;
                    Object value = extras.get(key);
                    if (value instanceof String || value instanceof Number || value instanceof Boolean) {
                        data.put(key, value);
                    }
                }
            }
            notification.put("data", data);
            notifications.put(notification);
        }
        JSObject result = new JSObject();
        result.put("notifications", notifications);
        call.resolve(result);
    }

    @PluginMethod
    public void removeDeliveredNotifications(PluginCall call) {
        JSArray notifications = call.getArray("notifications");
        if (notifications == null) {
            call.reject("notifications must be an array");
            return;
        }
        int count = notifications.length();
        int[] ids = new int[count];
        String[] tags = new String[count];
        try {
            for (int i = 0; i < count; i++) {
                JSONObject notification = notifications.getJSONObject(i);
                String id = notification.getString("id");
                if (id == null) {
                    call.reject("Each notification must have an id");
                    return;
                }
                ids[i] = Integer.parseInt(id);
                tags[i] = notification.optString("tag", null);
            }
        } catch (JSONException | NumberFormatException e) {
            call.reject("Each notification must have a valid id", e);
            return;
        }
        NotificationManager notificationManager =
            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        for (int i = 0; i < count; i++) {
            notificationManager.cancel(tags[i], ids[i]);
        }
        call.resolve();
    }

    @PluginMethod
    public void removeAllDeliveredNotifications(PluginCall call) {
        NotificationManager notificationManager =
            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        for (StatusBarNotification notification : notificationManager.getActiveNotifications()) {
            notificationManager.cancel(notification.getTag(), notification.getId());
        }
        call.resolve();
    }

    public static void onNewToken(String token) {
        if (instance != null) {
            JSObject ret = new JSObject();
            ret.put("value", token);
            instance.notifyListeners("registration", ret, true);
        }
    }

    public static void sendRemoteMessage(RemoteMessage message) {
        if (instance == null) return;
        JSObject data = new JSObject();
        for (String key : message.getData().keySet()) {
            data.put(key, message.getData().get(key));
        }
        JSObject notification = new JSObject();
        notification.put("id", message.getMessageId());
        notification.put("data", data);
        RemoteMessage.Notification n = message.getNotification();
        if (n != null) {
            notification.put("title", n.getTitle());
            notification.put("body", n.getBody());
        }
        instance.notifyListeners("pushNotificationReceived", notification, true);
    }
}
