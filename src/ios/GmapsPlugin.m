#import "GmapsPlugin.h"
#import "GmapsRequestBuilder.h"
#import "AddressParser.h"
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
    NSString *address = [command.arguments objectAtIndex:0];

    __block CDVPluginResult* pluginResult = nil;

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT messageAsString:nil];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];

    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    [geocoder geocodeAddressString:address completionHandler:^(NSArray<CLPlacemark *> * _Nullable placemarks, NSError * _Nullable error) {
        NSDictionary *jsonResult = [[NSDictionary alloc] init];

        if ([placemarks count] > 0) {

            CLPlacemark *mark = [placemarks objectAtIndex:0];
            jsonResult = [[[AddressParser alloc] init] parse:mark];
        }

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonResult];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
    }];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)reverseGeocode:(CDVInvokedUrlCommand*)command {
    callbackId = command.callbackId;
    NSDictionary *coords = [command.arguments objectAtIndex:0];

    __block CDVPluginResult* pluginResult = nil;

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT messageAsString:nil];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];

    CLLocation *location = [[CLLocation alloc] initWithLatitude:[[coords valueForKey:@"lat"] doubleValue] longitude:[[coords valueForKey:@"lng"] doubleValue]];

    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray<CLPlacemark *> * _Nullable placemarks, NSError * _Nullable error) {
        NSDictionary *jsonResult = [[NSDictionary alloc] init];

        if ([placemarks count] > 0) {

            CLPlacemark *mark = [placemarks objectAtIndex:0];
            jsonResult = [[[AddressParser alloc] init] parse:mark];
        }

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonResult];
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

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:results];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

@end
