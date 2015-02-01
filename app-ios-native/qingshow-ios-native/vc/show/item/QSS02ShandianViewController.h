//
//  QSS02ShandianViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 2/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
//#import "QSItemImageListTableViewProvider.h"
#import "QSItemCollectionViewProvider.h"
@interface QSS02ShandianViewController : UIViewController <QSItemProviderDelegate>

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

@end
