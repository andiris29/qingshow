//
//  QSS01MatchShowsViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSRootContentViewController.h"
#import <UIKit/UIKit.h>
#import "QSS12NewTradeExpectableViewController.h"
@interface QSS01MatchShowsViewController : QSRootContentViewController <QSS12NewTradeNotifyViewControllerDelegate>


@property (weak, nonatomic) IBOutlet UICollectionView* darenCollectionView;
@property (weak, nonatomic) IBOutlet UICollectionView* hotCollectionView;
@property (weak, nonatomic) IBOutlet UITableView* newestTableView;

@property (weak, nonatomic) IBOutlet UIButton *backToTopbtn;

@property (weak, nonatomic) IBOutlet UIView *segmentContainerView;



@end
