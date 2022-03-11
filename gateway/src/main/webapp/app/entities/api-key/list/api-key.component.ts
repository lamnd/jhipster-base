import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';
import { ApiKeyDeleteDialogComponent } from '../delete/api-key-delete-dialog.component';

@Component({
  selector: 'jhi-api-key',
  templateUrl: './api-key.component.html',
})
export class ApiKeyComponent implements OnInit {
  apiKeys?: IApiKey[];
  isLoading = false;

  constructor(protected apiKeyService: ApiKeyService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.apiKeyService.query().subscribe(
      (res: HttpResponse<IApiKey[]>) => {
        this.isLoading = false;
        this.apiKeys = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IApiKey): number {
    return item.id!;
  }

  delete(apiKey: IApiKey): void {
    const modalRef = this.modalService.open(ApiKeyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.apiKey = apiKey;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
