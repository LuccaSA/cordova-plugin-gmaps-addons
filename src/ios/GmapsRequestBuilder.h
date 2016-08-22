@interface GmapsRequestBuilder : NSObject

-(NSString*)execute:(NSArray*)waypoints withParams:(NSDictionary*)routeParams;

@end