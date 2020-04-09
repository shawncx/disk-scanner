import { Injectable } from '@angular/core';
import { IpcRenderer } from 'electron';

export class ElectronIpcChannel {
  public static readonly OpenDirecotryDialog = 'OpenDirecotryDialog';
  public static readonly CloseDirecotryDialog = 'CloseDirecotryDialog';
}

@Injectable({
  providedIn: 'root',
})
export class ElectronIpcService {
  private _ipc: IpcRenderer | undefined = void 0;

  constructor() {
    if ((<any>window).require) {
      try {
        this._ipc = (<any>window).require('electron').ipcRenderer;
      } catch (e) {
        throw e;
      }
    } else {
      // ignore
    }
  }

  public on(channel: string, listener: any): void {
    if (!this._ipc) {
      return;
    }
    this._ipc.on(channel, listener);
  }

  public send(channel: string, ...args): void {
    if (!this._ipc) {
      return;
    }
    this._ipc.send(channel, ...args);
  }
}
