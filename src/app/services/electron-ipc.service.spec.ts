import { TestBed } from '@angular/core/testing';

import { ElectronIpcService } from './electron-ipc.service';

describe('ElectronIpcService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ElectronIpcService = TestBed.get(ElectronIpcService);
    expect(service).toBeTruthy();
  });
});
