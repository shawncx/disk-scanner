import { TestBed } from '@angular/core/testing';

import { FileInfoService } from './file-info.service';

describe('FileInfoService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FileInfoService = TestBed.get(FileInfoService);
    expect(service).toBeTruthy();
  });
});
