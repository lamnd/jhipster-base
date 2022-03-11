import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';

@Component({
  templateUrl: './api-key-delete-dialog.component.html',
})
export class ApiKeyDeleteDialogComponent {
  apiKey?: IApiKey;

  constructor(protected apiKeyService: ApiKeyService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.apiKeyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
