jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IApiKey, ApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';

import { ApiKeyRoutingResolveService } from './api-key-routing-resolve.service';

describe('Service Tests', () => {
  describe('ApiKey routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ApiKeyRoutingResolveService;
    let service: ApiKeyService;
    let resultApiKey: IApiKey | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ApiKeyRoutingResolveService);
      service = TestBed.inject(ApiKeyService);
      resultApiKey = undefined;
    });

    describe('resolve', () => {
      it('should return IApiKey returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultApiKey = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultApiKey).toEqual({ id: 123 });
      });

      it('should return new IApiKey if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultApiKey = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultApiKey).toEqual(new ApiKey());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultApiKey = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultApiKey).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
