//
//  QSS24SearchResultViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/12.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSMatchCollectionViewProvider.h"
@interface QSS24SearchResultViewController : UIViewController <QSMatchCollectionViewProviderDelegate>

- (instancetype)initWithCategory:(NSDictionary*)categoryDict;

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet UIButton *backToTopbtn;

- (IBAction)backToTopBtnPressed:(id)sender;
@end
