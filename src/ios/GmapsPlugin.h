#import <Cordova/CDVPlugin.h>

@interface GmapsPlugin : CDVPlugin

- (void)reverseGeocode:(CDVInvokedUrlCommand*)command;
- (void)geocode:(CDVInvokedUrlCommand*)command;

@end
