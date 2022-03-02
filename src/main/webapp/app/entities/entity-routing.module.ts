import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'contact',
        data: { pageTitle: 'jhipsterSampleApplicationApp.contact.home.title' },
        loadChildren: () => import('./contact/contact.module').then(m => m.ContactModule),
      },
      {
        path: 'contacts',
        data: { pageTitle: 'jhipsterSampleApplicationApp.contacts.home.title' },
        loadChildren: () => import('./contacts/contacts.module').then(m => m.ContactsModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
