#import "GmapsPlugin.h"
#import "GmapsRequestBuilder.h"
#import <Cordova/CDVPlugin.h>
#import <GoogleMaps/GoogleMaps.h>

@implementation GmapsPlugin {
    GMSPlacesClient *_placesClient;
    NSString *callbackId;
}

- (void) pluginInitialize {
    _placesClient = [[GMSPlacesClient alloc] init];
}

- (void)autocomplete:(CDVInvokedUrlCommand*)command
{
    callbackId = command.callbackId;

    __block CDVPluginResult* pluginResult = nil;
    NSString* query = [command.arguments objectAtIndex:0];

    if (query == nil || [query length] == 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT messageAsString:query];
    [pluginResult setKeepCallbackAsBool:YES];

    [_placesClient autocompleteQuery:query
                              bounds: nil
                              filter: nil
                            callback: ^(NSArray *results, NSError *error)
     {
         if (error != nil) {
             NSLog(@"Autocomplete error %@", [error localizedDescription]);
             return;
         }

         NSMutableArray *places = [[NSMutableArray alloc] init];
         for (GMSAutocompletePrediction* result in results) {
             NSDictionary *place = @{
                                     @"placeId": result.placeID,
                                     @"fullText": result.attributedFullText.string,
                                     @"primaryText": result.attributedPrimaryText.string,
                                     @"secondaryText": result.attributedSecondaryText.string
                                     };

             [places addObject:place];
         }

         pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:places];
         [pluginResult setKeepCallbackAsBool:NO];
         [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
     }
     ];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)geocode:(CDVInvokedUrlCommand*)command {
    callbackId = command.callbackId;
    NSString *placeId = [command.arguments objectAtIndex:0];

    __block CDVPluginResult* pluginResult = nil;

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT messageAsString:placeId];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];

    [_placesClient lookUpPlaceID:placeId callback:^(GMSPlace *place, NSError *error) {
        if (error != nil) {
            NSLog(@"Place details error %@", [error localizedDescription]);
            return;
        }

        if (place == nil) {
            NSLog(@"No place details for %@", placeId);
            return;
        }

        NSDictionary *geocode = @{
                                  @"lat": @(place.coordinate.latitude),
                                  @"lng": @(place.coordinate.longitude)
                                  };

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:geocode];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
    }];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)directions:(CDVInvokedUrlCommand*)command
{
    NSArray *inputWaypoints = [command.arguments objectAtIndex:0];
    NSDictionary *routeParams = [command.arguments objectAtIndex:1];
    NSString *query = [[[GmapsRequestBuilder alloc] init] execute:inputWaypoints withParams:routeParams];

    NSString *url = [NSString stringWithFormat:
                     @"https://maps.googleapis.com/maps/api/directions/json?%@",
                     query];

    NSLog(@"URL for directions: %@", url);

    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setHTTPMethod: @"GET"];
    [request setURL:[NSURL URLWithString: url]];

    NSURLResponse *urlResponse = nil;
    NSError *error = nil;

    NSData *response = [NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    NSDictionary *results = [NSJSONSerialization JSONObjectWithData:response options:0 error:nil];

    NSDictionary *polylineOverview = [[[results objectForKey:@"routes"] objectAtIndex:0] objectForKey:@"overview_polyline"];
    NSString *points = [polylineOverview objectForKey:@"points"];
    GMSPath *path = [GMSPath pathFromEncodedPath:points];

    NSMutableArray *routeWaypoints = [[NSMutableArray alloc] init];
    for (int i=0; i<path.count; i++) {
        CLLocationCoordinate2D location = [path coordinateAtIndex: (NSUInteger)i];
        NSDictionary *waypoint = @{
                                   @"lat": @(location.latitude),
                                   @"lng": @(location.longitude)
                                   };

        [routeWaypoints addObject:waypoint];

    }

    NSNumber *distance = @(0);
    NSArray *legs = [[[results objectForKey:@"routes"] objectAtIndex:0] objectForKey:@"legs"];
    for (NSDictionary *leg in legs) {
        distance = @([[leg objectForKey:@"distance"] intValue] + [distance intValue]);
    }

    NSDictionary *result = @{
                             @"waypoints": routeWaypoints,
                             @"distance": distance
                             };

    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

@end
