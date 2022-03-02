import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IContacts, Contacts } from '../contacts.model';

import { ContactsService } from './contacts.service';

describe('Contacts Service', () => {
  let service: ContactsService;
  let httpMock: HttpTestingController;
  let elemDefault: IContacts;
  let expectedResult: IContacts | IContacts[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ContactsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      firstname: 'AAAAAAA',
      lastname: 'AAAAAAA',
      email: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Contacts', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Contacts()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Contacts', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          firstname: 'BBBBBB',
          lastname: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Contacts', () => {
      const patchObject = Object.assign(
        {
          firstname: 'BBBBBB',
          email: 'BBBBBB',
        },
        new Contacts()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Contacts', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          firstname: 'BBBBBB',
          lastname: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Contacts', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addContactsToCollectionIfMissing', () => {
      it('should add a Contacts to an empty array', () => {
        const contacts: IContacts = { id: 'ABC' };
        expectedResult = service.addContactsToCollectionIfMissing([], contacts);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(contacts);
      });

      it('should not add a Contacts to an array that contains it', () => {
        const contacts: IContacts = { id: 'ABC' };
        const contactsCollection: IContacts[] = [
          {
            ...contacts,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addContactsToCollectionIfMissing(contactsCollection, contacts);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Contacts to an array that doesn't contain it", () => {
        const contacts: IContacts = { id: 'ABC' };
        const contactsCollection: IContacts[] = [{ id: 'CBA' }];
        expectedResult = service.addContactsToCollectionIfMissing(contactsCollection, contacts);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(contacts);
      });

      it('should add only unique Contacts to an array', () => {
        const contactsArray: IContacts[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'a0f98981-0639-4a91-93a4-1e97317379f4' }];
        const contactsCollection: IContacts[] = [{ id: 'ABC' }];
        expectedResult = service.addContactsToCollectionIfMissing(contactsCollection, ...contactsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const contacts: IContacts = { id: 'ABC' };
        const contacts2: IContacts = { id: 'CBA' };
        expectedResult = service.addContactsToCollectionIfMissing([], contacts, contacts2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(contacts);
        expect(expectedResult).toContain(contacts2);
      });

      it('should accept null and undefined values', () => {
        const contacts: IContacts = { id: 'ABC' };
        expectedResult = service.addContactsToCollectionIfMissing([], null, contacts, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(contacts);
      });

      it('should return initial array if no Contacts is added', () => {
        const contactsCollection: IContacts[] = [{ id: 'ABC' }];
        expectedResult = service.addContactsToCollectionIfMissing(contactsCollection, undefined, null);
        expect(expectedResult).toEqual(contactsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
