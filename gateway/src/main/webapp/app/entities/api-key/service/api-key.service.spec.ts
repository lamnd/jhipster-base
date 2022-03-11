import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IApiKey, ApiKey } from '../api-key.model';

import { ApiKeyService } from './api-key.service';

describe('Service Tests', () => {
  describe('ApiKey Service', () => {
    let service: ApiKeyService;
    let httpMock: HttpTestingController;
    let elemDefault: IApiKey;
    let expectedResult: IApiKey | IApiKey[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ApiKeyService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        description: 'AAAAAAA',
        clientId: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a ApiKey', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new ApiKey()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a ApiKey', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            description: 'BBBBBB',
            clientId: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a ApiKey', () => {
        const patchObject = Object.assign({}, new ApiKey());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of ApiKey', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            description: 'BBBBBB',
            clientId: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a ApiKey', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addApiKeyToCollectionIfMissing', () => {
        it('should add a ApiKey to an empty array', () => {
          const apiKey: IApiKey = { id: 123 };
          expectedResult = service.addApiKeyToCollectionIfMissing([], apiKey);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(apiKey);
        });

        it('should not add a ApiKey to an array that contains it', () => {
          const apiKey: IApiKey = { id: 123 };
          const apiKeyCollection: IApiKey[] = [
            {
              ...apiKey,
            },
            { id: 456 },
          ];
          expectedResult = service.addApiKeyToCollectionIfMissing(apiKeyCollection, apiKey);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a ApiKey to an array that doesn't contain it", () => {
          const apiKey: IApiKey = { id: 123 };
          const apiKeyCollection: IApiKey[] = [{ id: 456 }];
          expectedResult = service.addApiKeyToCollectionIfMissing(apiKeyCollection, apiKey);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(apiKey);
        });

        it('should add only unique ApiKey to an array', () => {
          const apiKeyArray: IApiKey[] = [{ id: 123 }, { id: 456 }, { id: 70066 }];
          const apiKeyCollection: IApiKey[] = [{ id: 123 }];
          expectedResult = service.addApiKeyToCollectionIfMissing(apiKeyCollection, ...apiKeyArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const apiKey: IApiKey = { id: 123 };
          const apiKey2: IApiKey = { id: 456 };
          expectedResult = service.addApiKeyToCollectionIfMissing([], apiKey, apiKey2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(apiKey);
          expect(expectedResult).toContain(apiKey2);
        });

        it('should accept null and undefined values', () => {
          const apiKey: IApiKey = { id: 123 };
          expectedResult = service.addApiKeyToCollectionIfMissing([], null, apiKey, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(apiKey);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
