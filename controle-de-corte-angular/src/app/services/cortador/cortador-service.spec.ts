import { TestBed } from '@angular/core/testing';

import { CortadorService } from './cortador-service';

describe('CortadorService', () => {
  let service: CortadorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CortadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
