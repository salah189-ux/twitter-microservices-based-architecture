import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFollow, getFollowIdentifier } from '../follow.model';

export type EntityResponseType = HttpResponse<IFollow>;
export type EntityArrayResponseType = HttpResponse<IFollow[]>;

@Injectable({ providedIn: 'root' })
export class FollowService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/follows', 'socialgraph');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(follow: IFollow): Observable<EntityResponseType> {
    return this.http.post<IFollow>(this.resourceUrl, follow, { observe: 'response' });
  }

  update(follow: IFollow): Observable<EntityResponseType> {
    return this.http.put<IFollow>(`${this.resourceUrl}/${getFollowIdentifier(follow) as number}`, follow, { observe: 'response' });
  }

  partialUpdate(follow: IFollow): Observable<EntityResponseType> {
    return this.http.patch<IFollow>(`${this.resourceUrl}/${getFollowIdentifier(follow) as number}`, follow, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFollow>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFollow[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFollowToCollectionIfMissing(followCollection: IFollow[], ...followsToCheck: (IFollow | null | undefined)[]): IFollow[] {
    const follows: IFollow[] = followsToCheck.filter(isPresent);
    if (follows.length > 0) {
      const followCollectionIdentifiers = followCollection.map(followItem => getFollowIdentifier(followItem)!);
      const followsToAdd = follows.filter(followItem => {
        const followIdentifier = getFollowIdentifier(followItem);
        if (followIdentifier == null || followCollectionIdentifiers.includes(followIdentifier)) {
          return false;
        }
        followCollectionIdentifiers.push(followIdentifier);
        return true;
      });
      return [...followsToAdd, ...followCollection];
    }
    return followCollection;
  }
}
