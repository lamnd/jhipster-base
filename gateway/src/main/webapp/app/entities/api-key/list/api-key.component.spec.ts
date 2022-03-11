import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ApiKeyService } from '../service/api-key.service';

import { ApiKeyComponent } from './api-key.component';

describe('Component Tests', () => {
  describe('ApiKey Management Component', () => {
    let comp: ApiKeyComponent;
    let fixture: ComponentFixture<ApiKeyComponent>;
    let service: ApiKeyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApiKeyComponent],
      })
        .overrideTemplate(ApiKeyComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApiKeyComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ApiKeyService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.apiKeys?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
