import { WebPlugin } from '@capacitor/core';

import type { DeliveredNotifications, PermissionStatus, PushNotificationsPlugin, Token } from './definitions';

export class PushNotificationsWeb extends WebPlugin implements PushNotificationsPlugin {
  async checkPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async requestPermissions(): Promise<PermissionStatus> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async register(): Promise<void> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async unregister(): Promise<void> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async getToken(): Promise<Token> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async subscribeToTopic(_options: { topic: string }): Promise<void> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async unsubscribeFromTopic(_options: { topic: string }): Promise<void> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async getDeliveredNotifications(): Promise<DeliveredNotifications> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async removeDeliveredNotifications(_delivered: DeliveredNotifications): Promise<void> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
  async removeAllDeliveredNotifications(): Promise<void> {
    throw this.unimplemented('Push notifications are not implemented on web.');
  }
}
