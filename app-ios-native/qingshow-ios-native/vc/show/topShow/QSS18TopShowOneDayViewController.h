//
//  QSS18TopShowOneDayViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSS18WaterfallProvider.h"

@interface QSS18TopShowOneDayViewController : UIViewController<QSS18WaterfallProviderDelegate>
@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet UIButton *backToTopBtn;
- (IBAction)bakToTopBtnPressed:(id)sender;

- (instancetype)initWithDate:(NSDate*)date;

@end
