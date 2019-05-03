#import "GmapsPlugin.h"
#import "AddressParser.h"
#import <Cordova/CDVPlugin.h>
#import <GoogleMaps/GoogleMaps.h>

@interface GmapsPlugin ()
- (void) _sendCallbackErrorResult: (NSString*) errorMessage withCallbackId: (NSString*) id;
@end

@implementation GmapsPlugin

- (void) pluginInitialize {

}

- (void) _sendCallbackErrorResult:(NSString*) errorMessage withCallbackId: (NSString*) callbackId {
    CDVPluginResult* pluginResult = nil;

    NSLog(@"Error %@", errorMessage);

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)geocode:(CDVInvokedUrlCommand*)command {

    NSString *address = [command.arguments objectAtIndex:0];

    __block CDVPluginResult* pluginResult = nil;

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT messageAsString:nil];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];

    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    [geocoder geocodeAddressString:address completionHandler:^(NSArray<CLPlacemark *> * _Nullable placemarks, NSError * _Nullable error) {
        if (error != nil) {
            [self _sendCallbackErrorResult:[error localizedDescription] withCallbackId:command.callbackId];
            return;
        }

        NSMutableArray *jsonResult = [[NSMutableArray alloc] init];

        for (CLPlacemark * placemark in placemarks) {
            if (placemark.locality != nil && placemark.name != nil) {
                [jsonResult addObject:[[[AddressParser alloc] init] parse:placemark]];
            }
        }

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:jsonResult];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)reverseGeocode:(CDVInvokedUrlCommand*)command {

    NSDictionary *coords = [command.arguments objectAtIndex:0];

    __block CDVPluginResult* pluginResult = nil;

    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT messageAsString:nil];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];

    CLLocation *location = [[CLLocation alloc] initWithLatitude:[[coords valueForKey:@"lat"] doubleValue] longitude:[[coords valueForKey:@"lng"] doubleValue]];

    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray<CLPlacemark *> * _Nullable placemarks, NSError * _Nullable error) {
        if (error != nil) {
            [self _sendCallbackErrorResult:[error localizedDescription] withCallbackId:command.callbackId];
            return;
        }

        NSMutableArray *jsonResult = [[NSMutableArray alloc] init];

        for (CLPlacemark * placemark in placemarks) {
            if (placemark.locality != nil && placemark.name != nil) {
                [jsonResult addObject:[[[AddressParser alloc] init] parse:placemark]];
            }
        }

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:jsonResult];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
