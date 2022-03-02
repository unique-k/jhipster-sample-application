export interface IContacts {
  id?: string;
  firstname?: string | null;
  lastname?: string | null;
  email?: string | null;
}

export class Contacts implements IContacts {
  constructor(public id?: string, public firstname?: string | null, public lastname?: string | null, public email?: string | null) {}
}

export function getContactsIdentifier(contacts: IContacts): string | undefined {
  return contacts.id;
}
