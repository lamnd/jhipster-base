import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IApiKey, getApiKeyIdentifier } from '../api-key.model';

export type EntityResponseType = HttpResponse<IApiKey>;
export type EntityArrayResponseType = HttpResponse<IApiKey[]>;

@Injectable({ providedIn: 'root' })
export class ApiKeyService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/api-keys');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(apiKey: IApiKey): Observable<EntityResponseType> {
    return this.http.post<IApiKey>(this.resourceUrl, apiKey, { observe: 'response' });
  }

  update(apiKey: IApiKey): Observable<EntityResponseType> {
    return this.http.put<IApiKey>(`${this.resourceUrl}/${getApiKeyIdentifier(apiKey) as number}`, apiKey, { observe: 'response' });
  }

  partialUpdate(apiKey: IApiKey): Observable<EntityResponseType> {
    return this.http.patch<IApiKey>(`${this.resourceUrl}/${getApiKeyIdentifier(apiKey) as number}`, apiKey, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IApiKey>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IApiKey[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addApiKeyToCollectionIfMissing(apiKeyCollection: IApiKey[], ...apiKeysToCheck: (IApiKey | null | undefined)[]): IApiKey[] {
    const apiKeys: IApiKey[] = apiKeysToCheck.filter(isPresent);
    if (apiKeys.length > 0) {
      const apiKeyCollectionIdentifiers = apiKeyCollection.map(apiKeyItem => getApiKeyIdentifier(apiKeyItem)!);
      const apiKeysToAdd = apiKeys.filter(apiKeyItem => {
        const apiKeyIdentifier = getApiKeyIdentifier(apiKeyItem);
        if (apiKeyIdentifier == null || apiKeyCollectionIdentifiers.includes(apiKeyIdentifier)) {
          return false;
        }
        apiKeyCollectionIdentifiers.push(apiKeyIdentifier);
        return true;
      });
      return [...apiKeysToAdd, ...apiKeyCollection];
    }
    return apiKeyCollection;
  }
}
