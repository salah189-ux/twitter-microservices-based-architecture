import { IUser } from 'app/entities/user/user.model';

export interface IMessage {
  id?: number;
  messageContent?: string | null;
  user?: IUser | null;
}

export class Message implements IMessage {
  constructor(public id?: number, public messageContent?: string | null, public user?: IUser | null) {}
}

export function getMessageIdentifier(message: IMessage): number | undefined {
  return message.id;
}
