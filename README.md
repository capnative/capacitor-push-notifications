# capacitor-push-notifications
A Capacitor plugin for handling Push Notifications using Firebase.

Capacitor 8 plugin using Firebase Cloud Messaging (FCM) on both iOS and Android.

## Install

```bash
npm install @capnative/capacitor-push-notifications
npx cap sync
```

## Setup

- **Android**: add `google-services.json` to `android/app/` and apply the `com.google.gms.google-services` Gradle plugin in your app.
- **iOS**: add `GoogleService-Info.plist` to your app target, enable the Push Notifications and Background Modes (Remote notifications) capabilities, and upload your APNs key to Firebase. Forward the APNs token in `AppDelegate` by posting `.capacitorDidRegisterForRemoteNotifications` (default in Capacitor templates).

## Usage

```ts
import { PushNotifications } from '@capnative/capacitor-push-notifications';

await PushNotifications.addListener('registration', ({ value }) => console.log('FCM token', value));
await PushNotifications.addListener('pushNotificationReceived', (n) => console.log(n));
await PushNotifications.addListener('pushNotificationActionPerformed', (a) => console.log(a));

if ((await PushNotifications.requestPermissions()).receive === 'granted') {
  await PushNotifications.register();
}
```

API: `checkPermissions`, `requestPermissions`, `register`, `unregister`, `getToken`, `subscribeToTopic`, `unsubscribeFromTopic`, and the events `registration`, `registrationError`, `pushNotificationReceived`, `pushNotificationActionPerformed`.
