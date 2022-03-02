import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IContacts } from '../contacts.model';
import { ContactsService } from '../service/contacts.service';

@Component({
  templateUrl: './contacts-delete-dialog.component.html',
})
export class ContactsDeleteDialogComponent {
  contacts?: IContacts;

  constructor(protected contactsService: ContactsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.contactsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
