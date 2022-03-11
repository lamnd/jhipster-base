import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IApiKey } from '../api-key.model';

@Component({
  selector: 'jhi-api-key-detail',
  templateUrl: './api-key-detail.component.html',
})
export class ApiKeyDetailComponent implements OnInit {
  apiKey: IApiKey | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ apiKey }) => {
      this.apiKey = apiKey;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
