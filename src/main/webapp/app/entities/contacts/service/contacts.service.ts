import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IContacts, getContactsIdentifier } from '../contacts.model';

export type EntityResponseType = HttpResponse<IContacts>;
export type EntityArrayResponseType = HttpResponse<IContacts[]>;

@Injectable({ providedIn: 'root' })
export class ContactsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/contacts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(contacts: IContacts): Observable<EntityResponseType> {
    return this.http.post<IContacts>(this.resourceUrl, contacts, { observe: 'response' });
  }

  update(contacts: IContacts): Observable<EntityResponseType> {
    return this.http.put<IContacts>(`${this.resourceUrl}/${getContactsIdentifier(contacts) as string}`, contacts, { observe: 'response' });
  }

  partialUpdate(contacts: IContacts): Observable<EntityResponseType> {
    return this.http.patch<IContacts>(`${this.resourceUrl}/${getContactsIdentifier(contacts) as string}`, contacts, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IContacts>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IContacts[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addContactsToCollectionIfMissing(contactsCollection: IContacts[], ...contactsToCheck: (IContacts | null | undefined)[]): IContacts[] {
    const contacts: IContacts[] = contactsToCheck.filter(isPresent);
    if (contacts.length > 0) {
      const contactsCollectionIdentifiers = contactsCollection.map(contactsItem => getContactsIdentifier(contactsItem)!);
      const contactsToAdd = contacts.filter(contactsItem => {
        const contactsIdentifier = getContactsIdentifier(contactsItem);
        if (contactsIdentifier == null || contactsCollectionIdentifiers.includes(contactsIdentifier)) {
          return false;
        }
        contactsCollectionIdentifiers.push(contactsIdentifier);
        return true;
      });
      return [...contactsToAdd, ...contactsCollection];
    }
    return contactsCollection;
  }
}
