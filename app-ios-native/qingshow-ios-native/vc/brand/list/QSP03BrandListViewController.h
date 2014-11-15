//
//  QSBrandListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBrandCollectionViewDelegateObj.h"
@interface QSP03BrandListViewController : UIViewController <QSBrandCollectionViewDelegateObjDelegate>

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;


- (id)init;

@end
