#import "GmapsRequestBuilder.h"

@implementation GmapsRequestBuilder {

}

-(NSString*)execute:(NSArray*)waypoints withParams:(NSDictionary*)routeParams {
    return [NSString stringWithFormat:@"%@%@",
            [self getDirection:waypoints],
            [self getParams:routeParams]];
}

-(NSString*)getDirection:(NSArray*)waypoints {
    if ([[waypoints objectAtIndex:0] isKindOfClass:[NSString class]]) {

        NSString *query = [NSString stringWithFormat:@"origin=%@&destination=%@",
                           [waypoints objectAtIndex:0],
                           [waypoints objectAtIndex:(waypoints.count - 1)]];

        if (waypoints.count > 2) {
            query = [query stringByAppendingFormat:@"&waypoints=%@",
                 [[waypoints subarrayWithRange:NSMakeRange(1, waypoints.count - 2)] componentsJoinedByString:@"|"]];
        }

        return query;
    }

    [NSException raise:@"Invalid waypoints" format:@"waypoints are invalid"];
    return NULL;
}

-(NSString*)getParams:(NSDictionary*)routeParams {
    NSString *query = @"";

    if ([routeParams objectForKey:@"unit"]) {
        query = [query stringByAppendingFormat:@"&unit=%@", [routeParams objectForKey:@"unit"]];
    }

    if ([routeParams objectForKey:@"avoid"]) {
        query = [query stringByAppendingFormat:@"&avoid=%@", [routeParams objectForKey:@"avoid"]];
    }

    return query;
}

@end
