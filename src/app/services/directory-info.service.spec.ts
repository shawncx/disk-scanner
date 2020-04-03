import { TestBed } from '@angular/core/testing';

import { DirectoryInfoService } from './directory-info.service';

describe('DirectoryInfoService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DirectoryInfoService = TestBed.get(DirectoryInfoService);
    expect(service).toBeTruthy();
  });
});
