import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { ApiKeyComponent } from './list/api-key.component';
import { ApiKeyDetailComponent } from './detail/api-key-detail.component';
import { ApiKeyUpdateComponent } from './update/api-key-update.component';
import { ApiKeyDeleteDialogComponent } from './delete/api-key-delete-dialog.component';
import { ApiKeyRoutingModule } from './route/api-key-routing.module';

@NgModule({
  imports: [SharedModule, ApiKeyRoutingModule],
  declarations: [ApiKeyComponent, ApiKeyDetailComponent, ApiKeyUpdateComponent, ApiKeyDeleteDialogComponent],
  entryComponents: [ApiKeyDeleteDialogComponent],
})
export class ApiKeyModule {}
