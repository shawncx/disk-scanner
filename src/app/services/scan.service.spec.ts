import { TestBed } from '@angular/core/testing';

import { ScanService } from './scan.service';

describe('ScanService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ScanService = TestBed.get(ScanService);
    expect(service).toBeTruthy();
  });
});
