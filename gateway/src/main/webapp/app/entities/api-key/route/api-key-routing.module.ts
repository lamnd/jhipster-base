import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ApiKeyComponent } from '../list/api-key.component';
import { ApiKeyDetailComponent } from '../detail/api-key-detail.component';
import { ApiKeyUpdateComponent } from '../update/api-key-update.component';
import { ApiKeyRoutingResolveService } from './api-key-routing-resolve.service';

const apiKeyRoute: Routes = [
  {
    path: '',
    component: ApiKeyComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ApiKeyDetailComponent,
    resolve: {
      apiKey: ApiKeyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ApiKeyUpdateComponent,
    resolve: {
      apiKey: ApiKeyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ApiKeyUpdateComponent,
    resolve: {
      apiKey: ApiKeyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(apiKeyRoute)],
  exports: [RouterModule],
})
export class ApiKeyRoutingModule {}
