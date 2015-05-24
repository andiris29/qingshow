//
//  QSS18TopShowOneDayViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS18TopShowOneDayViewController : UIViewController
@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

- (instancetype)initWithDate:(NSDate*)date;

@end
