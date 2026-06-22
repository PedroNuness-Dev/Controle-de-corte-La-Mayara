import { TestBed } from '@angular/core/testing';

import { EnfestadorService } from './enfestador-service';

describe('EnfestadorService', () => {
  let service: EnfestadorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnfestadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
