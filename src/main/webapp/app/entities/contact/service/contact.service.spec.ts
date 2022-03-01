import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IContact, Contact } from '../contact.model';

import { ContactService } from './contact.service';

describe('Contact Service', () => {
  let service: ContactService;
  let httpMock: HttpTestingController;
  let elemDefault: IContact;
  let expectedResult: IContact | IContact[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ContactService);
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

    it('should create a Contact', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Contact()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Contact', () => {
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

    it('should partial update a Contact', () => {
      const patchObject = Object.assign(
        {
          firstname: 'BBBBBB',
          email: 'BBBBBB',
        },
        new Contact()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Contact', () => {
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

    it('should delete a Contact', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addContactToCollectionIfMissing', () => {
      it('should add a Contact to an empty array', () => {
        const contact: IContact = { id: 'ABC' };
        expectedResult = service.addContactToCollectionIfMissing([], contact);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(contact);
      });

      it('should not add a Contact to an array that contains it', () => {
        const contact: IContact = { id: 'ABC' };
        const contactCollection: IContact[] = [
          {
            ...contact,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addContactToCollectionIfMissing(contactCollection, contact);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Contact to an array that doesn't contain it", () => {
        const contact: IContact = { id: 'ABC' };
        const contactCollection: IContact[] = [{ id: 'CBA' }];
        expectedResult = service.addContactToCollectionIfMissing(contactCollection, contact);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(contact);
      });

      it('should add only unique Contact to an array', () => {
        const contactArray: IContact[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '061fc516-52d5-424b-a871-5e7b80230af0' }];
        const contactCollection: IContact[] = [{ id: 'ABC' }];
        expectedResult = service.addContactToCollectionIfMissing(contactCollection, ...contactArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const contact: IContact = { id: 'ABC' };
        const contact2: IContact = { id: 'CBA' };
        expectedResult = service.addContactToCollectionIfMissing([], contact, contact2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(contact);
        expect(expectedResult).toContain(contact2);
      });

      it('should accept null and undefined values', () => {
        const contact: IContact = { id: 'ABC' };
        expectedResult = service.addContactToCollectionIfMissing([], null, contact, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(contact);
      });

      it('should return initial array if no Contact is added', () => {
        const contactCollection: IContact[] = [{ id: 'ABC' }];
        expectedResult = service.addContactToCollectionIfMissing(contactCollection, undefined, null);
        expect(expectedResult).toEqual(contactCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
