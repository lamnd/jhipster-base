import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'api-key',
        data: { pageTitle: 'gatewayApp.apiKey.home.title' },
        loadChildren: () => import('./api-key/api-key.module').then(m => m.ApiKeyModule),
      },
      {
        path: 'employee',
        data: { pageTitle: 'gatewayApp.employeeEmployee.home.title' },
        loadChildren: () => import('./employee/employee/employee.module').then(m => m.EmployeeEmployeeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
