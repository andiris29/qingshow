//
//  QSItemImageTableViewCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QSSingleImageScrollView.h"

@protocol QSItemImageTableViewCellDelegate <NSObject>

- (void)didClickShopBtn:(UITableViewCell*)cell;

@end

@interface QSItemImageTableViewCell : UITableViewCell<QSImageScrollViewBaseDelegate>

- (void)bindWithItem:(NSDictionary*)itemDict;

@property (strong, nonatomic) IBOutlet UIView* infoContainerView;
@property (strong, nonatomic) IBOutlet UIView* imageContainerView;

//@property (strong, nonatomic) IBOutlet UILabel* saleLabel;
@property (strong, nonatomic) IBOutlet UILabel* nameLabel;
//@property (strong, nonatomic) IBOutlet UILabel* priceLabel;
@property (strong, nonatomic) IBOutlet UIButton* shopBtn;
- (IBAction)shopBtnPressed:(id)sender;
@property (weak, nonatomic) NSObject<QSItemImageTableViewCellDelegate>* delegate;

+ (CGFloat)getHeightWithItem:(NSDictionary*)itemDict;
@end
