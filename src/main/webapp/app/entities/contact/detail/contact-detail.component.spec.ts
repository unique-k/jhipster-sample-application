import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ContactDetailComponent } from './contact-detail.component';

describe('Contact Management Detail Component', () => {
  let comp: ContactDetailComponent;
  let fixture: ComponentFixture<ContactDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContactDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ contact: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(ContactDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ContactDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load contact on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.contact).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
