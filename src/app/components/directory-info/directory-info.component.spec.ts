import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DirectoryInfoComponent } from './directory-info.component';

describe('DirectoryInfoComponent', () => {
  let component: DirectoryInfoComponent;
  let fixture: ComponentFixture<DirectoryInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DirectoryInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DirectoryInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
