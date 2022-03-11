import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ApiKeyDetailComponent } from './api-key-detail.component';

describe('Component Tests', () => {
  describe('ApiKey Management Detail Component', () => {
    let comp: ApiKeyDetailComponent;
    let fixture: ComponentFixture<ApiKeyDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ApiKeyDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ apiKey: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ApiKeyDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ApiKeyDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load apiKey on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.apiKey).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
