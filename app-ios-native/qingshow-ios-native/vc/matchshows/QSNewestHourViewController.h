//
//  QSNewestHourViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/25.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSNewestHourViewController : UIViewController

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

- (instancetype)initWithDate:(NSDate*)date;

@end
