@import CoreLocation;

@interface AddressParser : NSObject

-(NSDictionary*)parse:(CLPlacemark*) placemark;

@end