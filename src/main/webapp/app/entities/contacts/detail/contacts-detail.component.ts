import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IContacts } from '../contacts.model';

@Component({
  selector: 'jhi-contacts-detail',
  templateUrl: './contacts-detail.component.html',
})
export class ContactsDetailComponent implements OnInit {
  contacts: IContacts | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ contacts }) => {
      this.contacts = contacts;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
