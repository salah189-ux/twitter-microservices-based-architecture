import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITweet, getTweetIdentifier } from '../tweet.model';

export type EntityResponseType = HttpResponse<ITweet>;
export type EntityArrayResponseType = HttpResponse<ITweet[]>;

@Injectable({ providedIn: 'root' })
export class TweetService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tweets', 'tweet');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tweet: ITweet): Observable<EntityResponseType> {
    return this.http.post<ITweet>(this.resourceUrl, tweet, { observe: 'response' });
  }

  update(tweet: ITweet): Observable<EntityResponseType> {
    return this.http.put<ITweet>(`${this.resourceUrl}/${getTweetIdentifier(tweet) as number}`, tweet, { observe: 'response' });
  }

  partialUpdate(tweet: ITweet): Observable<EntityResponseType> {
    return this.http.patch<ITweet>(`${this.resourceUrl}/${getTweetIdentifier(tweet) as number}`, tweet, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITweet>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITweet[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTweetToCollectionIfMissing(tweetCollection: ITweet[], ...tweetsToCheck: (ITweet | null | undefined)[]): ITweet[] {
    const tweets: ITweet[] = tweetsToCheck.filter(isPresent);
    if (tweets.length > 0) {
      const tweetCollectionIdentifiers = tweetCollection.map(tweetItem => getTweetIdentifier(tweetItem)!);
      const tweetsToAdd = tweets.filter(tweetItem => {
        const tweetIdentifier = getTweetIdentifier(tweetItem);
        if (tweetIdentifier == null || tweetCollectionIdentifiers.includes(tweetIdentifier)) {
          return false;
        }
        tweetCollectionIdentifiers.push(tweetIdentifier);
        return true;
      });
      return [...tweetsToAdd, ...tweetCollection];
    }
    return tweetCollection;
  }
}
