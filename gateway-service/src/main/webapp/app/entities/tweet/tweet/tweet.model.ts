export interface ITweet {
  id?: number;
  content?: string;
}

export class Tweet implements ITweet {
  constructor(public id?: number, public content?: string) {}
}

export function getTweetIdentifier(tweet: ITweet): number | undefined {
  return tweet.id;
}
