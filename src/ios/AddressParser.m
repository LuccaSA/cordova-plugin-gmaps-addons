#import "AddressParser.h"

@implementation AddressParser

-(NSDictionary*)parse:(CLPlacemark*)placemark {
    NSDictionary *point = @{
                            @"latitude": [NSNumber numberWithDouble:placemark.location.coordinate.latitude],
                            @"longitude": [NSNumber numberWithDouble:placemark.location.coordinate.longitude]
                            };

    NSDictionary *jsonResult = @{
                                 @"formattedAddress": [NSString stringWithFormat:@"%@, %@", placemark.name, placemark.locality],
                                 @"town": placemark.locality,
                                 @"countryName": placemark.country,
                                 @"countryIsoCode": placemark.ISOcountryCode,
                                 @"id": [NSString stringWithFormat:@"%f,%f", placemark.location.coordinate.latitude, placemark.location.coordinate.longitude],
                                 @"point": point
                                 };

    return jsonResult;
}

@end
