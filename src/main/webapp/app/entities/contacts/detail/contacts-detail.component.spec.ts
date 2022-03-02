import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ContactsDetailComponent } from './contacts-detail.component';

describe('Contacts Management Detail Component', () => {
  let comp: ContactsDetailComponent;
  let fixture: ComponentFixture<ContactsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContactsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ contacts: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(ContactsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ContactsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load contacts on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.contacts).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
