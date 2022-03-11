import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IApiKey, ApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';

@Injectable({ providedIn: 'root' })
export class ApiKeyRoutingResolveService implements Resolve<IApiKey> {
  constructor(protected service: ApiKeyService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IApiKey> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((apiKey: HttpResponse<ApiKey>) => {
          if (apiKey.body) {
            return of(apiKey.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ApiKey());
  }
}
