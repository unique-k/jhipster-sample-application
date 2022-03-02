import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ContactsComponent } from '../list/contacts.component';
import { ContactsDetailComponent } from '../detail/contacts-detail.component';
import { ContactsUpdateComponent } from '../update/contacts-update.component';
import { ContactsRoutingResolveService } from './contacts-routing-resolve.service';

const contactsRoute: Routes = [
  {
    path: '',
    component: ContactsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ContactsDetailComponent,
    resolve: {
      contacts: ContactsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ContactsUpdateComponent,
    resolve: {
      contacts: ContactsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ContactsUpdateComponent,
    resolve: {
      contacts: ContactsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(contactsRoute)],
  exports: [RouterModule],
})
export class ContactsRoutingModule {}
