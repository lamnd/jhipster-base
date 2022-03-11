import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IApiKey, ApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';

@Component({
  selector: 'jhi-api-key-update',
  templateUrl: './api-key-update.component.html',
})
export class ApiKeyUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    description: [],
    clientId: [null, [Validators.required]],
  });

  constructor(protected apiKeyService: ApiKeyService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ apiKey }) => {
      this.updateForm(apiKey);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const apiKey = this.createFromForm();
    if (apiKey.id !== undefined) {
      this.subscribeToSaveResponse(this.apiKeyService.update(apiKey));
    } else {
      this.subscribeToSaveResponse(this.apiKeyService.create(apiKey));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApiKey>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(apiKey: IApiKey): void {
    this.editForm.patchValue({
      id: apiKey.id,
      description: apiKey.description,
      clientId: apiKey.clientId,
    });
  }

  protected createFromForm(): IApiKey {
    return {
      ...new ApiKey(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      clientId: this.editForm.get(['clientId'])!.value,
    };
  }
}
