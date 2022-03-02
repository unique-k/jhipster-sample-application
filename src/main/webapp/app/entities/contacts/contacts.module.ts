import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ContactsComponent } from './list/contacts.component';
import { ContactsDetailComponent } from './detail/contacts-detail.component';
import { ContactsUpdateComponent } from './update/contacts-update.component';
import { ContactsDeleteDialogComponent } from './delete/contacts-delete-dialog.component';
import { ContactsRoutingModule } from './route/contacts-routing.module';

@NgModule({
  imports: [SharedModule, ContactsRoutingModule],
  declarations: [ContactsComponent, ContactsDetailComponent, ContactsUpdateComponent, ContactsDeleteDialogComponent],
  entryComponents: [ContactsDeleteDialogComponent],
})
export class ContactsModule {}
