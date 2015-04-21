//
//  QSS13TopicDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 4/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSShowCollectionViewProvider.h"

@interface QSS13TopicDetailViewController : UIViewController <QSShowProviderDelegate>

- (IBAction)backBtnPressed:(id)sender;

@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *numberLabel;


@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstraint;

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

- (id)initWithTopic:(NSDictionary*)topicDict;

@end
