import Foundation
import Capacitor
import UserNotifications
import FirebaseCore
import FirebaseMessaging

@objc(PushNotificationsPlugin)
public class PushNotificationsPlugin: CAPPlugin, CAPBridgedPlugin, UNUserNotificationCenterDelegate, MessagingDelegate {
    public let identifier = "PushNotificationsPlugin"
    public let jsName = "PushNotifications"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "checkPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "requestPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "register", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "unregister", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getToken", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "subscribeToTopic", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "unsubscribeFromTopic", returnType: CAPPluginReturnPromise)
    ]

    override public func load() {
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self
        NotificationCenter.default.addObserver(self, selector: #selector(didRegisterForRemoteNotifications(_:)),
                                               name: .capacitorDidRegisterForRemoteNotifications, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(didFailToRegisterForRemoteNotifications(_:)),
                                               name: .capacitorDidFailToRegisterForRemoteNotifications, object: nil)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    private func permissionString(_ status: UNAuthorizationStatus) -> String {
        switch status {
        case .authorized, .provisional, .ephemeral: return "granted"
        case .denied: return "denied"
        default: return "prompt"
        }
    }

    @objc override public func checkPermissions(_ call: CAPPluginCall) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            call.resolve(["receive": self.permissionString(settings.authorizationStatus)])
        }
    }

    @objc override public func requestPermissions(_ call: CAPPluginCall) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { _, error in
            if let error = error {
                call.reject(error.localizedDescription)
                return
            }
            UNUserNotificationCenter.current().getNotificationSettings { settings in
                call.resolve(["receive": self.permissionString(settings.authorizationStatus)])
            }
        }
    }

    @objc func register(_ call: CAPPluginCall) {
        Messaging.messaging().isAutoInitEnabled = true
        DispatchQueue.main.async {
            UIApplication.shared.registerForRemoteNotifications()
        }
        call.resolve()
    }

    @objc func unregister(_ call: CAPPluginCall) {
        Messaging.messaging().isAutoInitEnabled = false
        Messaging.messaging().deleteToken { error in
            if let error = error {
                call.reject(error.localizedDescription)
                return
            }
            DispatchQueue.main.async {
                UIApplication.shared.unregisterForRemoteNotifications()
            }
            call.resolve()
        }
    }

    @objc func getToken(_ call: CAPPluginCall) {
        Messaging.messaging().token { token, error in
            if let error = error {
                call.reject(error.localizedDescription)
            } else if let token = token {
                call.resolve(["value": token])
            } else {
                call.reject("Failed to get token")
            }
        }
    }

    @objc func subscribeToTopic(_ call: CAPPluginCall) {
        guard let topic = call.getString("topic") else {
            call.reject("topic is required")
            return
        }
        Messaging.messaging().subscribe(toTopic: topic) { error in
            if let error = error { call.reject(error.localizedDescription) } else { call.resolve() }
        }
    }

    @objc func unsubscribeFromTopic(_ call: CAPPluginCall) {
        guard let topic = call.getString("topic") else {
            call.reject("topic is required")
            return
        }
        Messaging.messaging().unsubscribe(fromTopic: topic) { error in
            if let error = error { call.reject(error.localizedDescription) } else { call.resolve() }
        }
    }

    @objc func didRegisterForRemoteNotifications(_ notification: NSNotification) {
        if let deviceToken = notification.object as? Data {
            Messaging.messaging().apnsToken = deviceToken
        }
    }

    @objc func didFailToRegisterForRemoteNotifications(_ notification: NSNotification) {
        let error = (notification.object as? Error)?.localizedDescription ?? "Unable to register for remote notifications"
        notifyListeners("registrationError", data: ["error": error], retainUntilConsumed: true)
    }

    // MARK: MessagingDelegate
    public func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        if let fcmToken = fcmToken {
            notifyListeners("registration", data: ["value": fcmToken], retainUntilConsumed: true)
        }
    }

    // MARK: UNUserNotificationCenterDelegate
    private func payload(_ notification: UNNotification) -> JSObject {
        let content = notification.request.content
        var result = JSObject()
        result["id"] = notification.request.identifier
        result["title"] = content.title
        result["body"] = content.body
        var data = JSObject()
        for (key, value) in content.userInfo {
            if let key = key as? String, let value = JSTypes.coerceDictionaryToJSObject(["v": value])?["v"] {
                data[key] = value
            }
        }
        result["data"] = data
        return result
    }

    public func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification,
                                       withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        notifyListeners("pushNotificationReceived", data: payload(notification), retainUntilConsumed: true)
        completionHandler([.banner, .list, .sound, .badge])
    }

    public func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse,
                                       withCompletionHandler completionHandler: @escaping () -> Void) {
        let actionId = response.actionIdentifier == UNNotificationDefaultActionIdentifier ? "tap" : response.actionIdentifier
        notifyListeners("pushNotificationActionPerformed",
                        data: ["actionId": actionId, "notification": payload(response.notification)],
                        retainUntilConsumed: true)
        completionHandler()
    }
}
