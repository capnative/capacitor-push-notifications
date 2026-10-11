# Capacitor Push Notifications

A Capacitor plugin for handling push notifications using Firebase Cloud Messaging (FCM) on iOS and Android.

## Compatibility

| Plugin version | Capacitor compatibility | Maintained |
| -------------- | ----------------------- | ---------- |
| v8.\*.\*       | v8.\*.\*                | ✅         |

## Supported platforms

| Platform | Support | Minimum version            |
| -------- | ------- | -------------------------- |
| iOS      | ✅      | iOS 15                     |
| Android  | ✅      | API level 24 (Android 7.0) |
| Web      | ❌      | —                          |

## Install

```bash
npm install @capnative/capacitor-push-notifications
npx cap sync
```

## Setup

Configure Firebase Cloud Messaging for your app before registering for notifications:

- **Android**: Add `google-services.json` to `android/app/`.
- **iOS**: Add `GoogleService-Info.plist` to your app target, enable the Push Notifications and Background Modes (Remote notifications) capabilities, and upload your APNs authentication key to Firebase.

## Basic usage

```typescript
import { PushNotifications } from '@capnative/capacitor-push-notifications';

await PushNotifications.addListener('registration', ({ value }) => {
  console.log('FCM token:', value);
});

await PushNotifications.addListener('registrationError', ({ error }) => {
  console.error('FCM registration failed:', error);
});

await PushNotifications.addListener('pushNotificationReceived', (notification) => {
  console.log('Notification received:', notification);
});

await PushNotifications.addListener('pushNotificationActionPerformed', (action) => {
  console.log('Notification action:', action);
});

const permission = await PushNotifications.requestPermissions();
if (permission.receive === 'granted') {
  await PushNotifications.register();
}
```

## API

<docgen-index>

* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`register()`](#register)
* [`unregister()`](#unregister)
* [`getToken()`](#gettoken)
* [`subscribeToTopic(...)`](#subscribetotopic)
* [`unsubscribeFromTopic(...)`](#unsubscribefromtopic)
* [`getDeliveredNotifications()`](#getdeliverednotifications)
* [`removeDeliveredNotifications(...)`](#removedeliverednotifications)
* [`removeAllDeliveredNotifications()`](#removealldeliverednotifications)
* [`addListener('registration', ...)`](#addlistenerregistration)
* [`addListener('registrationError', ...)`](#addlistenerregistrationerror)
* [`addListener('pushNotificationReceived', ...)`](#addlistenerpushnotificationreceived)
* [`addListener('pushNotificationActionPerformed', ...)`](#addlistenerpushnotificationactionperformed)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

Checks whether push notification permission is granted.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

Requests permission to receive push notifications and returns the resulting status.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### register()

```typescript
register() => Promise<void>
```

Starts FCM registration. The resulting token is emitted by the `registration` event; registration failures are emitted by `registrationError`.

--------------------


### unregister()

```typescript
unregister() => Promise<void>
```

Unregisters from push notifications and deletes the FCM token.

--------------------


### getToken()

```typescript
getToken() => Promise<Token>
```

Retrieves the current FCM registration token.

**Returns:** <code>Promise&lt;<a href="#token">Token</a>&gt;</code>

--------------------


### subscribeToTopic(...)

```typescript
subscribeToTopic(options: { topic: string; }) => Promise<void>
```

Subscribes this app installation to an FCM topic.

**Options:**

| Param | Type |
| --- | --- |
| **`options`** | <code>{ topic: string; }</code> |

--------------------


### unsubscribeFromTopic(...)

```typescript
unsubscribeFromTopic(options: { topic: string; }) => Promise<void>
```

Unsubscribes this app installation from an FCM topic.

**Options:**

| Param | Type |
| --- | --- |
| **`options`** | <code>{ topic: string; }</code> |

--------------------


### getDeliveredNotifications()

```typescript
getDeliveredNotifications() => Promise<DeliveredNotifications>
```

Retrieves notifications currently displayed in the system notification center.

**Returns:** <code>Promise&lt;<a href="#deliverednotifications">DeliveredNotifications</a>&gt;</code>

--------------------


### removeDeliveredNotifications(...)

```typescript
removeDeliveredNotifications(delivered: DeliveredNotifications) => Promise<void>
```

Removes the specified notifications from the system notification center. Pass the result of `getDeliveredNotifications()` or an object containing a subset of its notifications.

**Param:** <code>delivered: <a href="#deliverednotifications">DeliveredNotifications</a></code>

--------------------


### removeAllDeliveredNotifications()

```typescript
removeAllDeliveredNotifications() => Promise<void>
```

Removes all notifications currently displayed in the system notification center.

--------------------


### addListener('registration', ...)

```typescript
addListener(eventName: 'registration', listenerFunc: (token: Token) => void): Promise<PluginListenerHandle>
```

Registers a listener for the FCM registration token event.

| Param | Type |
| --- | --- |
| **`eventName`** | <code>'registration'</code> |
| **`listenerFunc`** | <code>(token: <a href="#token">Token</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('registrationError', ...)

```typescript
addListener(eventName: 'registrationError', listenerFunc: (error: RegistrationError) => void): Promise<PluginListenerHandle>
```

Registers a listener for FCM registration errors.

| Param | Type |
| --- | --- |
| **`eventName`** | <code>'registrationError'</code> |
| **`listenerFunc`** | <code>(error: <a href="#registrationerror">RegistrationError</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('pushNotificationReceived', ...)

```typescript
addListener(eventName: 'pushNotificationReceived', listenerFunc: (notification: PushNotification) => void): Promise<PluginListenerHandle>
```

Registers a listener for notifications received while the app is active.

| Param | Type |
| --- | --- |
| **`eventName`** | <code>'pushNotificationReceived'</code> |
| **`listenerFunc`** | <code>(notification: <a href="#pushnotification">PushNotification</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('pushNotificationActionPerformed', ...)

```typescript
addListener(eventName: 'pushNotificationActionPerformed', listenerFunc: (action: ActionPerformed) => void): Promise<PluginListenerHandle>
```

Registers a listener for notification interactions, including taps.

| Param | Type |
| --- | --- |
| **`eventName`** | <code>'pushNotificationActionPerformed'</code> |
| **`listenerFunc`** | <code>(action: <a href="#actionperformed">ActionPerformed</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Removes all listeners registered for this plugin.

--------------------

### Interfaces

#### PermissionStatus

| Prop | Type | Description |
| --- | --- | --- |
| **`receive`** | <code><a href="#permissionstate">PermissionState</a></code> | Current permission state for receiving push notifications. |

#### Token

| Prop | Type | Description |
| --- | --- | --- |
| **`value`** | <code>string</code> | Firebase Cloud Messaging registration token. |

#### PushNotification

| Prop | Type | Description |
| --- | --- | --- |
| **`id`** | <code>string</code> | Optional platform notification identifier. |
| **`title`** | <code>string</code> | Optional notification title. |
| **`body`** | <code>string</code> | Optional notification body. |
| **`data`** | <code>Record&lt;string, any&gt;</code> | Notification data payload. |

#### DeliveredNotification

| Prop | Type | Description |
| --- | --- | --- |
| **`id`** | <code>string</code> | Platform notification identifier. |
| **`tag`** | <code>string</code> | Optional Android notification tag. |
| **`title`** | <code>string</code> | Optional notification title. |
| **`body`** | <code>string</code> | Optional notification body. |
| **`data`** | <code>Record&lt;string, any&gt;</code> | Notification data payload. |

#### DeliveredNotifications

| Prop | Type | Description |
| --- | --- | --- |
| **`notifications`** | <code><a href="#deliverednotification">DeliveredNotification</a>[]</code> | Notifications currently displayed in the system notification center. |

#### ActionPerformed

| Prop | Type | Description |
| --- | --- | --- |
| **`actionId`** | <code>string</code> | Identifier for the action performed on the notification. A standard notification tap is reported as `tap`. |
| **`notification`** | <code><a href="#pushnotification">PushNotification</a></code> | The notification that was interacted with. |

#### RegistrationError

| Prop | Type | Description |
| --- | --- | --- |
| **`error`** | <code>string</code> | Registration error details. |

#### PluginListenerHandle

| Prop | Type | Description |
| --- | --- | --- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> | Removes this listener. |


### Type Aliases

#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>

</docgen-api>
