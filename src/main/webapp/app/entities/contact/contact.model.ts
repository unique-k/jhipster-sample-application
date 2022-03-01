export interface IContact {
  id?: string;
  firstname?: string | null;
  lastname?: string | null;
  email?: string | null;
}

export class Contact implements IContact {
  constructor(public id?: string, public firstname?: string | null, public lastname?: string | null, public email?: string | null) {}
}

export function getContactIdentifier(contact: IContact): string | undefined {
  return contact.id;
}
