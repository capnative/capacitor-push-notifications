import { WebPlugin } from '@capacitor/core';

import type { PermissionStatus, PushNotificationsPlugin, Token } from './definitions';

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
}
