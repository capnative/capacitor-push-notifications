import type { PluginListenerHandle } from '@capacitor/core';

export type PermissionState = 'prompt' | 'prompt-with-rationale' | 'granted' | 'denied';

export interface PermissionStatus {
  receive: PermissionState;
}

export interface Token {
  /** Firebase Cloud Messaging registration token. */
  value: string;
}

export interface PushNotification {
  id?: string;
  title?: string;
  body?: string;
  data: Record<string, any>;
}

export interface ActionPerformed {
  actionId: string;
  notification: PushNotification;
}

export interface RegistrationError {
  error: string;
}

export interface PushNotificationsPlugin {
  /** Check the push notification permission state. */
  checkPermissions(): Promise<PermissionStatus>;
  /** Request the push notification permission. */
  requestPermissions(): Promise<PermissionStatus>;
  /** Register with Firebase Cloud Messaging; emits `registration` or `registrationError`. */
  register(): Promise<void>;
  /** Unregister from push notifications and delete the FCM token. */
  unregister(): Promise<void>;
  /** Get the current FCM token. */
  getToken(): Promise<Token>;
  /** Subscribe to an FCM topic. */
  subscribeToTopic(options: { topic: string }): Promise<void>;
  /** Unsubscribe from an FCM topic. */
  unsubscribeFromTopic(options: { topic: string }): Promise<void>;

  addListener(eventName: 'registration', listenerFunc: (token: Token) => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'registrationError', listenerFunc: (error: RegistrationError) => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'pushNotificationReceived', listenerFunc: (notification: PushNotification) => void): Promise<PluginListenerHandle>;
  addListener(eventName: 'pushNotificationActionPerformed', listenerFunc: (action: ActionPerformed) => void): Promise<PluginListenerHandle>;
  removeAllListeners(): Promise<void>;
}
