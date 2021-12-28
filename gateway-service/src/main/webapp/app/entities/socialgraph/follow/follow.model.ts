import { Following } from 'app/entities/enumerations/following.model';

export interface IFollow {
  id?: number;
  follow?: Following | null;
}

export class Follow implements IFollow {
  constructor(public id?: number, public follow?: Following | null) {}
}

export function getFollowIdentifier(follow: IFollow): number | undefined {
  return follow.id;
}
