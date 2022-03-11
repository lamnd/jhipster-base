export interface IApiKey {
  id?: number;
  description?: string | null;
  clientId?: string;
}

export class ApiKey implements IApiKey {
  constructor(public id?: number, public description?: string | null, public clientId?: string) {}
}

export function getApiKeyIdentifier(apiKey: IApiKey): number | undefined {
  return apiKey.id;
}
