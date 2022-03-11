jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ApiKeyService } from '../service/api-key.service';
import { IApiKey, ApiKey } from '../api-key.model';

import { ApiKeyUpdateComponent } from './api-key-update.component';

describe('Component Tests', () => {
  describe('ApiKey Management Update Component', () => {
    let comp: ApiKeyUpdateComponent;
    let fixture: ComponentFixture<ApiKeyUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let apiKeyService: ApiKeyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApiKeyUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ApiKeyUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApiKeyUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      apiKeyService = TestBed.inject(ApiKeyService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const apiKey: IApiKey = { id: 456 };

        activatedRoute.data = of({ apiKey });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(apiKey));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const apiKey = { id: 123 };
        spyOn(apiKeyService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ apiKey });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: apiKey }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(apiKeyService.update).toHaveBeenCalledWith(apiKey);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const apiKey = new ApiKey();
        spyOn(apiKeyService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ apiKey });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: apiKey }));
        saveSubject.complete();

        // THEN
        expect(apiKeyService.create).toHaveBeenCalledWith(apiKey);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const apiKey = { id: 123 };
        spyOn(apiKeyService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ apiKey });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(apiKeyService.update).toHaveBeenCalledWith(apiKey);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
