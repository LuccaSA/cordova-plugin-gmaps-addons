#import <Cordova/CDVPlugin.h>

@interface GmapsPlugin : CDVPlugin

- (void)autocomplete:(CDVInvokedUrlCommand*)command;
- (void)geocode:(CDVInvokedUrlCommand*)command;
- (void)directions:(CDVInvokedUrlCommand*)command;

@end
