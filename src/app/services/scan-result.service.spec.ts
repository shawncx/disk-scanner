import { TestBed } from '@angular/core/testing';

import { ScanResultService } from './scan-result.service';

describe('ScanResultService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ScanResultService = TestBed.get(ScanResultService);
    expect(service).toBeTruthy();
  });
});
