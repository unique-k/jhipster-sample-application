import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IContacts, Contacts } from '../contacts.model';
import { ContactsService } from '../service/contacts.service';

@Component({
  selector: 'jhi-contacts-update',
  templateUrl: './contacts-update.component.html',
})
export class ContactsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    firstname: [],
    lastname: [],
    email: [],
  });

  constructor(protected contactsService: ContactsService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ contacts }) => {
      this.updateForm(contacts);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const contacts = this.createFromForm();
    if (contacts.id !== undefined) {
      this.subscribeToSaveResponse(this.contactsService.update(contacts));
    } else {
      this.subscribeToSaveResponse(this.contactsService.create(contacts));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IContacts>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
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

  protected updateForm(contacts: IContacts): void {
    this.editForm.patchValue({
      id: contacts.id,
      firstname: contacts.firstname,
      lastname: contacts.lastname,
      email: contacts.email,
    });
  }

  protected createFromForm(): IContacts {
    return {
      ...new Contacts(),
      id: this.editForm.get(['id'])!.value,
      firstname: this.editForm.get(['firstname'])!.value,
      lastname: this.editForm.get(['lastname'])!.value,
      email: this.editForm.get(['email'])!.value,
    };
  }
}
