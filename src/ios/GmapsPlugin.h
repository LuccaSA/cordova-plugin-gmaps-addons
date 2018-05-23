#import <Cordova/CDVPlugin.h>

@interface GmapsPlugin : CDVPlugin

- (void)autocomplete:(CDVInvokedUrlCommand*)command;
- (void)reverseGeocode:(CDVInvokedUrlCommand*)command;
- (void)geocode:(CDVInvokedUrlCommand*)command;

@end
